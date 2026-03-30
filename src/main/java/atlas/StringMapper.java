package atlas;

import java.util.HashMap;
import java.util.Map;

public class StringMapper {
    public static Map<String, String> generateMapping(String a, String b){

        String[] tokensA = tokenise(a);
        String[] tokensB = tokenise(b);

        // structures must be of the same length
        if (tokensA.length != tokensB.length){
            throw new IllegalArgumentException("Structures are mismatched");
        }

        Map<String, String> mapping = new HashMap<>();

        for (int  i = 0; i < tokensA.length; i++){
            String tokenA =  tokensA[i];
            String tokenB =  tokensB[i];

            // skips parentheses and throws error if tokens are not both parentheses
            if (tokenA.equals("(") || tokenA.equals(")")) {
                if (!tokenA.equals(tokenB)) {
                    throw new  IllegalArgumentException("Structures are mismatched");
                }
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
