package com.thabiso.spring_ai.service;

import com.thabiso.spring_ai.model.ChatSession;
import com.thabiso.spring_ai.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatSessionService(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public List<String> getChatSessions() {
        return chatSessionRepository.findAll().stream()
                .map(ChatSession::getSessionId)
                .toList();
    }

    public List<String> getChatHistory(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .map(ChatSession::getMessages)
                .orElse(List.of());
    }

    public List<String> saveChatMessage(String sessionId, String chat, String response) {
        ChatSession chatSession = chatSessionRepository.findBySessionId(sessionId)
                .orElse(new ChatSession(sessionId));

        chatSession.addMessage("User: " + chat);
        chatSession.addMessage("LIKESTEM AI: " + response);
        chatSessionRepository.save(chatSession);

        return chatSession.getMessages();
    }

    public void clearChatHistory(String sessionId) {
        chatSessionRepository.deleteBySessionId(sessionId);
    }
}
