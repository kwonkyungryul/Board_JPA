package com.example.board.consts;

import com.example.board.module.admin.entity.Admin;
import com.example.board.module.common.enums.RoleType;

public interface AdminConst {
    Admin admin = new Admin(1L, "admin", "1234", RoleType.ADMIN);
}
