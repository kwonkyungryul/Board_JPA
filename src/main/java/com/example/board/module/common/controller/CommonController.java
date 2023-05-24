package com.example.board.module.common.controller;

import com.example.board.auth.jwt.MyJwtProvider;
import com.example.board.module.common.exception.Exception400;
import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.common.service.CommonService;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserConst;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.request.UserSaveRequest;
import com.example.board.module.user.response.UserResponse;
import com.example.board.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class CommonController {

    private final CommonService commonService;

    private final UserService userService;


    public CommonController(CommonService commonService, UserService userService) {
        this.commonService  = commonService;
        this.userService = userService;
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw new Exception400(errors.getAllErrors().get(0).getDefaultMessage());
        }

        Optional<User> optionalUser = commonService.findUser(request);

        if (optionalUser.isEmpty()) {
            throw new Exception400("ID 혹은 Password가 잘못되었습니다.");
        }

        User user = optionalUser.get();

        if (user.getStatus().equals(UserStatus.INACTIVE)) {
            throw new Exception400(UserConst.statusAccessDenied);
        }

        if (!user.getPassword().equals(request.password())) {
            throw new Exception400("ID 혹은 Password가 잘못되었습니다.");
        }

        String jwt = commonService.getToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add(MyJwtProvider.HEADER, jwt);
        headers.add("Content-Type", "application/json;charset=utf-8");
        Map<String, String> map = new HashMap<>();
        map.put("message", "로그인이 완료되었습니다.");

        return new ResponseEntity<>(map, headers, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> saveUser(
            @Valid @RequestBody UserSaveRequest request,
            Errors error
    ) {
        if (error.hasErrors()) {
            throw new Exception400(error.getAllErrors().get(0).getDefaultMessage());
        }

        User user = userService.save(request);

        return ResponseEntity.ok(user.toResponse());
    }
}
