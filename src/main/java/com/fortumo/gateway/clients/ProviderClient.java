package com.fortumo.gateway.clients;

import com.fortumo.gateway.models.SmsContentRequest;
import org.springframework.stereotype.Component;

@Component
public class ProviderClient {
    public boolean notifyUser(SmsContentRequest smsContentRequest){
        return true;
    }
}
