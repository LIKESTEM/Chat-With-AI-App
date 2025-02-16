package com.thabiso.spring_ai.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sessionId;

    @ElementCollection
    private List<String> messages = new ArrayList<>();

    public ChatSession() {}

    public ChatSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
