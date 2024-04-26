package com.example.cloudFriends.Mapper;

import com.example.cloudFriends.Models.pojo.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86151
* @description 针对表【tag(标签)】的数据库操作Mapper
* @createDate 2024-03-26 14:36:11
* @Entity com.example.cloudFriends.Pojo.pojo.Tag
*/
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}




