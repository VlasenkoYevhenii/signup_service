package application.distributedsignup.service;

import application.distributedsignup.dto.UserDto;
import application.distributedsignup.exception.SignupException;
import application.distributedsignup.response.ResponseMessage;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {
    private static final String TOPIC_USERS = "users";
    private static final String TOPIC_RESPONSE = "response";
    private static final String SUCCESS = "success";
    private static final String SIGNUP_EXCEPTION_MSG = "Failed to register user: ";
    private static final String UNKNOWN_ERROR = ":unknown error";
    private static final String INTERRUPTED_EXCEPTION_MSG =
            "Interrupted while waiting for response";
    private static final String NULL_DTO_MSG = "UserDto is null";
    private static final int LATCH_TIMER = 10;
    private static final int LATCH_INIT_COUNT = 1;
    private final KafkaTemplate<String, UserDto> kafkaTemplate;
    private CountDownLatch latch;
    private ResponseMessage responseMessage;

    @Override
    public String processSignup(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException(NULL_DTO_MSG);
        }

        String uuid = UUID.randomUUID().toString();
        userDto.setUuid(uuid);
        latch = new CountDownLatch(LATCH_INIT_COUNT);
        responseMessage = null;
        kafkaTemplate.send(TOPIC_USERS, userDto);

        try {
            boolean completed = latch.await(LATCH_TIMER, TimeUnit.SECONDS);
            if (!completed
                    || responseMessage == null
                    || !SUCCESS.equals(responseMessage.getStatus())) {
                throw new SignupException(SIGNUP_EXCEPTION_MSG
                        + (responseMessage != null ? responseMessage.getMessage() : UNKNOWN_ERROR));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(INTERRUPTED_EXCEPTION_MSG, e);
        }
        return uuid;
    }

    @KafkaListener(topics = TOPIC_RESPONSE)
    public void listen(ResponseMessage message) {
        this.responseMessage = message;
        latch.countDown();
    }
}
