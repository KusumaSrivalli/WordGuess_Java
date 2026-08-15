package com.wordguess.repository;

import com.wordguess.model.Word;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends MongoRepository<Word, String> {
    Optional<Word> findByWord(String word);
    boolean existsByWord(String word);
}
