package com.example.cloudFriends.Models.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author CloudyW
 * @version 1.0
 */
@Data
public class TeamJoinRequest {

    private Long teamId;

    /**
     * 入队密码
     */
    private String teamPassword;
}
