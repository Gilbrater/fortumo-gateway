package com.fortumo.gateway.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortumo.gateway.models.MerchantRequest;
import com.fortumo.gateway.models.MerchantResponse;
import com.fortumo.gateway.utils.MerchantUrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class MerchantClient {
    private static final Logger logger = LoggerFactory.getLogger(MerchantClient.class);

    @Value("${merchant.client.timeout}")
    private int timeout;

    @Value("${merchant.urls}")
    private String merchants;

    public MerchantResponse notifyMerchant(MerchantRequest merchantRequest){
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        MerchantUrlUtil merchantURLSUtil = MerchantUrlUtil.getInstance(merchants);
        String keyword = merchantRequest.getKeyword();
        String responseMessage = "";

        String url = merchantURLSUtil.getMerchantURL(keyword);
        MerchantResponse merchantResponse = new MerchantResponse();
        try{
            HttpHeaders headers = createHttpHeaders();
            HttpEntity entity = new HttpEntity<>(merchantRequest, headers);
            logger.info("Message sent to merchant - "+merchantRequest.toString());
            ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            logger.info("Response for message - "+merchantRequest.toString()+" - received from merchant with a status code of - "+response.getStatusCodeValue());

            if(response.getStatusCodeValue()==302 || response.getStatusCodeValue()==500){
                throw new HttpServerErrorException(response.getStatusCode());
            }else{
                ObjectMapper objectMapper = new ObjectMapper();
                responseMessage = response.getBody().toString();
                merchantResponse = objectMapper.readValue(responseMessage, MerchantResponse.class);
            }
        }catch (IOException | NullPointerException e) {
            merchantResponse.setReplyMessage(responseMessage);
            logger.error("Error mapping response from merchant for message -  "+merchantRequest.toString()+" - " + e.getMessage());
        }catch(ResourceAccessException | HttpClientErrorException | HttpServerErrorException e){
            merchantResponse.setError("Network or Server Error for message - "+merchantRequest.toString()+" - "+e.getMessage());
            logger.error("error:  " + e.getMessage());
        }
        return merchantResponse;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT, "application/json");
        return headers;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        return factory;
    }
}
