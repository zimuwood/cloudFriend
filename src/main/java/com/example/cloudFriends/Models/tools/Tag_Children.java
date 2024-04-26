package com.example.cloudFriends.Models.tools;

import lombok.Data;

import java.io.Serializable;

/**
 * @author CloudyW
 * @version 1.0
 */
@Data
public class Tag_Children implements Serializable {
    private static final long serialVersionUID = -2885254744659347651L;
    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String id;

    /**
     * 标签名称
     */
    private String text;

    /**
     * 父标签id
     */
    private Long parentId;

    public Tag_Children(Long tagId, String id, String text, Long parentId) {
        this.tagId = tagId;
        this.id = id;
        this.text = text;
        this.parentId = parentId;
    }
}
