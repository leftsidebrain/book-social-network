package leftsidebrain.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {

	NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No Code"),
	INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST,"Invalid current password"),
	NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "New Password Not Match"),
	ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "Account Locked"),
	ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "Account Disabled"),
	BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN,"Invalid credentials"),;

	@Getter
	private final String description;
	@Getter
	private final HttpStatus httpStatusCode;
	@Getter
	private final int code;

	BusinessErrorCodes(int code, HttpStatus httpStatusCode, String description) {
		this.httpStatusCode = httpStatusCode;
		this.code = code;
		this.description = description;
	}
}
