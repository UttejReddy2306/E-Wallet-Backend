package org.Maven;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class WalletService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JSONParser jsonParser;

    @Value("${wallet.promotional.balance}")
    private Long balance;


    @Autowired
    WalletRepository walletRepository;

    @KafkaListener(topics = {"user-created"},groupId = "test123")
    public void createWallet(String msg){
        try{
            JSONObject object = (JSONObject) this.jsonParser.parse(msg);
            Integer userId = (Integer)object.get("id");
            Wallet wallet = walletRepository.findByUserId(userId);
            if(wallet != null){
                LoggerFactory.getLogger(WalletService.class).info("wallet already created for a user with id " + userId);
                return;
            }
            wallet = Wallet.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .balance(this.balance)
                    .status(WalletStatus.ACTIVE)
                    .build();
            this.walletRepository.save(wallet);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
