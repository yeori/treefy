package github.yeori.treefy;

public class TreefyException extends RuntimeException {

	public TreefyException(String format, Object ... args) {
		super(String.format(format, args));
	}

}
