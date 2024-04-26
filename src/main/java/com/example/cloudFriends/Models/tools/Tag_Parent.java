package com.example.cloudFriends.Models.tools;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author CloudyW
 * @version 1.0
 */
@Data
public class Tag_Parent implements Serializable {
    private static final long serialVersionUID = -7797525106564914413L;
    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String text;

    /**
     * 子标签链表
     */
    List<Tag_Children> children;

    public Tag_Parent(Long tagId, String text, List<Tag_Children> children) {
        this.tagId = tagId;
        this.text = text;
        this.children = children;
    }
}
