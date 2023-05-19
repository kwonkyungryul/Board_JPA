package com.example.board.consts;

import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;

public interface UserConst {
    User user = new com.example.board.module.user.entity.User(1L, "kkr", "1234", "kkr@nate.com", RoleType.USER, UserStatus.ACTIVE);
}
