package com.example.board.module.common.controller;

import com.example.board.auth.jwt.MyJwtProvider;
import com.example.board.module.common.exception.Exception400;
import com.example.board.module.common.request.LoginRequest;
import com.example.board.module.common.service.CommonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    private final CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            Errors errors
    ) {
        if (errors.hasErrors()) {
            throw new Exception400(errors.getAllErrors().get(0).getDefaultMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        String jwt = commonService.login(request);
        headers.add(MyJwtProvider.HEADER, jwt);
        headers.add("Content-Type", "application/json;charset=utf-8");
        return new ResponseEntity<>("성공", headers, HttpStatus.OK);
    }
}
