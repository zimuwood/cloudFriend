package com.example.cloudFriends.Utils;

import com.example.cloudFriends.Constant.ErrorCode;
import com.example.cloudFriends.Models.Result;
import com.example.cloudFriends.exception.BusinessException;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author CloudyW
 * @version 1.0
 */
public class UpLoadAvatarUtil {
    public static Result getResult(MultipartFile avatar, AliOSSUtils aliOSSUtils, Logger log) {
        if (avatar == null) {
            return Result.error(ErrorCode.NULL_ERROR, "请求发生错误");
        }
        String avatarURL;
        try {
            avatarURL = aliOSSUtils.upload(avatar);
        } catch (Exception e) {
            log.error("头像上传错误");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像上传失败");
        }
        return Result.success(avatarURL);
    }
}
