package com.example.board.consts;

import com.example.board.module.common.enums.RoleType;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import org.springframework.data.domain.PageRequest;

public interface UserConst {
    User user = new User(1L, "kkr", "1234", "kkr@nate.com", RoleType.USER, UserStatus.ACTIVE);

    PageRequest pageRequest = PageRequest.of(1, 10);
}
