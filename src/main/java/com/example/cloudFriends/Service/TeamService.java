package com.example.cloudFriends.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.cloudFriends.Models.dto.*;
import com.example.cloudFriends.Models.pojo.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.vo.TeamUserVo;

import java.util.List;

/**
* @author 86151
* @description 针对表【team】的数据库操作Service
* @createDate 2024-04-10 14:16:07
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @param user
     * @return
     */
    long createTeam(Team team, User user);

    /**
     * 搜索队伍
     * @param teamSearchRequest
     * @return
     */
    IPage<TeamUserVo> searchTeams(TeamSearchRequest teamSearchRequest);

    /**
     * 获得我加入或创建的队伍
     * @param user
     * @return
     */
    List<TeamUserVo> getMyTeams(User user);

    /**
     * 根据id获得队伍信息
     * @param teamId
     * @return
     */
    TeamUserVo selectTeamById(long teamId);

    /**
     * 删除队伍
     * @param id
     * @param user
     * @return
     */
    Boolean deleteTeam(long id, User user);

    /**
     * 更新队伍信息
     * @param teamUpdateRequest
     * @return
     */
    Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User user);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param user
     * @return
     */
    Boolean joinTeam(TeamJoinRequest teamJoinRequest, User user);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param user
     */
    Boolean quitTeam(TeamQuitRequest teamQuitRequest, User user);

}
