package com.example.cloudFriends.Service;

import com.example.cloudFriends.Models.pojo.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.tools.Tag_Parent;

import java.util.List;

/**
* @author 86151
* @description 针对表【tag(标签)】的数据库操作Service
* @createDate 2024-03-26 14:36:11
*/
public interface TagService extends IService<Tag> {

    /**
     * 获得标签列表
     * @return
     */
    List<Tag_Parent> getTags();

    /**
     * 添加标签
     * @param user
     * @return
     */
    Boolean createTag(Tag tag, User user);
}
