package com.thabiso.spring_ai;

import com.thabiso.spring_ai.service.ChatSessionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HelloController {

    private final ChatClient chatClient;
    private final ChatSessionService chatSessionService;

    public HelloController(ChatClient.Builder chatClient, ChatSessionService chatSessionService) {
        this.chatClient = chatClient.build();
        this.chatSessionService = chatSessionService;
    }

    @GetMapping("/chat/sessions")
    public ResponseEntity<List<String>> getChatSessions() {
        return ResponseEntity.ok(chatSessionService.getChatSessions());
    }

    @GetMapping("/chat/history/{sessionId}")
    public ResponseEntity<List<String>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatSessionService.getChatHistory(sessionId));
    }

    @PostMapping("/chat/{sessionId}/{chat}")
    public ResponseEntity<List<String>> sendChatMessage(
            @PathVariable String sessionId,
            @PathVariable String chat) {
        try {
            // Fetch the AI response
            String fullResponse = chatClient.prompt(chat).call().content();
            String response = fullResponse.contains("</think>") ? fullResponse.split("</think>", 2)[1].trim() : fullResponse;
            response = response.replaceAll("\n+", "\n\n").trim();

            // Store chat history in database
            return ResponseEntity.ok(chatSessionService.saveChatMessage(sessionId, chat, response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/chat/history/{sessionId}")
    public ResponseEntity<String> clearChatHistory(@PathVariable String sessionId) {
        chatSessionService.clearChatHistory(sessionId);
        return ResponseEntity.ok("Chat history cleared.");
    }
}
