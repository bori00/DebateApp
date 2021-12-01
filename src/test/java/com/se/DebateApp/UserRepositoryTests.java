package com.se.DebateApp;

import static org.assertj.core.api.Assertions.assertThat;

import com.se.DebateApp.Model.User;
import com.se.DebateApp.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUserName("user111");
        user.setPassword("pass111");

        User savedUser = repository.save(user);

        User existUser = entityManager.find(User.class, savedUser.getId());

        assertThat(existUser.getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    public void findUserByUserName_ExistingUser() {
        User user = new User();
        String userName = "usernametest";
        user.setUserName(userName);
        user.setPassword("passtest");

        repository.save(user);

        User foundUser = repository.findByUserName(userName);

        assertThat(foundUser).isNotNull();
    }

    @Test
    public void findUserByUserName_NonExistingUser() {

        User foundUser = repository.findByUserName("missingname");

        assertThat(foundUser).isNull();
    }
}
