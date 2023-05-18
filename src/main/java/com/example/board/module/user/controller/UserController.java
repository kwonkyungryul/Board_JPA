package com.example.board.module.user.controller;

import com.example.board.auth.session.MyUserDetails;
import com.example.board.module.common.exception.Exception400;
import com.example.board.module.user.dto.UserDTO;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserConst;
import com.example.board.module.user.request.UserSaveRequest;
import com.example.board.module.user.response.UserResponse;
import com.example.board.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<UserDTO> getUser(
        @AuthenticationPrincipal MyUserDetails myUserDetails
    ) {
        Optional<User> optionalUser = userService.getUser(myUserDetails);
        if (optionalUser.isEmpty()) {
            throw new Exception400(UserConst.notFound);
        }
        return ResponseEntity.ok(optionalUser.get().toDTO());
    }

    @PostMapping
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
