package com.example.board.module.user.controller;

import com.example.board.auth.session.MyUserDetails;
import com.example.board.module.common.exception.Exception400;
import com.example.board.module.common.exception.Exception403;
import com.example.board.module.user.UserModelAssembler;
import com.example.board.module.user.dto.UserModel;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserConst;
import com.example.board.module.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<UserModel>> getList(
            Pageable pageable,
            PagedResourcesAssembler<User> assembler
    ) {
        return ResponseEntity.ok(
                assembler.toModel(
                        userService.getList(pageable),
                        new UserModelAssembler()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUser(
        @AuthenticationPrincipal MyUserDetails myUserDetails,
        @PathVariable Long id) {

        Optional<User> optionalUser = userService.getUser(id);
        if (optionalUser.isEmpty()) {
            throw new Exception400(UserConst.notFound);
        }

        if (!myUserDetails.getUser().getId().equals(id)) {
            throw new Exception403(UserConst.forbidden);
        }

        return ResponseEntity.ok(new UserModelAssembler().toModel(optionalUser.get()));
    }
}
