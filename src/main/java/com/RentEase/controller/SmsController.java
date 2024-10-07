package com.RentEase.controller;


import com.RentEase.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/message")
public class SmsController {


    private final SmsService smsService;

    @Autowired
    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }


//    public SmsController(SmsService smsService) {
//        this.smsService = smsService;
//    }

    @PostMapping("/sendSms")
    public void  sendMessage(){
        smsService.sendSMS("+918602915346", "test ");
    }
}
