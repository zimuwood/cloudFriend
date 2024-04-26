package com.example.cloudFriends.Models.vo;

import com.example.cloudFriends.Models.pojo.User;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 队伍和用户信息
 * @author CloudyW
 * @version 1.0
 */
@Data
public class TeamUserVo implements Serializable {

    private static final long serialVersionUID = -48461828335079685L;

    private long id;

    /**
     * 队伍头像
     */
    private String teamAvatar;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 队长id
     */
    private Long captainId;

    /**
     * 队伍状态 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer teamStatus;

    /**
     * 当前队伍人数
     */
    private Integer currentNum;

    /**
     * 队伍成员列表(注意信息脱敏)
     */
    List<User> userList = new ArrayList<>();
}
