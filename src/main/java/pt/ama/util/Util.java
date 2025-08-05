package pt.ama.util;

import com.fasterxml.jackson.databind.JsonNode;

public class Util {
    /**
     * Extrai campo string do JsonNode tentando múltiplos nomes de campo
     */
    public static String extractStringField(JsonNode data, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = data.get(fieldName);
            if (field != null && !field.isNull() && field.isTextual()) {
                String value = field.asText().trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }
}
