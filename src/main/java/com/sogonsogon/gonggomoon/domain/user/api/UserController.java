package com.sogonsogon.gonggomoon.domain.user.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.user.api.dto.UserReadResponse;
import com.sogonsogon.gonggomoon.domain.user.application.UserService;
import com.sogonsogon.gonggomoon.domain.user.domain.User;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자", description = "사용자 정보 조회 및 회원 탈퇴 등 사용자 관련 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {this.userService = userService;}

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회한다.")
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserReadResponse>> getUserInfo(@AuthenticationPrincipal AccessUser user) {

        User findUser = userService.getUser(user.getId());
        return ResponseEntity.ok(BaseResponse.success(UserReadResponse.from(findUser)));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 탈퇴 처리한다.")
    @DeleteMapping("/me")
    public ResponseEntity<BaseResponse<Void>> withdrawUser(@AuthenticationPrincipal AccessUser user) {

        userService.withdrawUser(user.getId());

        return ResponseEntity.ok(BaseResponse.success(null));
    }
}
