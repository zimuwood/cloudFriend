package com.example.cloudFriends.Models.dto;

import com.example.cloudFriends.Models.request.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * 队伍查询封装类
 * @author CloudyW
 * @version 1.0
 */
@Data
public class TeamQuery extends PageRequest {

    private Long id;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamDescription;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队长id
     */
    private Long captainId;

    /**
     * 队伍状态 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer teamStatus;
}
