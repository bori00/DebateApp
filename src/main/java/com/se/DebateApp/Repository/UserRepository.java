package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("Select u FROM User u WHERE u.userName = ?1")
    User findByUserName(String userName);
}