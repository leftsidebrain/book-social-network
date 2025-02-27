package leftsidebrain.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
	@Email(message = "Email is not valid")
	@NotEmpty(message = "Email must not empty")
	@NotBlank(message = "Email Name must not empty")
	private String email;

	@Size(min = 8, message = "Password must be at least 8 characters")
	@NotEmpty(message = "First Name must not empty")
	@NotBlank(message = "First Name must not empty")
	private String password;
}
