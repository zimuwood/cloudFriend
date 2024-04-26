package com.example.cloudFriends.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.cloudFriends.Constant.ErrorCode;
import com.example.cloudFriends.Models.pojo.Tag;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.tools.Tag_Children;
import com.example.cloudFriends.Models.tools.Tag_Parent;
import com.example.cloudFriends.Service.TagService;
import com.example.cloudFriends.Mapper.TagMapper;
import com.example.cloudFriends.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 86151
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-03-26 14:36:11
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<Tag_Parent> getTags() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        String redisKey = String.format("cloudFriends:tag:getTags");
        List<Tag_Parent> parentTags = (List<Tag_Parent>) valueOperations.get(redisKey);

        if (parentTags != null) {
            return parentTags;
        }

        // 无缓存，查询并放入 redis 中
        HashMap<Long, Tag_Parent> parentTagHashMap = new HashMap<>();
        List<Tag_Children> childrenTags = new ArrayList<>();

        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.select("id", "tag_name", "is_parent", "parent_id");
        List<Tag> tagList = this.list(tagQueryWrapper);
        tagList.forEach(tag -> {
            Long tagId = tag.getId();
            String tagName = tag.getTagName();
            if (tag.getIsParent() == 1) {
                parentTagHashMap.put(tagId, new Tag_Parent(tagId, tagName, new ArrayList<>()));
            }
            else {
                childrenTags.add(new Tag_Children(tagId, tagName, tagName, tag.getParentId()));
            }
        });

        if (CollectionUtils.isEmpty(childrenTags) || MapUtils.isEmpty(parentTagHashMap)) {
            throw new BusinessException(ErrorCode.DATA_BASE_ERROR, "查询标签失败");
        }

        childrenTags.forEach(tagChildren -> {
            Long parentId = tagChildren.getParentId();
            Tag_Parent tagParent = parentTagHashMap.get(parentId);
            if (tagParent == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            List<Tag_Children> parentChildren = tagParent.getChildren();
            if (CollectionUtils.isEmpty(parentChildren)) {
                parentChildren = new ArrayList<>();
            }
            parentChildren.add(tagChildren);
            tagParent.setChildren(parentChildren);
            parentTagHashMap.put(parentId, tagParent);
        });

        parentTags = new ArrayList<>(parentTagHashMap.values());

        try {
            valueOperations.set(redisKey, parentTags, 1800000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }

        return parentTags;
    }

    @Override
    public Boolean createTag(Tag tag, User user) {
        if (tag == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "标签信息为空");
        }
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }

        tag.setUserId(user.getId());
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        tag.setIsDelete(0);

        return this.save(tag);
    }
}




