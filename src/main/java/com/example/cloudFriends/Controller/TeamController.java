package com.example.cloudFriends.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.cloudFriends.Constant.ErrorCode;
import com.example.cloudFriends.Models.Result;
import com.example.cloudFriends.Models.dto.*;
import com.example.cloudFriends.Models.pojo.Team;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.vo.TeamUserVo;
import com.example.cloudFriends.Service.TeamService;
import com.example.cloudFriends.Service.UserService;
import com.example.cloudFriends.Utils.AliOSSUtils;
import com.example.cloudFriends.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.example.cloudFriends.Constant.UserConstant.USER_LOGIN_STATE;
import static com.example.cloudFriends.Utils.UpLoadAvatarUtil.getResult;

/**
 * @author CloudyW
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/team")
@CrossOrigin(origins = { "http://localhost:3000/", "http://localhost:80/" }, allowCredentials = "true")
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private AliOSSUtils aliOSSUtils;

    @PostMapping("/create")
    public Result createTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍信息为空");
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.createTeam(team, userService.getLoginUser(request));
        if (teamId == -1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍创建失败, 请联系管理人员");
        }
        return Result.success(teamId);
    }

    @PostMapping("/delete")
    public Result deleteTeam(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        User user = userService.getLoginUser(request);
        Boolean delete = teamService.deleteTeam(id, user);
        if (!delete) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍删除失败, 请联系管理人员");
        }
        return Result.success("队伍删除成功！");
    }

    @PostMapping("/update")
    public Result updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍信息为空");
        }
        User user = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamUpdateRequest, user);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍信息更新失败, 请联系管理人员");
        }
        return Result.success("队伍信息更新成功！");
    }

    @GetMapping("/get")
    public Result getTeamById(long teamId) {
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        TeamUserVo team = teamService.selectTeamById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未查询到队伍信息");
        }
        return Result.success(team);
    }

    @GetMapping("/search")
    public Result searchTeams(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String teamInfo,
            @RequestParam(required = false) Integer maxNum,
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date expireTime,
            @RequestParam Integer currentPage) {
        TeamSearchRequest teamSearchRequest = new TeamSearchRequest(id, teamInfo, maxNum, expireTime);
        teamSearchRequest.setPageNum(currentPage);
        IPage<TeamUserVo> teamUserVoIPage = teamService.searchTeams(teamSearchRequest);
        return Result.success(teamUserVoIPage);
    }

    @PostMapping("/getAvatar")
    public Result uploadAvatar(@RequestParam("image") MultipartFile avatar) {
        return getResult(avatar, aliOSSUtils, log);
    }

    @GetMapping("/getMyTeams")
    public Result getMyTeams(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "空请求");
        }
        User user = userService.getLoginUser(request);
        List<TeamUserVo> myTeams = teamService.getMyTeams(user);
        return Result.success(myTeams);
    }

    @PostMapping("/join")
    public Result joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍信息为空");
        }
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        Boolean joinTeam = teamService.joinTeam(teamJoinRequest, user);
        if (Boolean.FALSE.equals(joinTeam)) {
            throw new BusinessException(ErrorCode.DATA_BASE_ERROR, "加入队伍失败，请尝试重新加入或联系管理员");
        }
        return Result.success("加入队伍成功!");
    }

    @PostMapping("/quit")
    public Result quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍信息为空");
        }
        User user = userService.getLoginUser(request);
        Boolean quitTeam = teamService.quitTeam(teamQuitRequest, user);
        if (!quitTeam) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
        }
        return Result.success("退出队伍成功!");
    }
}
