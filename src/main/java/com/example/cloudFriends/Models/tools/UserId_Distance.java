package com.example.cloudFriends.Models.tools;

import lombok.Data;

import java.util.Objects;

/**
 * @author CloudyW
 * @version 1.0
 */
@Data
public class UserId_Distance {

    private Long userId;

    private Long distance;

    public UserId_Distance(Long userId, Long distance) {
        this.userId = userId;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId_Distance that = (UserId_Distance) o;
        return userId.equals(that.userId) && distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, distance);
    }
}
