package github.yeori.treefy.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PropertyNameRuleChain implements IPropertyRule {

	List<ExactMatchingRule> rules;
	int currentIndex;
	
	int rollbackIndex = -1;
	
	PropertyNameRuleChain(List<String> rules) {
		this.rules = new ArrayList<>();
		for (int i = 0; i < rules.size(); i++) {
			this.rules.add(new ExactMatchingRule(rules.get(i)));
		}
		this.currentIndex = 0;
	}
	
	@Override
	public boolean isMatched(String propertyName, int level) {
		if (currentIndex == rules.size()) {
			// consumed
			return false;
		}
		ExactMatchingRule rule = rules.get(currentIndex);
		
		boolean matched = rule.isMatched(propertyName, level);
		int idx = currentIndex;
		if (matched) {
			currentIndex ++ ;
			return idx + 1 == rules.size();
		} else {
			return false;
		}
	}
}
