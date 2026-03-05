package com.sogonsogon.gonggomoon.domain.user.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.user.api.dto.UserReadResponse;
import com.sogonsogon.gonggomoon.domain.user.application.UserService;
import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {this.userService = userService;}

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserReadResponse>> getUserInfo(@AuthenticationPrincipal AccessUser user) {

        User findUser = userService.getUser(user.getId());
        return ResponseEntity.ok(BaseResponse.success(UserReadResponse.from(findUser)));
    }
}
