package com.example.cloudFriends.Mapper;

import com.example.cloudFriends.Models.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86151
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-03-26 14:42:58
* @Entity com.example.cloudFriends.Pojo.po.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




