package com.example.cloudFriends.Models.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {

    private static final long serialVersionUID = 8409237779393440747L;

    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 队伍创建人id
     */
    private Long founderId;

    /**
     * 队长id
     */
    private Long captainId;

    /**
     * 队伍状态 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer teamStatus;

    /**
     * 入队密码
     */
    private String teamPassword;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 当前队伍人数
     */
    private Integer currentNum;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}