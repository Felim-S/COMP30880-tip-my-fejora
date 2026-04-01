package atlas;

import java.util.HashMap;
import java.util.Map;

public class StringMapper {
    public static Map<String, String> generateMapping(String A, String B){

        if (A == null || A.isBlank() || B == null || B.isBlank()) {
            throw new IllegalArgumentException("Structures are mismatched");
        }

        String[] tokensA = tokenise(A);
        String[] tokensB = tokenise(B);

        // structures must be of the same length
        if (tokensA.length != tokensB.length){
            throw new IllegalArgumentException("Structures are mismatched");
        }

        Map<String, String> mapping = new HashMap<>();

        boolean isPredicate = false;

        for (int  i = 0; i < tokensA.length; i++){
            String tokenA =  tokensA[i];
            String tokenB =  tokensB[i];

            // skips parentheses and throws error if tokens are not both parentheses
            if (tokenA.equals("(")) {
                if (!tokenA.equals(tokenB)) {
                    throw new IllegalArgumentException("Structures are mismatched");
                }
                isPredicate = true;
                continue;
            } else if (tokenA.equals(")")) {
                if (!tokenA.equals(tokenB)) {
                    throw new IllegalArgumentException("Structures are mismatched");
                }
                continue;
            }

            if (isPredicate) {
                if (!tokenA.equals(tokenB)) {
                    throw new IllegalArgumentException("Predicates do not match");
                }
                isPredicate = false;
                continue;
            }

            boolean asteriskA = tokenA.startsWith("*");
            boolean asteriskB = tokenB.startsWith("*");

            if (asteriskA != asteriskB) {
                throw new IllegalArgumentException("Asterisk mismatch in structure");
            }

            // 1:1 mapping checks
            if (mapping.containsKey(tokenA)) {
                if (!mapping.get(tokenA).equals(tokenB)) {
                    throw new IllegalArgumentException("Mapping is not 1:1");
                }
            }
            else {
                if (mapping.containsValue(tokenB)) {
                    throw new IllegalArgumentException("Mapping is not 1:1");
                }
            }

            mapping.put(tokenA, tokenB);
        }
        return mapping;
    }

    // helper method inspired by StructureParser; separates each token by whitespace
    private static String[] tokenise(String input) {
        return input
                .replace("(", " ( ")
                .replace(")", " ) ")
                .trim().split("\\s+");
    }
}
