package com.wordguess.config;

import com.wordguess.model.User;
import com.wordguess.model.Word;
import com.wordguess.repository.UserRepository;
import com.wordguess.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    @Autowired
    public DataSeeder(WordRepository wordRepository, UserRepository userRepository) {
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        seedWords();
        seedUsers();
    }

    private void seedWords() {
        // Requirement: Save twenty 5-letter English words (upper case) in database to start with.
        List<String> initialWords = Arrays.asList(
            "APPLE", "HOUSE", "SMART", "PLANT", "TRAIN",
            "GRAPE", "WATER", "BRAIN", "CLOUD", "FLAME",
            "LIGHT", "MUSIC", "DREAM", "SHINE", "STORM",
            "BEACH", "TIGER", "SWEET", "GREEN", "CANDY"
        );

        for (String w : initialWords) {
            if (!wordRepository.existsByWord(w)) {
                wordRepository.save(new Word(w));
            }
        }
        System.out.println("[DataSeeder] 20 5-letter English words initialized in database.");
    }

    private void seedUsers() {
        // Seed default Admin user if not present (Username: AdminUser, Password: Password1*)
        if (!userRepository.existsByUsername("AdminUser")) {
            userRepository.save(new User("AdminUser", "Admin1$", "ADMIN"));
            System.out.println("[DataSeeder] Default Admin user created (Username: AdminUser, Password: Admin1$)");
        }

        // Seed default Player user if not present (Username: PlayerOne, Password: Player1*)
        if (!userRepository.existsByUsername("PlayerOne")) {
            userRepository.save(new User("PlayerOne", "Player1*", "PLAYER"));
            System.out.println("[DataSeeder] Default Player user created (Username: PlayerOne, Password: Player1*)");
        }
    }
}
