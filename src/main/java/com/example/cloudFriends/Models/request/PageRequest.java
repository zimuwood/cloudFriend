package com.example.cloudFriends.Models.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求
 * @author CloudyW
 * @version 1.0
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 6856758172107192987L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前页面
     */
    protected int pageNum = 1;
}
