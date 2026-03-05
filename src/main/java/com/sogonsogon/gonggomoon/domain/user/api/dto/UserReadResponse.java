package com.sogonsogon.gonggomoon.domain.user.api.dto;

import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.domain.user.domain.UserRole;

public record UserReadResponse(
    String email,
    String name,
    String profileImageUrl,
    UserRole role
) {

    public static UserReadResponse from(User user) {
        return new UserReadResponse(
            user.getEmail(),
            user.getName(),
            user.getProfileImageUrl(),
            user.getRole()
        );
    }
}
