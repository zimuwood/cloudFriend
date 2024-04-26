package com.example.cloudFriends.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.cloudFriends.Models.pojo.UserTeam;
import com.example.cloudFriends.Service.UserTeamService;
import com.example.cloudFriends.Mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 86151
* @description 针对表【user_team】的数据库操作Service实现
* @createDate 2024-04-10 14:19:34
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




