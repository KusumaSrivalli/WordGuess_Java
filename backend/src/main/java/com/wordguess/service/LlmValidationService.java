package com.wordguess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LlmValidationService {

    @Value("${llm.api.key:${GEMINI_API_KEY:}}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Standard 5-letter English word dictionary for local validation & fallback
    private static final Set<String> KNOWN_WORDS = new HashSet<>(Arrays.asList(
        "APPLE", "HOUSE", "SMART", "PLANT", "TRAIN", "GRAPE", "WATER", "BRAIN", "CLOUD", "FLAME",
        "LIGHT", "MUSIC", "DREAM", "SHINE", "STORM", "BEACH", "TIGER", "SWEET", "GREEN", "CANDY",
        "ABOVE", "ACID", "ACRE", "ACTOR", "ACUTE", "ADAPT", "ADMIT", "ADOPT", "ADULT", "AFTER",
        "AGAIN", "AGENT", "AGREE", "AHEAD", "ALARM", "ALBUM", "ALERT", "ALIEN", "ALIKE", "ALIVE",
        "ALLOW", "ALONE", "ALONG", "ALTER", "AMONG", "ANGER", "ANGLE", "ANGRY", "ANKLE", "ANNOY",
        "ANSWER", "APPLY", "ARENA", "ARGUE", "ARISE", "ARMOR", "ARRAY", "ARROW", "ASSET",
        "AUDIO", "AUDIT", "AVOID", "AWAKE", "AWARE", "BADGE", "BAKER", "BASIC", "BASIS",
        "BEGIN", "BEING", "BELOW", "BENCH", "BLACK", "BLADE", "BLAME", "BLANK", "BLAST", "BLEED",
        "BLEND", "BLESS", "BLIND", "BLOCK", "BLOOD", "BLOOM", "BOARD", "BOAST", "BOOST", "BOOTH",
        "BOUND", "BRAND", "BREAD", "BREAK", "BRICK", "BRIDE", "BRIEF", "BRING", "BROAD",
        "BROWN", "BUILD", "BUNCH", "BUYER", "CABLE", "CANOE", "CARGO", "CARRY", "CATCH",
        "CAUSE", "CHAIN", "CHAIR", "CHALK", "CHAMP", "CHARM", "CHART", "CHASE", "CHEAP", "CHECK",
        "CHEEK", "CHEER", "CHEST", "CHIEF", "CHILD", "CHILL", "CHINA", "CHIP", "CHOIR", "CLAIM",
        "CLASS", "CLEAN", "CLEAR", "CLIMB", "CLOCK", "CLOSE", "CLOTH", "COACH", "COAST",
        "COUNT", "COURT", "COVER", "CRACK", "CRAFT", "CRANE", "CRASH", "CRAWL", "CRAZY", "CREAM",
        "CREEK", "CRIME", "CROSS", "CROWD", "CROWN", "CRUDE", "CRUEL", "CRUSH", "CYCLE", "DAILY",
        "DANCE", "DANGER", "DRAFT", "DRAIN", "DRAMA", "DRANK", "DRAWN", "DRESS", "DRIED",
        "DRIFT", "DRINK", "DRIVE", "DROWN", "DRUNK", "EAGER", "EARLY", "EARTH", "EIGHT", "ELBOW",
        "ELDER", "ELECT", "ELITE", "EMPTY", "ENEMY", "ENJOY", "ENTER", "EQUAL", "ERROR", "EVENT",
        "EVERY", "EXACT", "EXIST", "EXTRA", "FAITH", "FALSE", "FANCY", "FATAL", "FAULT", "FAVOR",
        "FEAST", "FEVER", "FIELD", "FIGHT", "FINAL", "FIRST", "FLASH", "FLEET", "FLESH",
        "FLOAT", "FLOCK", "FLOOD", "FLOOR", "FLOUR", "FLUID", "FLUSH", "FOCUS", "FORCE", "FORTH",
        "FORTY", "FORUM", "FOUND", "FRAME", "FRESH", "FRONT", "FROST", "FRUIT", "GIANT", "GIVEN",
        "GLASS", "GLOBE", "GLORY", "GLOVE", "GRACE", "GRADE", "GRAIN", "GRAND", "GRANT",
        "GRAPH", "GRASP", "GRASS", "GRAVE", "GREAT", "GREET", "GRIEF", "GROUP", "GUARD",
        "GUESS", "GUEST", "GUIDE", "HAPPY", "HEART", "HEAVY", "HELLO", "HONEY", "HORSE", "HOTEL",
        "HUMAN", "HUMOR", "IDEAL", "IMAGE", "INDEX", "INNER", "INPUT", "ISSUE", "JAPAN",
        "JEANS", "JOINT", "JUDGE", "JUICE", "KNIFE", "KNOCK", "LABOR", "LARGE", "LAUGH", "LAYER",
        "LEARN", "LEASE", "LEAST", "LEMON", "LIMIT", "LOCAL", "LOGIC", "LUCKY", "LUNCH",
        "MAGIC", "MAJOR", "MAKER", "MARCH", "MATCH", "MAYBE", "MAYOR", "MEDAL", "MEDIA", "METAL",
        "MIGHT", "MINOR", "MODEL", "MONEY", "MONTH", "MORAL", "MOTOR", "MOUNT", "MOUSE", "MOUTH",
        "MOVIE", "NIGHT", "NOISE", "NORTH", "NOVEL", "NURSE", "OFFER", "OFFICE", "OPERA",
        "ORDER", "OTHER", "OUTER", "OWNER", "PANEL", "PAPER", "PARTY", "PEACE", "PHASE", "PHONE",
        "PHOTO", "PIECE", "PILOT", "PITCH", "PLACE", "PLAIN", "PLANE", "PLATE", "POINT",
        "POUND", "POWER", "PRESS", "PRICE", "PRIDE", "PRIME", "PRINT", "PRIZE", "PROOF", "PROUD",
        "PROVE", "QUEEN", "QUICK", "QUIET", "QUITE", "RADIO", "RAISE", "RANGE", "RAPID", "RATIO",
        "REACH", "REACT", "READY", "REFER", "RIGHT", "RIVAL", "RIVER", "ROBOT", "ROUGH", "ROUND",
        "ROUTE", "ROYAL", "RURAL", "SCALE", "SCENE", "SCOPE", "SCORE", "SENSE", "SERVE", "SEVEN",
        "SHADE", "SHAKE", "SHALL", "SHAPE", "SHARE", "SHARP", "SHEET", "SHELF", "SHELL", "SHIFT",
        "SHIRT", "SHOCK", "SHOOT", "SHORT", "SHOWN", "SIGHT", "SINCE", "SKILL", "SLEEP",
        "SLIDE", "SMALL", "SMILE", "SMOKE", "SOLID", "SOLVE", "SORRY", "SOUND", "SOUTH",
        "SPACE", "SPARE", "SPEAK", "SPEED", "SPEND", "SPLIT", "SPORT", "STAFF", "STAGE", "STAND",
        "START", "STATE", "STEAM", "STEEL", "STICK", "STILL", "STOCK", "STONE", "STORE",
        "STORY", "STRIP", "STUDY", "STYLE", "SUGAR", "TABLE", "TASTE", "TEACH", "THANK",
        "THEIR", "THEME", "THERE", "THESE", "THICK", "THING", "THINK", "THIRD", "THOSE", "THREE",
        "THROW", "TIGHT", "TITLE", "TODAY", "TOPIC", "TOTAL", "TOUCH", "TOWER", "TRACK",
        "TRADE", "TREAT", "TREND", "TRIAL", "TRIBE", "TRICK", "TRUCK", "TRULY", "TRUST",
        "TRUTH", "UNCLE", "UNDER", "UNION", "UNITY", "UNTIL", "UPPER", "UPSET", "URBAN", "USAGE",
        "VALUE", "VIDEO", "VIRUS", "VISIT", "VITAL", "VOICE", "WASTE", "WATCH", "WHEEL",
        "WHERE", "WHICH", "WHITE", "WHOLE", "WHOSE", "WOMAN", "WORLD", "WORRY", "WORST", "WORTH",
        "WOULD", "WRITE", "WRONG", "YOUTH"
    ));

    /**
     * Validates if a 5-letter uppercase word is a legitimate English dictionary word.
     * Uses Google Gemini LLM API when configured, otherwise falls back to local dictionary verification.
     */
    public boolean isValidWord(String word) {
        if (word == null || word.trim().length() != 5 || !word.matches("^[A-Z]{5}$")) {
            return false;
        }

        String upperWord = word.toUpperCase();

        // If Gemini API Key is available, invoke LLM API
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            try {
                boolean llmResult = validateWithGeminiLlm(upperWord);
                return llmResult;
            } catch (Exception e) {
                System.err.println("[LlmValidationService] Gemini API call failed: " + e.getMessage() + ". Falling back to dictionary check.");
            }
        }

        // Fallback dictionary verification
        return KNOWN_WORDS.contains(upperWord) || isHeuristicValidWord(upperWord);
    }

    private boolean validateWithGeminiLlm(String word) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "Is '" + word + "' a valid 5-letter English dictionary word? Answer in JSON format ONLY: {\"valid\": true} or {\"valid\": false}";

        Map<String, Object> textPart = Collections.singletonMap("text", prompt);
        Map<String, Object> partsObj = Collections.singletonMap("parts", Collections.singletonList(textPart));
        Map<String, Object> contentsObj = Collections.singletonMap("contents", Collections.singletonList(partsObj));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(contentsObj, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String responseText = extractTextFromGeminiResponse(response.getBody());
            if (responseText != null) {
                return responseText.toLowerCase().contains("\"valid\": true") || responseText.toLowerCase().contains("true");
            }
        }

        return KNOWN_WORDS.contains(word);
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromGeminiResponse(Map responseBody) {
        try {
            List candidates = (List) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map candidate = (Map) candidates.get(0);
                Map content = (Map) candidate.get("content");
                List parts = (List) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    Map part = (Map) parts.get(0);
                    return (String) part.get("text");
                }
            }
        } catch (Exception e) {
            System.err.println("[LlmValidationService] Error parsing Gemini JSON response: " + e.getMessage());
        }
        return null;
    }

    private boolean isHeuristicValidWord(String word) {
        // Basic heuristic for fallback: must have at least one vowel or Y
        String vowels = "AEIOUY";
        for (char c : word.toCharArray()) {
            if (vowels.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }
}
