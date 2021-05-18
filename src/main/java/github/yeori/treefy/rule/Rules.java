package github.yeori.treefy.rule;

import java.util.ArrayList;
import java.util.List;

import github.yeori.treefy.TreefyException;

public class Rules {

	private static final IPropertyRule EMPTY_RULE = (propName, level) -> false;
    private static final RuleInflater INFLATER = new RuleInflater();

	public static IPropertyRule parseRules(String ... ruleSpecs) {
		List<IPropertyRule> rules = new ArrayList<>();
		for (String spec : ruleSpecs) {
			List<String> inflatedRules = INFLATER.inflate(spec.trim());
			for (String _rule: inflatedRules) {
				rules.add(new ExactMatchingRule(_rule));
			}
		}
		return new CompositeRule(rules);
	}

	private static String[] delim(String spec) {
		String [] tokens = spec.split("\\.");
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].trim();
			if(tokens[i].length() == 0) {
				throw new TreefyException("empty property found : [%s]", spec);
			}
		}
		return tokens;
	}

	public static IPropertyRule emptyRule() {
		return EMPTY_RULE;
	}
}
