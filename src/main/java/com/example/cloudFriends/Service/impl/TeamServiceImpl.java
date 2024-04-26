package com.example.cloudFriends.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.cloudFriends.Constant.ErrorCode;
import com.example.cloudFriends.Models.dto.TeamJoinRequest;
import com.example.cloudFriends.Models.dto.TeamQuitRequest;
import com.example.cloudFriends.Models.dto.TeamSearchRequest;
import com.example.cloudFriends.Models.dto.TeamUpdateRequest;
import com.example.cloudFriends.Constant.TeamStatusEnum;
import com.example.cloudFriends.Models.pojo.Team;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.pojo.UserTeam;
import com.example.cloudFriends.Models.vo.TeamUserVo;
import com.example.cloudFriends.Service.TeamService;
import com.example.cloudFriends.Mapper.TeamMapper;
import com.example.cloudFriends.Service.UserService;
import com.example.cloudFriends.Service.UserTeamService;
import com.example.cloudFriends.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.cloudFriends.Constant.TeamConstant.TEAM_DEFAULT_AVATAR;
import static com.example.cloudFriends.Constant.UserConstant.ADMIN_ROLE;

/**
* @author 86151
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-04-10 14:16:07
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "cloudyW";

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createTeam(Team team, User user) {
        RLock lock = redissonClient.getLock("cloudFriends:team:createTeam:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                if (team == null) {
                    throw new BusinessException(ErrorCode.NULL_ERROR);
                }
                // 判断登录状态
                if (user == null) {
                    throw new BusinessException(ErrorCode.NOT_LOGIN);
                }
                // 判断队伍最大人数
                int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
                if (maxNum < 1 || maxNum > 20) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍最大人数不满足要求");
                }
                // 判断队伍名称
                String teamName = team.getTeamName();
                if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不满足要求");
                }
                // 判断队伍简介
                String teamDescription = team.getTeamDescription();
                if (StringUtils.isNotBlank(teamDescription) && teamDescription.length() > 200) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍简介过长");
                }
                // 判断队伍状态
                int teamStatus = Optional.ofNullable(team.getTeamStatus()).orElse(0);
                TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
                if (statusEnum == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
                }
                // 判断密码设置
                if (TeamStatusEnum.ENCRYPTED.equals(statusEnum) ) {
                    String teamPassword = team.getTeamPassword();
                    if (StringUtils.isBlank(teamPassword) || teamPassword.length() > 12 || teamPassword.length() < 6) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不满足要求");
                    }
                    else {
                        team.setTeamPassword(DigestUtils.md5DigestAsHex((SALT + team.getTeamPassword()).getBytes()));
                    }
                }
                // 判断过期时间
                Date expireTime = team.getExpireTime();
                if (new Date().after(expireTime)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不满足要求");
                }
                // 判断创建队伍数量上限
                Long id = user.getId();
                long joinTeamNumber = userTeamService.count(new QueryWrapper<UserTeam>().eq("user_id", id));
                long joinLimit = user.getUserRole() == 0 ? 5 : 15;
                if (joinTeamNumber >= joinLimit) {
                    throw new BusinessException(ErrorCode.NO_AUTH, "加入或创建的队伍数量已达上限");
                }
                // 判断是否已经拥有同名队伍
                QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("captain_id", id);
                queryWrapper.eq("team_name", teamName);
                if (this.exists(queryWrapper)) {
                    throw new BusinessException(ErrorCode.NO_AUTH, "你已经拥有过同名的队伍");
                }

                team.setCaptainId(id);
                team.setFounderId(id);
                team.setCurrentNum(1);
                String teamAvatar = team.getTeamAvatar();
                team.setTeamAvatar(teamAvatar == null ? TEAM_DEFAULT_AVATAR : teamAvatar);
                boolean save = this.save(team);
                if (!save) {
                    throw new BusinessException(ErrorCode.DATA_BASE_ERROR, "数据库错误");
                }

                addUserTeam(team, user);

                return team.getId();
            }
        } catch (InterruptedException e) {
            log.error("createTeam error ", e);
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.getErrorCodeByCode(e.getCode()), e.getDescription());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return -1;
    }

    @Override
    public IPage<TeamUserVo> searchTeams(TeamSearchRequest teamSearchRequest) {
        int pageNum = teamSearchRequest.getPageNum();
        int pageSize = teamSearchRequest.getPageSize();
        Long id = teamSearchRequest.getId();
        String teamInfo = teamSearchRequest.getTeamInfo();
        Date expireTime = teamSearchRequest.getExpireTime();
        Integer maxNum = teamSearchRequest.getMaxNum();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            if (id <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id错误");
            }
            queryWrapper.eq("id", id);
        }
        else {
            if (maxNum != null) {
                if (maxNum < 1 || maxNum > 20) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍最大人数错误");
                }
                queryWrapper.eq("max_num", maxNum);
            }
            if (teamInfo != null) {
                queryWrapper.like("team_name", teamInfo).like("team_description", teamInfo);
            }
            queryWrapper.ge("expire_time", expireTime != null ? expireTime : new Date());
        }
        queryWrapper.notIn("team_status", TeamStatusEnum.PRIVATE.getValue());

        IPage<Team> teamIPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<TeamUserVo> teamUserVos = getLinkedUser(teamIPage.getRecords());
        IPage<TeamUserVo> teamUserVoPage = new Page<>(pageNum, pageSize);
        teamUserVoPage.setRecords(teamUserVos);
        return teamUserVoPage;
    }

    @Override
    public List<TeamUserVo> getMyTeams(User user) {
        List<UserTeam> userTeamList = userTeamService.list(new QueryWrapper<UserTeam>().eq("user_id", user.getId()));
        if (CollectionUtils.isEmpty(userTeamList)) {
            return new ArrayList<>();
        }
        List<Long> teamIds = new ArrayList<>();
        for (UserTeam userTeam : userTeamList) {
            teamIds.add(userTeam.getTeamId());
        }
        return getLinkedUser(this.list(new QueryWrapper<Team>().in("id", teamIds)));
    }

    @Override
    public TeamUserVo selectTeamById(long id) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        List<Team> teams = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teams)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "该队伍不存在或已被删除");
        }
        List<TeamUserVo> teamUserVos = getLinkedUser(teams);
        return teamUserVos.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTeam(long id, User user) {
        Team team = getTeamById(id);
        if (!team.getCaptainId().equals(user.getId()) && user.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限执行此操作");
        }
        return this.removeById(id) && userTeamService.remove(new QueryWrapper<UserTeam>().eq("team_id", id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User user) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        // 判断权限
        Long teamId = teamUpdateRequest.getId();
        Team team = getTeamById(teamId);
        if (!team.getCaptainId().equals(user.getId()) && user.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限执行此操作");
        }
        // 判断队伍名称
        boolean editFlag = false;
        String teamName = teamUpdateRequest.getTeamName();
        if (StringUtils.isBlank(teamName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不满足要求");

        } else if (!teamName.equals(team.getTeamName())) {
            team.setTeamName(teamName);
            editFlag = true;
        }
        // 判断队伍介绍
        String teamDescription = teamUpdateRequest.getTeamDescription();
        if (StringUtils.isBlank(teamDescription)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍介绍不满足要求");
        } else if (!teamDescription.equals(team.getTeamDescription())) {
            team.setTeamDescription(teamDescription);
            editFlag = true;
        }
        // 判断当前人数与修改的是否满足
        Integer maxNum = teamUpdateRequest.getMaxNum();
        if (maxNum == null || maxNum < team.getCurrentNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最大人数不满足要求");
        } else if (!maxNum.equals(team.getMaxNum())) {
            team.setMaxNum(maxNum);
            editFlag = true;
        }
        // 判断队伍过期时间
        Date expireTime = teamUpdateRequest.getExpireTime();
        if (expireTime == null || new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不满足要求");
        } else if (!expireTime.equals(team.getExpireTime())) {
            team.setExpireTime(expireTime);
            editFlag = true;
        }
        // 判断队伍权限和密码
        Integer teamStatus = teamUpdateRequest.getTeamStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        } else if (!teamStatus.equals(team.getTeamStatus())) {
            team.setTeamStatus(teamStatus);
            editFlag = true;
        }
        if (TeamStatusEnum.ENCRYPTED.equals(statusEnum)) {
            String teamPassword = teamUpdateRequest.getTeamPassword();
            if (StringUtils.isBlank(teamPassword) || teamPassword.length() > 12 || teamPassword.length() < 6) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不满足要求");
            }
            else if (!teamPassword.equals(team.getTeamPassword())) {
                team.setTeamPassword(DigestUtils.md5DigestAsHex((SALT + teamPassword).getBytes()));
                editFlag = true;
            }
        }

        if (editFlag) {
            return this.updateById(team);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean joinTeam(TeamJoinRequest teamJoinRequest, User user) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        Long userId = user.getId();

        RLock lock = redissonClient.getLock("cloudFriends:team:" + userId + "joinTeam" + teamId + ":lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                // 判断队伍是否存在
                Team team = getTeamById(teamId);
                // 队伍是否过期
                Date expireTime = team.getExpireTime();
                if (expireTime != null && expireTime.before(new Date())) {
                    throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
                }
                // 判断队伍人数是否已满
                Integer currentNum = team.getCurrentNum();
                if (currentNum >= team.getMaxNum()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满");
                }
                // 判断加入队伍数量是否达到上限
                long joinTeamNumber = userTeamService.count(new QueryWrapper<UserTeam>().eq("user_id", userId));
                long joinLimit = user.getUserRole() == 0 ? 5 : 15;
                if (joinTeamNumber >= joinLimit) {
                    throw new BusinessException(ErrorCode.NO_AUTH, "加入的队伍数量已达上限");
                }
                // 判断权限和密码
                Integer teamStatus = team.getTeamStatus();
                TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
                if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
                    throw new BusinessException(ErrorCode.NO_AUTH, "无权限加入私有队伍");
                } else if (TeamStatusEnum.ENCRYPTED.equals(teamStatusEnum)) {
                    String teamPassword = teamJoinRequest.getTeamPassword();
                    if (StringUtils.isBlank(teamPassword)) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不匹配");
                    }
                    String password = DigestUtils.md5DigestAsHex((SALT + teamPassword).getBytes());
                    if (!password.equals(team.getTeamPassword())) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不匹配");
                    }
                }
                // 判断重复加入队伍
                if (userTeamService.exists(new QueryWrapper<UserTeam>().eq("user_id", userId).eq("team_id", teamId))) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入队伍");
                }

                team.setCurrentNum(currentNum + 1);
                boolean update = this.updateById(team);
                addUserTeam(team, user);
                return update;
            }
        } catch (InterruptedException e) {
            log.error("joinTeam error ", e);
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.getErrorCodeByCode(e.getCode()), e.getDescription());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean quitTeam(TeamQuitRequest teamQuitRequest, User user) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);

        Long userId = user.getId();
        Integer count = team.getCurrentNum();
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入该队伍");
        }
        else if (count == 1) {
            boolean removeUserTeam = userTeamService.remove(new QueryWrapper<UserTeam>().eq("team_id", teamId));
            if (!removeUserTeam) {
                throw new BusinessException(ErrorCode.DATA_BASE_ERROR);
            }
            boolean removeTeam = this.removeById(teamId);
            if (!removeTeam) {
                throw new BusinessException(ErrorCode.DATA_BASE_ERROR);
            }
        }
        else {
            // 如果是队长应该转移权限
            if (team.getCaptainId().equals(userId)) {
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("team_id", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() > 2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                team.setCaptainId(nextTeamLeaderId);
            }
            // 删除入队信息以及队伍人数 - 1
            team.setCurrentNum(count - 1);
            boolean update = this.updateById(team);
            if (!update) {
                throw new BusinessException(ErrorCode.DATA_BASE_ERROR);
            }
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("team_id", teamId);
            userTeamQueryWrapper.eq("user_id", userId);
            boolean remove = userTeamService.remove(userTeamQueryWrapper);
            if (!remove) {
                throw new BusinessException(ErrorCode.DATA_BASE_ERROR);
            }
        }
        return true;
    }


    /**
     * 添加入队记录
     * @param team
     * @param user
     */
    private void addUserTeam(Team team, User user) {
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(user.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        userTeam.setCreateTime(new Date());
        boolean save = userTeamService.save(userTeam);
        if (!save) {
            throw new BusinessException(ErrorCode.DATA_BASE_ERROR, "数据库错误");
        }
    }

    /**
     * 获取队伍用户信息
     * @param teams
     * @return
     */
    private List<TeamUserVo> getLinkedUser(List<Team> teams) {
        List<TeamUserVo> teamUserVos = new ArrayList<>();
        // 关联查询用户信息
        for (Team team : teams) {
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            List<User> users = new ArrayList<>();
            users.add(userService.getSafetyUser(userService.getOne(new QueryWrapper<User>().eq("id", teamUserVo.getCaptainId()))));
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("team_id", teamUserVo.getId()).notIn("user_id", teamUserVo.getCaptainId());
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            for (UserTeam userTeam : userTeamList) {
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("id", userTeam.getUserId());
                User user = userService.getSafetyUser(userService.getOne(userQueryWrapper));
                users.add(user);
            }
            teamUserVo.setUserList(users);
            teamUserVos.add(teamUserVo);
        }
        return teamUserVos;
    }

    /**
     * 通过id获得队伍
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍Id不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在或已被删除");
        }
        return team;
    }
}




