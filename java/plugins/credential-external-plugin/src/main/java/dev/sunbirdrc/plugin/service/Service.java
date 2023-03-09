package dev.sunbirdrc.plugin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.sunbirdrc.pojos.PluginRequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {
    @Value("${credentials.clientId}")
    private String clientId;
    @Value("${credentials.clientSecret}")
    private String clientSecret;
    @Value("${credentials.issuanceUrl}")
    private String issuanceUrl;
    @Value("${credentials.issuanceKeycloakUrl}")
    private String keycloakUrl;
    @Value("${credentials.registeredCredentials}")
    private List<String> registeredCredentials;
    @Autowired
    private ObjectMapper objectMapper;

    public String fetchClientSecretToken(RestTemplate restTemplate) {
        HttpHeaders headersForKeycloak = new HttpHeaders();
        headersForKeycloak.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("client_id", clientId);
        map.set("client_secret", clientSecret);
        map.set("grant_type", "client_credentials");
        HttpEntity entity = new HttpEntity(map, headersForKeycloak);
        ResponseEntity<Map> keycloakResponse = restTemplate.exchange(keycloakUrl, HttpMethod.POST,  entity, Map.class);
        final String keycloakSecret = (String) keycloakResponse.getBody().get("access_token");
        return keycloakSecret;
    }

    public Map<String, ArrayNode> fetchCredentials(PluginRequestMessage message, RestTemplate restTemplate, String keycloakSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + keycloakSecret);
        MultiValueMap<String, Object> filters = new LinkedMultiValueMap<>();
        MultiValueMap<String, Object> studentReference = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> condition = new LinkedMultiValueMap<>();
        condition.set("eq", message.getSourceOSID());
        studentReference.set("studentReference", condition);
        filters.set("filters", studentReference);
        HttpEntity header = new HttpEntity<>(filters, headers);
        Map<String, ArrayNode> map = new HashMap<>();
        registeredCredentials.stream().forEach(schema -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(issuanceUrl + "/" + schema +"/search", HttpMethod.POST, header, String.class);
            ArrayNode credentials = null;
            try {
                credentials = objectMapper.readValue(responseEntity.getBody(), ArrayNode.class);
                map.put(schema, credentials);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return map;

    }
}
