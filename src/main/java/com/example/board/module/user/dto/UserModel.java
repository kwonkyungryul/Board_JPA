package com.example.board.module.user.dto;

import com.example.board.module.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.format.DateTimeFormatter;

@Relation(value = "user", collectionRelation = "users")
@Getter
@Setter
public class UserModel extends RepresentationModel<UserModel> {
    Long id;
    String username;
    String email;
    String createDate;

    public UserModel(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        if (user.getCreatedDate() == null ) {
            user.changeCreatedDate(null);
        }
        this.createDate = user.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}