package com.thabiso.spring_ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HelloController {
    private final ChatClient chatClient;
    private final Map<String, List<String>> chatHistory = new HashMap<>();

    public HelloController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    // Fetch all chat session IDs
    @GetMapping("/chat/sessions")
    public ResponseEntity<Set<String>> getChatSessions() {
        return ResponseEntity.ok(chatHistory.keySet());
    }

    // Fetch chat history for a session
    @GetMapping("/chat/history/{sessionId}")
    public ResponseEntity<List<String>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatHistory.getOrDefault(sessionId, new ArrayList<>()));
    }

    // Send a message & get AI response
    @PostMapping("/chat/{sessionId}/{chat}")
    public ResponseEntity<List<String>> sendChatMessage(
            @PathVariable String sessionId,
            @PathVariable String chat) {
        try {
            String response = chatClient.prompt(chat).call().content();

            // Store chat history
            chatHistory.putIfAbsent(sessionId, new ArrayList<>());
            chatHistory.get(sessionId).add("User: " + chat);
            chatHistory.get(sessionId).add("AI: " + response);

            return ResponseEntity.ok(chatHistory.get(sessionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonList("Error: " + e.getMessage()));
        }
    }

    // Clear chat history
    @DeleteMapping("/chat/history/{sessionId}")
    public ResponseEntity<String> clearChatHistory(@PathVariable String sessionId) {
        chatHistory.remove(sessionId);
        return ResponseEntity.ok("Chat history cleared.");
    }
}
