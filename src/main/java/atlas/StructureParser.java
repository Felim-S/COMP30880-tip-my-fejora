package atlas;

import java.util.Stack;

public class StructureParser {
    public static Structure parse(String input){

        Stack<Structure> stack = new Stack<>();

        // Separate each token by whitespace
        String[] tokens = input
                .replace("(", " ( ")
                .replace(")", " ) ")
                .trim().split("\\s+");

        // The first symbol after a "(" is always considered a predicate
        boolean isPredicate = false;

        for (String token : tokens) {
            if (token.equals("(")) {
                stack.push(new Structure());
                isPredicate = true;
            }  else if (token.equals(")")) {
                Structure completed = stack.pop();
                if (stack.isEmpty()) { return completed; }
                else{ stack.peek().addElement(completed); }
            } else{
                if (isPredicate) {
                    stack.peek().addElement(new Predicate(token));
                    isPredicate = false;
                } else{
                    stack.peek().addElement(new Symbol(token));
                }
            }
        }
        throw new IllegalArgumentException("Malformed structure");
    }
}
