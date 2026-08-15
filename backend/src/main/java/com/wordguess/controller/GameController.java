package com.wordguess.controller;

import com.wordguess.dto.GuessRequest;
import com.wordguess.dto.GuessResultDto;
import com.wordguess.model.GameSession;
import com.wordguess.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;
    private final com.wordguess.service.LlmValidationService llmValidationService;

    @Autowired
    public GameController(GameService gameService, com.wordguess.service.LlmValidationService llmValidationService) {
        this.gameService = gameService;
        this.llmValidationService = llmValidationService;
    }

    @GetMapping("/validate-word")
    public ResponseEntity<?> validateWord(@RequestParam String word) {
        boolean valid = llmValidationService.isValidWord(word);
        Map<String, Object> resp = new HashMap<>();
        resp.put("word", word.toUpperCase());
        resp.put("valid", valid);
        resp.put("message", valid ? "Valid 5-letter English word" : "Invalid English word according to LLM validation");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startNewGame(@RequestParam String userId) {
        try {
            GameSession session = gameService.startNewGame(userId);
            long gamesPlayedToday = gameService.getDailyGamesPlayed(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("status", session.getStatus());
            response.put("attempts", session.getAttempts());
            response.put("gamesPlayedToday", gamesPlayedToday);
            response.put("remainingGamesToday", 3 - gamesPlayedToday);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(err);
        }
    }

    @PostMapping("/guess")
    public ResponseEntity<?> submitGuess(@Valid @RequestBody GuessRequest request) {
        try {
            GuessResultDto result = gameService.submitGuess(request);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getGameStatus(@RequestParam String userId) {
        long playedToday = gameService.getDailyGamesPlayed(userId);
        var activeSessionOpt = gameService.getActiveSession(userId);

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("gamesPlayedToday", playedToday);
        statusMap.put("remainingGamesToday", Math.max(0, 3 - playedToday));
        statusMap.put("hasActiveSession", activeSessionOpt.isPresent());

        activeSessionOpt.ifPresent(session -> {
            statusMap.put("activeSessionId", session.getId());
            statusMap.put("attempts", session.getAttempts());
        });

        return ResponseEntity.ok(statusMap);
    }
}
