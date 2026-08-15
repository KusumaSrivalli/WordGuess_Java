package com.wordguess.repository;

import com.wordguess.model.GameSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GameSessionRepository extends MongoRepository<GameSession, String> {
    List<GameSession> findByUserIdAndPlayDate(String userId, LocalDate playDate);
    long countByUserIdAndPlayDate(String userId, LocalDate playDate);
    List<GameSession> findByPlayDate(LocalDate playDate);
    List<GameSession> findByUserId(String userId);
}
