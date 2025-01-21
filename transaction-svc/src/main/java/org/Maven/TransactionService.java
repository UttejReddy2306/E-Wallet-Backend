package org.Maven;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JSONParser jsonParser;

    public String initiateTransaction(Integer sender, Integer receiver, Long amount, String reason) throws JsonProcessingException {

        Transaction transaction = Transaction.builder()
                .transactionStatus(TransactionStatus.PENDING)
                .amount(amount)
                .externalTxnId(UUID.randomUUID().toString())
                .sender(sender)
                .reason(reason)
                .receiver(receiver)
                .build();
        this.transactionRepository.save(transaction);
        JSONObject object = this.objectMapper.convertValue(transaction, JSONObject.class);
        this.kafkaTemplate.send("transaction-initiated" , this.objectMapper.writeValueAsString(object));

        return transaction.getExternalTxnId();
    }

    @KafkaListener(topics = "wallet-update",groupId = "cptTxn23")
    public void completeTransaction(String msg) throws ParseException {
        JSONObject object = (JSONObject) jsonParser.parse(msg);
        String externalTxnId = (String)object.get("externalTxnId");
        String status = (String) object.get("status");
        Transaction transaction = this.transactionRepository.findByExternalTxnId(externalTxnId);
        if(!transaction.getTransactionStatus().equals(TransactionStatus.PENDING)){
            LoggerFactory.getLogger(TransactionService.class).warn("Transaction Already reached to Terminal state, id {}", externalTxnId);
            return;
        }
        transaction.setTransactionStatus((status.equals("SUCCESSFUL"))
                ?TransactionStatus.SUCCESSFUL:TransactionStatus.FAILED);
        this.transactionRepository.save(transaction);

    }


}
