package com.wordguess.service;

import com.wordguess.dto.GuessRequest;
import com.wordguess.dto.GuessResultDto;
import com.wordguess.model.*;
import com.wordguess.repository.GameSessionRepository;
import com.wordguess.repository.UserRepository;
import com.wordguess.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class GameService {

    private final WordRepository wordRepository;
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final LlmValidationService llmValidationService;

    @Autowired
    public GameService(WordRepository wordRepository,
                       GameSessionRepository gameSessionRepository,
                       UserRepository userRepository,
                       LlmValidationService llmValidationService) {
        this.wordRepository = wordRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
        this.llmValidationService = llmValidationService;
    }

    public GameSession startNewGame(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();

        // Rule: Don't allow more than 3 words to guess in a day for a user
        long todayCount = gameSessionRepository.countByUserIdAndPlayDate(userId, today);
        if (todayCount >= 3) {
            throw new IllegalStateException("Daily limit reached! You cannot play more than 3 words per day.");
        }

        // Pick one word randomly from database
        List<Word> allWords = wordRepository.findAll();
        if (allWords.isEmpty()) {
            throw new IllegalStateException("No words available in the database. Please seed words first.");
        }

        Random random = new Random();
        Word selectedWord = allWords.get(random.nextInt(allWords.size()));

        GameSession session = new GameSession(userId, user.getUsername(), selectedWord.getWord(), today);
        return gameSessionRepository.save(session);
    }

    public GuessResultDto submitGuess(GuessRequest request) {
        GameSession session = gameSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Game session not found"));

        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new IllegalStateException("Game session is already finished.");
        }

        if (session.getAttempts().size() >= 5) {
            throw new IllegalStateException("Maximum 5 guesses allowed per word game.");
        }

        String guessedWord = request.getGuessedWord().toUpperCase();
        if (guessedWord.length() != 5) {
            throw new IllegalArgumentException("Guessed word must be exactly 5 uppercase letters.");
        }

        // Validate word using LLM API (Google Gemini / Fallback dictionary)
        if (!llmValidationService.isValidWord(guessedWord)) {
            throw new IllegalArgumentException("INVALID_WORD: '" + guessedWord + "' is not a valid 5-letter English word according to LLM validation.");
        }

        String targetWord = session.getTargetWord();
        List<LetterStatus> feedback = evaluateGuess(targetWord, guessedWord);

        GuessAttempt attempt = new GuessAttempt(guessedWord, feedback);
        session.getAttempts().add(attempt);

        long playedToday = getDailyGamesPlayed(session.getUserId());
        long remainingToday = Math.max(0, 3 - playedToday);

        GuessResultDto result = new GuessResultDto();
        result.setSessionId(session.getId());
        result.setAttemptNumber(session.getAttempts().size());
        result.setRemainingAttempts(5 - session.getAttempts().size());
        result.setPreviousAttempts(session.getAttempts());
        result.setGamesPlayedToday(playedToday);
        result.setRemainingGamesToday(remainingToday);

        if (guessedWord.equals(targetWord)) {
            session.setStatus("WON");
            result.setStatus("WON");
            result.setMessage("Congratulations! You guessed the word correctly!");
            result.setTargetWord(targetWord);
        } else if (session.getAttempts().size() >= 5) {
            session.setStatus("LOST");
            result.setStatus("LOST");
            result.setMessage("Better luck next time!");
            result.setTargetWord(targetWord);
        } else {
            result.setStatus("IN_PROGRESS");
            result.setMessage("Keep guessing!");
        }

        gameSessionRepository.save(session);
        return result;
    }

    public List<LetterStatus> evaluateGuess(String targetWord, String guessedWord) {
        LetterStatus[] feedback = new LetterStatus[5];
        char[] targetChars = targetWord.toCharArray();
        char[] guessChars = guessedWord.toCharArray();
        boolean[] targetMatched = new boolean[5];

        // First pass: GREEN (Correct letter & correct position)
        for (int i = 0; i < 5; i++) {
            if (guessChars[i] == targetChars[i]) {
                feedback[i] = LetterStatus.GREEN;
                targetMatched[i] = true;
            }
        }

        // Second pass: ORANGE (Correct letter & wrong position) or GREY
        for (int i = 0; i < 5; i++) {
            if (feedback[i] != null) continue;

            boolean foundOrange = false;
            for (int j = 0; j < 5; j++) {
                if (!targetMatched[j] && guessChars[i] == targetChars[j]) {
                    targetMatched[j] = true;
                    foundOrange = true;
                    break;
                }
            }

            if (foundOrange) {
                feedback[i] = LetterStatus.ORANGE;
            } else {
                feedback[i] = LetterStatus.GREY;
            }
        }

        return Arrays.asList(feedback);
    }

    public long getDailyGamesPlayed(String userId) {
        return gameSessionRepository.countByUserIdAndPlayDate(userId, LocalDate.now());
    }

    public Optional<GameSession> getActiveSession(String userId) {
        List<GameSession> todaySessions = gameSessionRepository.findByUserIdAndPlayDate(userId, LocalDate.now());
        return todaySessions.stream()
                .filter(s -> "IN_PROGRESS".equals(s.getStatus()))
                .findFirst();
    }
}
