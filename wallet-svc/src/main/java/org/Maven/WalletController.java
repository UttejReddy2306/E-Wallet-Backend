package org.Maven;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @GetMapping("/get")
    public String getStatus(){
        return " this is wallet Application";
    }
}
