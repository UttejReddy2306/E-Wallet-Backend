package org.Maven;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/initiate/{sender}/{receiver}/{amount}/{reason}")
    public String initiateTransaction(@PathVariable("sender") Integer sender,
                                      @PathVariable("receiver") Integer receiver,
                                      @PathVariable("amount") Long amount,
                                      @PathVariable("reason") String reason) throws JsonProcessingException {
        return this.transactionService.initiateTransaction(sender,receiver,amount,reason);
    }
}
