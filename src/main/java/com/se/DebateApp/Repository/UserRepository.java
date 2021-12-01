package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
