package com.thabiso.spring_ai.repository;

import com.thabiso.spring_ai.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}
