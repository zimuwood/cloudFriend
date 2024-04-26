package com.example.cloudFriends.Utils;

/**
 * @author CloudyW
 * @version 1.0
 */
public class TagsToJSONUtil {
    public static String tagsToJSON(String tagsString) {
        String[] tags = tagsString.split(",");
        StringBuilder tagStr = new StringBuilder();
        tagStr.append("[");
        for (String tag : tags) {
            tagStr.append("\"").append(tag).append("\"").append(",");
        }
        tagStr.deleteCharAt(tagStr.lastIndexOf(","));
        tagStr.append("]");
        return new String(tagStr);
    }
}
