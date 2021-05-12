package github.yeori.treefy.rule;

import java.util.Collections;
import java.util.List;

class CompositeRule implements IPropertyRule{

	private List<IPropertyRule> ruleChains;
	
	CompositeRule(List<IPropertyRule> ruleChains) {
		this.ruleChains = Collections.unmodifiableList(ruleChains);
	}
	@Override
	public boolean isMatched(String propertyName, int level) {
		IPropertyRule rule = null;
		for(int i = 0 ; i < ruleChains.size(); i++) {
			rule = ruleChains.get(i);
			if (rule.isMatched(propertyName, level)) {
				return true;
			}
		}
		return false;
	}
}
