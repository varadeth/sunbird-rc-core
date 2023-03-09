package dev.sunbirdrc.plugin.controllers;

import dev.sunbirdrc.plugin.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CredentialController {

    @Value("${credentials.issuanceUrl}")
    private String issuanceUrl;
    @Autowired
    private Service service;
    @GetMapping("/plugin/api/v1/{entityName}/{osid}")
    public ResponseEntity getCredential(@PathVariable String entityName, @PathVariable String osid, @RequestHeader("Accept") String acceptType) {
        RestTemplate restTemplate = new RestTemplate();
        String clientSecretToken = service.fetchClientSecretToken(restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + clientSecretToken);
        headers.set("Accept", acceptType);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(issuanceUrl + "/" + entityName + "/" + osid, HttpMethod.GET, entity, ResponseEntity.class);
    }
}
