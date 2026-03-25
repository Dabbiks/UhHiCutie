package dabbiks.uhc.player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ChatCensor {

    private final Map<String, String> roots = new LinkedHashMap<>();
    private final Map<String, String> irregulars = new LinkedHashMap<>();
    private final Set<String> exceptions = new HashSet<>();

    public ChatCensor() {
        exceptions.addAll(Arrays.asList(
                "sukces", "sukcesja", "sukienka", "sukno", "sukcesywny", "sukulent", "sukurs",
                "kurwatura", "wymachując", "podsłuchując", "przesłuchując", "nasłuchując",
                "rachując", "wymachuj", "podsłuchuj", "przesłuchuj", "nasłuchuj", "rachuj"
        ));

        irregulars.put("kurew", "zup");
        irregulars.put("szmacie", "gazecie");
        irregulars.put("suko", "psinko");
        irregulars.put("pedale", "kolarzu");

        irregulars.put("ciota", "ciastko");
        irregulars.put("niger", "kolega");
        irregulars.put("nigger", "kolega");
        irregulars.put("neger", "kolega");
        irregulars.put("faggot", "kolega");
        irregulars.put("retard", "mądrala");
        irregulars.put("virgin", "zawodnik");
        irregulars.put("incel", "koleżka");
        irregulars.put("simp", "miłośnik");

        roots.put("skurwysyn", "huncwot");
        roots.put("skurwiel", "urwis");
        roots.put("skurw", "urwis");

        roots.put("pierdol", "miel");
        roots.put("pierdal", "miel");
        roots.put("pierdziel", "miel");

        roots.put("kurw", "zup");
        roots.put("chuj", "kij");
        roots.put("jeb", "kich");
        roots.put("pizd", "gwiazd");

        roots.put("pedał", "kolarz");
        roots.put("upośledz", "uzdolni");
        roots.put("czarnuch", "kominiarz");
        roots.put("transfob", "tranzystor");

        roots.put("cwel", "łobuz");
        roots.put("suk", "psink");
        roots.put("szmat", "gazet");
        roots.put("dziwk", "damk");
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

        if (exceptions.contains(lowerWord)) {
            return word;
        }

        if (irregulars.containsKey(lowerWord)) {
            return applyCasing(word, irregulars.get(lowerWord));
        }

        for (Map.Entry<String, String> entry : roots.entrySet()) {
            String root = entry.getKey();
            int index = lowerWord.indexOf(root);

            if (index != -1) {
                String prefix = lowerWord.substring(0, index);
                String replacementRoot = entry.getValue();
                String suffix = lowerWord.substring(index + root.length());

                return applyCasing(word, prefix + replacementRoot + suffix);
            }
        }

        return word;
    }

    private String applyCasing(String original, String replacement) {
        if (original.isEmpty() || replacement.isEmpty()) return replacement;

        if (Character.isUpperCase(original.charAt(0))) {
            return Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
        }
        return replacement;
    }
}