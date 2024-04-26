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
public class TeamSearchRequest extends PageRequest {

    /**
     * 队伍 id
     */
    private Long id;

    /**
     * 模糊搜索信息
     */
    private String teamInfo;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    public TeamSearchRequest(Long id, String teamInfo, Integer maxNum, Date expireTime) {
        this.id = id;
        this.teamInfo = teamInfo;
        this.maxNum = maxNum;
        this.expireTime = expireTime;
    }
}
