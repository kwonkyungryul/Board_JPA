package com.example.board.jpa.user;

import com.example.board.module.common.jpa.RoleType;
import com.example.board.module.user.entity.User;
import com.example.board.module.user.enums.UserStatus;
import com.example.board.module.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void init() {
        setUp("derek", "1234", "derek@nate.com", RoleType.USER, UserStatus.ACTIVE);
    }

    @Test
    void selectAll() {
        var users = this.userRepository.findAll();
        Assertions.assertNotEquals(users.size(), 0);

        User user = users.get(0);
        Assertions.assertEquals(user.getId(), 1L);
        Assertions.assertEquals(user.getUsername(), "kkr");
        Assertions.assertEquals(user.getPassword(), "1234");
        Assertions.assertEquals(user.getEmail(), "kkr@nate.com");
        Assertions.assertEquals(user.getRole(), RoleType.USER);
        Assertions.assertEquals(user.getStatus(), UserStatus.ACTIVE);
    }

    @Test
    @Transactional
    void selectAndUpdate() {
        Long id = 1L;
        var users = this.userRepository.findById(id);

        if (users.isPresent()) {
            var result = users.get();
            Assertions.assertEquals(result.getId(), 1L);
            Assertions.assertEquals(result.getUsername(), "kkr");
            Assertions.assertEquals(result.getPassword(), "1234");
            Assertions.assertEquals(result.getEmail(), "kkr@nate.com");
            Assertions.assertEquals(result.getRole(), RoleType.USER);
            Assertions.assertEquals(result.getStatus(), UserStatus.ACTIVE);

            var content = "5678";
            result.setPassword(content);
            User merge = entityManager.merge(result);

            Assertions.assertEquals(merge.getId(), 1L);
            Assertions.assertEquals(merge.getUsername(), "kkr");
            Assertions.assertEquals(merge.getPassword(), "5678");
            Assertions.assertEquals(merge.getEmail(), "kkr@nate.com");
            Assertions.assertEquals(merge.getRole(), RoleType.USER);
            Assertions.assertEquals(merge.getStatus(), UserStatus.ACTIVE);
        } else {
            Assertions.assertNotNull(users.get());
        }
    }

    @Test
    @Transactional
    void insertAndDelete() {
        var user = setUp("bob", "1234", "bob@nate.com", RoleType.USER, UserStatus.ACTIVE);
        Optional<User> findUser = this.userRepository.findById(user.getId());
        if (findUser.isPresent()) {
            var result = findUser.get();
            Assertions.assertEquals(result.getId(), 7L);
            Assertions.assertEquals(result.getUsername(), "bob");
            Assertions.assertEquals(result.getPassword(), "1234");
            Assertions.assertEquals(result.getEmail(), "bob@nate.com");
            Assertions.assertEquals(result.getRole(), RoleType.USER);
            Assertions.assertEquals(result.getStatus(), UserStatus.ACTIVE);

            this.entityManager.remove(result);
            Optional<User> deleteUser = this.userRepository.findById(result.getId());
            deleteUser.ifPresent(Assertions::assertNull);
        } else {
            Assertions.assertNotNull(findUser.get());
        }
    }

    private User setUp(String username, String password, String email, RoleType role, UserStatus status) {
        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .status(status)
                .build();
        return this.entityManager.persist(user);
    }
}
