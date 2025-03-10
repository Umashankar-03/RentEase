package com.RentEase.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private  String authToken;

    @Value(("${twilio.phone.number}"))
    private String twilioPhoneNumber;



    public void sendSMS(String to, String body){
        Twilio.init(accountSid , authToken);
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioPhoneNumber),
                body
        ).create();

        System.out.println("SMS sent with SID :" + message.getSid());

    }
}
