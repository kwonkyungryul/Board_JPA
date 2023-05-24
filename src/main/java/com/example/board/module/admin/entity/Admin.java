package com.example.board.module.admin.entity;

import com.example.board.module.admin.enums.AdminStatus;
import com.example.board.module.common.jpa.BaseTime;
import com.example.board.module.common.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ADMIN_LIST")
public class Admin extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("고유번호")
    private Long id;

    @Comment("관리자 아이디")
    private String username;

    @Comment("관리자 비밀번호")
    private String password;

    @Comment("권한")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Comment("관리자 상태")
    @Enumerated(EnumType.STRING)
    private AdminStatus status;

    public Admin(Long id, String username, String password, RoleType role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = AdminStatus.ACTIVE;
    }
}
