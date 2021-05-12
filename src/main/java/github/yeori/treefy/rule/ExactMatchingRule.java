package github.yeori.treefy.rule;

class ExactMatchingRule implements IPropertyRule {

	String proppertyName;
	
	ExactMatchingRule(String proppertyName) {
		this.proppertyName = proppertyName;
	}
	public String getProppertyName() {
		return proppertyName;
	}
	@Override
	public boolean isMatched(String propertyName, int level) {
		return propertyName.equals(this.proppertyName);
	}
	@Override
	public String toString() {
		return "ExactMatching [" + proppertyName + "]";
	}
}
