package github.yeori.treefy.rule;

public interface IPropertyRule {

	boolean isMatched(String propertyName, int level);
}
