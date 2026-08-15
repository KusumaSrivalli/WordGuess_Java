package com.wordguess.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "words")
public class Word {

    @Id
    private String id;

    @Indexed(unique = true)
    private String word; // 5-letter uppercase word

    public Word() {}

    public Word(String word) {
        this.word = word != null ? word.toUpperCase() : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word != null ? word.toUpperCase() : null;
    }
}
