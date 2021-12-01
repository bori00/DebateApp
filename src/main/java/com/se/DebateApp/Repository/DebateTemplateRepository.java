package com.se.DebateApp.Repository;

import com.se.DebateApp.Model.DebateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebateTemplateRepository extends JpaRepository<DebateTemplate, Long> {
}
