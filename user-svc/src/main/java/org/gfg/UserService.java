package org.gfg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;
    public void createUser(CreateUserRequest createUserRequest) throws JsonProcessingException {

        User user = createUserRequest.toUser();
        this.userRepository.save(user);
        JSONObject object = this.objectMapper.convertValue(user,JSONObject.class);
        this.kafkaTemplate.send("user-created", objectMapper.writeValueAsString(object));

    }
}
