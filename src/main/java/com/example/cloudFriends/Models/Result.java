package com.example.cloudFriends.Models;

import com.example.cloudFriends.Constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用返回类
 * @author CloudyW
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {

    private int code;
    private String message;
    private Object data;
    private String description;

    public static Result success() {
        return new Result(10000, "success", null, null);
    }

    public static Result success(Object data) {
        return new Result(10000, "success", data, null);
    }
    public static Result success(Object data, String description) {
        return new Result(10000, "success", data, description);
    }
    public static Result error(ErrorCode errorCode) {
        return new Result(errorCode.getCode(), errorCode.getMessage(), null, errorCode.getDescription());
    }

    public static Result error(ErrorCode errorCode, String description) {
        return new Result(errorCode.getCode(), errorCode.getMessage(), null, description);
    }

    public static Result error(int code, String msg, String description) {
        return new Result(code, msg, null, description);
    }


}
