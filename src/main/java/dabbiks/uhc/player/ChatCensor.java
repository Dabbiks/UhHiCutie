package dabbiks.uhc.player;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChatCensor {

    private final Map<String, String> roots = new LinkedHashMap<>();
    private final Map<String, String> irregulars = new LinkedHashMap<>();

    public ChatCensor() {
        roots.put("kurw", "zup");
        roots.put("chuj", "kij");
        roots.put("jeb", "kich");
        roots.put("pierdol", "miel");
        roots.put("pizd", "gwiazd");

        irregulars.put("kurew", "zup");

        roots.put("pedał", "rowerzyst");
        irregulars.put("pedale", "rowerzysto");
        roots.put("czarnuch", "kominiarz");
        roots.put("upośledz", "utalentow");
        roots.put("transfob", "tranzystor");

        irregulars.put("ciota", "ciastko");
        irregulars.put("nigger", "kolega");
        irregulars.put("neger", "kolega");
        irregulars.put("faggot", "kolega");
        irregulars.put("retard", "mądrala");
        irregulars.put("virgin", "zawodnik");
        irregulars.put("incel", "koleżka");
        irregulars.put("simp", "miłośnik");
    }

    public String censor(String message) {
        if (message == null || message.isEmpty()) return message;

        String[] parts = message.split("(?<=\\s)|(?=\\s)|(?<=[^\\p{L}])|(?=[^\\p{L}])");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            result.append(processWord(part));
        }

        return result.toString();
    }

    private String processWord(String word) {
        if (word.length() < 2) return word;
        String lowerWord = word.toLowerCase();

        if (irregulars.containsKey(lowerWord)) {
            return applyCasing(word, irregulars.get(lowerWord));
        }

        for (Map.Entry<String, String> entry : roots.entrySet()) {
            String root = entry.getKey();
            if (lowerWord.startsWith(root)) {
                String replacementRoot = entry.getValue();
                String suffix = lowerWord.substring(root.length());
                return applyCasing(word, replacementRoot + suffix);
            }
        }

        return word;
    }

    private String applyCasing(String original, String replacement) {
        if (Character.isUpperCase(original.charAt(0))) {
            return Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
        }
        return replacement;
    }
}