package com.fortumo.gateway.clients;

import com.fortumo.gateway.models.SmsContentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Base64;

@Component
public class ProviderClient {
    private static final Logger logger = LoggerFactory.getLogger(ProviderClient.class);

    @Value("${provider.send.sms.url}")
    private String url;
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
    @Value("${provider.client.timeout}")
    private int timeout;

    private RestTemplate restTemplate = new RestTemplate();

    public boolean notifyUser(SmsContentRequest smsContentRequest){
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

        try{
            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity entity = new HttpEntity<>(headers);
            String providerUrl = buildURL(url, smsContentRequest);

            ResponseEntity response = restTemplate.exchange(providerUrl, HttpMethod.GET, entity, String.class);
            if(response.getStatusCode()== HttpStatus.OK){
                return true;
            }
        }catch(ResourceAccessException | HttpClientErrorException | HttpServerErrorException e){
            logger.error("Network or Server Error for message  - "+smsContentRequest.toString()+" - "+e.getMessage());
        }
        return false;
    }

    private HttpHeaders createHttpHeaders(String username, String password) {
        String clearAuth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(clearAuth.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

    private String buildURL(String url, SmsContentRequest smsContentRequest){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("message", smsContentRequest.getMessage())
                .queryParam("mo_message_id", smsContentRequest.getMoMessageId())
                .queryParam("operator", smsContentRequest.getOperator())
                .queryParam("receiver", smsContentRequest.getReceiver());
        return builder.toUriString();
    }
}
