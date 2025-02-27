package leftsidebrain.book.exception;

public class OperationNotPermittedException extends RuntimeException {
	public OperationNotPermittedException(String string) {
		super(string);
	}
}
