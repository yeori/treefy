package github.yeori.treefy.rule;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RuleInflaterTest {

    @Test
    public void test_group_property() {
        String rule = "a.(b, c.(d,e), f)";

        RuleInflater inflater = new RuleInflater();
        List<String> expected = Arrays.asList(
                "a.b",
                "a.c.d",
                "a.c.e",
                "a.f");
        assertEquals(expected, inflater.inflate(rule));
    }

    @Test
    public void test_tokenizer() {
        RuleInflater.Token token = new RuleInflater.Token("user .    ( password, email)");
        assertEquals(Arrays.asList("user", ".", "(", "password", ",", "email", ")"), token.tokens);
    }
}