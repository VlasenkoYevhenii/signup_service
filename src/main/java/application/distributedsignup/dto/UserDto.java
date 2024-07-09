package application.distributedsignup.dto;

import application.distributedsignup.fieldvalidator.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(first = "password", second = "repeatPassword",
        message = "Repeated password must match the password")
public class UserDto {
    @NotBlank(message = "Please provide your email")
    @Email(message = "Incorrect email format")
    private String email;
    @NotBlank
    @Length(min = 6, message = "The password must be at least 6 characters long")
    private String password;
    private String repeatPassword;
    private String uuid;
}
