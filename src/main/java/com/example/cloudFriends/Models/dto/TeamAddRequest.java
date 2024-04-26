package com.example.cloudFriends.Models.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author CloudyW
 * @version 1.0
 */
@Data
public class TeamAddRequest {
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
     * 队伍状态 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer teamStatus;

    /**
     * 入队密码
     */
    private String teamPassword;
}
