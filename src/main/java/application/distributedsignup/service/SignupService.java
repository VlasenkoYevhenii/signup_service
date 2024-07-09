package application.distributedsignup.service;

import application.distributedsignup.dto.UserDto;

public interface SignupService {
    String processSignup(UserDto userDto);
}
