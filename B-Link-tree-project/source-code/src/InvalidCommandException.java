class InvalidCommandException extends Exception {
	private static final long serialVersionUID = -2169157330841961180L;

	InvalidCommandException(char command) {
        super("Error: invalid query-mode \"" + command + "\" entered");
    }
	InvalidCommandException(String s) {
		super(s);
	}
}