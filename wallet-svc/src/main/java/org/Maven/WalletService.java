package org.Maven;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @KafkaListener(topics = {"user-created"},groupId = "test123")
    public void createWallet(String msg){
        try{
            JSONObject object = (JSONObject) this.jsonParser.parse(msg);
            Long user = (Long)object.get("id");
            Integer userId = user.intValue();
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

    @KafkaListener(topics = {"transaction-initiated"},groupId = "test123")
    public void completeTransaction(String msg) throws ParseException, JsonProcessingException {
        JSONObject object = (JSONObject) this.jsonParser.parse(msg);
        Long sender  = (Long) object.get("sender");
        Long receiver = (Long) object.get("receiver");
        Long amount = (Long) object.get("amount");

        String externalTransactionId = (String)object.get("externalTxnId");

        Integer send = sender.intValue();
        Integer rec = receiver.intValue();
        Wallet s = this.walletRepository.findByUserId(send);
        Wallet r = this.walletRepository.findByUserId(rec);
        JSONObject event = new JSONObject();
        event.put("externalTxnId", externalTransactionId);
        if(s == null || r == null || amount < 1 || s.getBalance() < amount ){
            LoggerFactory.getLogger(WalletService.class)
                    .error("Wallet update failed due to either " +
                            "wallet doesn't exist or with low account" +
                            " balance in sender wallet ");

            event.put("status" , "FAILED");
            this.kafkaTemplate.send("wallet-update", this.objectMapper.writeValueAsString(event));
            return;

        }

                   // method 1
//        this.walletRepository.updateWalletBalance(sender,-amount);
//        this.walletRepository.updateWalletBalance(receiver,amount);
                    //or

        s.setBalance(s.getBalance()-amount);
        r.setBalance(r.getBalance()+amount);
        this.walletRepository.saveAll(List.of(s,r));



        event.put("status","SUCCESSFUL");

        this.kafkaTemplate.send("wallet-update", this.objectMapper.writeValueAsString(event));


    }
}
