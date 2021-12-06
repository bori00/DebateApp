package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.DebateTemplate;
import com.se.DebateApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebateTemplateRepository extends JpaRepository<DebateTemplate, Long> {
    @Query("Select dt FROM DebateTemplate dt WHERE ?1 = dt.owner")
    List<DebateTemplate> findAllDebateTemplatesOfUser(User user);
}
