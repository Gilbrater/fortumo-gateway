package com.fortumo.gateway.clients;

import com.fortumo.gateway.models.MerchantRequest;
import com.fortumo.gateway.models.MerchantResponse;
import org.springframework.stereotype.Component;

@Component
public class MerchantClient {
    public MerchantResponse notifyMerchant(MerchantRequest merchantRequest){
        MerchantResponse merchantResponse = new MerchantResponse();
        merchantResponse.setReplyMessage("This is the message");
        return merchantResponse;
    }
}
