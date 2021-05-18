package github.yeori.treefy.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleInflater {
    final private static String SPACE_IN_TOKEN = ".*\\s+.*";

    public List<String> inflate(String rule) {
        Token tokenContext = new Token(rule);
        Node node = parseNode(tokenContext);
        List<String> props = node.resolveProps();
        return props;
    }

    private String parseProp(Token token) {
        StringBuilder sb = new StringBuilder();
        while (token.remaing() && (token.isPropName() || token.isDot())) {
            sb.append(token.forward());
        }
        return sb.toString();
    }
    private Node parseNode(Token token ) {
        String prop = parseProp(token); // a.b.c.
        Node current = new Node(prop);
        if (token.remaing() && token.isGroupStart()) {
            parseGroup(token, current);
        }
        return current;
    }
    private void parseGroup(Token token, Node parent) {
        if (!token.isGroupStart()) {
            throw new IllegalStateException("group must start with '(' from tokens");
        }

        List<Node> nodes = new ArrayList<>();
        while (!token.isGroupEnd()) {
            if (token.isGroupStart() || token.isComma()) {
                token.forward();
                Node child = parseNode(token);
                nodes.add(child);
                child.prev = parent;
            } else {
                throw new IllegalStateException("unexpected token " + token.peek() + token.trace());
            }
        }
        parent.subs = nodes;
        token.forward(); // consume ')'
    }
    static class Token {
        final List<String> tokens;
        int pos;

        Token(String rule) {
            this.tokens = tokenize(rule.trim());
            validate(this.tokens);
        }

        private void validate(List<String> tokens) {
            for(int i = 0; i < tokens.size(); i++) {
                if (isPropName() && tokens.get(i).matches(SPACE_IN_TOKEN)) {
                    throw new IllegalArgumentException("space not allowed in property name: " + around(tokens, i));
                }
            }
        }

        private String around(List<String> tokens, int i) {
            String prev = i == 0 ? "" : tokens.get(i-1);
            String next = i == tokens.size() - 1 ? "" : tokens.get(i+1);
            return " at [" + prev + tokens.get(i) + next + "]";
        }
        public String trace() {
            return around(tokens, pos);
        }

        boolean isGroupStart() {
            String token =  peek();
            return "(".equals(token);
        }
        public boolean isGroupEnd() {
            String token =  peek();
            return ")".equals(token);
        }
        boolean isDot() {
            String token =  peek();
            return ".".equals(token);
        }
        boolean isComma() {
            String token =  peek();
            return ",".equals(token);
        }
        boolean isPropName () {
            String token = peek();
            return token.matches("[0-9a-zA-z$_]+");
        }
        List<String> tokenize(String rule) {
            Pattern p = Pattern.compile("[().,]");
            Matcher m = p.matcher(rule);
            List<String> tokens = new ArrayList<>();
            int pos = 0;
            while (m.find()) {
                String token = rule.substring(pos, m.start()).trim();
                if (token.length() > 0) {
                    tokens.add(token.trim());
                }
                token = m.group().trim();
                if (token.length() > 0) {
                    tokens.add(token.trim());
                }
                pos = m.end();
            }
            if (pos < rule.length()) {
                tokens.add(rule.substring(pos).trim());
            }
//            tokens.add(0, "(");
//            tokens.add(")");
            return tokens;
        }

        String peek() {
            return tokens.get(pos);
        }
        String forward() {
            return tokens.get(pos++);
        }

        public boolean remaing() {
            return pos < tokens.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("( " + pos +  ")");
            tokens.subList(pos, tokens.size()).forEach(t -> sb.append(" " + t));
            return sb.toString();
        }
    }
    static class Node {
        public List<Node> subs = Collections.emptyList();
        String prop;
        Node prev;

        public Node(String prop) {
            this(prop, null);
        }

        public Node(String prop, Node prev) {
            this.prop = prop;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "(" + prop + ")";
        }

        public void walk(Consumer<Node> fn) {
            fn.accept(this);
            for(Node child : subs) {
                child.walk(fn);
            }
        }

        public List<String> resolveProps() {
            List<String> pathes = new ArrayList<>();
            resolve("",this, pathes);
            return pathes;
        }

        private void resolve(String prefix, Node node, List<String> pathes) {
            String path = prefix + node.prop;
            if (node.subs.size() == 0) {
                pathes.add(path);
            } else {
                for (Node child : node.subs) {
                    resolve(path, child, pathes);
                }
            }
        }
    }
}
