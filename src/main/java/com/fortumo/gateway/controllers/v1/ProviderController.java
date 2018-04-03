package com.fortumo.gateway.controllers.v1;

import com.fortumo.gateway.models.ProviderRequest;
import com.fortumo.gateway.services.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sms")
public class ProviderController {
    private static final Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @Autowired
    private ProviderService providerService;

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public String paymentNotification(ProviderRequest providerRequest, @RequestParam(value = "message_id", required = true) String messageId){
        providerRequest.setMessageId(messageId);
        providerRequest.setId(UUID.randomUUID().toString());
        logger.info(providerRequest.toString());
        return providerService.paymentNotification(providerRequest);
    }
}
