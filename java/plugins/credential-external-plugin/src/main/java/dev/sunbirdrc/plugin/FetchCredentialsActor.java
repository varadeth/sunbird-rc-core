package dev.sunbirdrc.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.sunbirdrc.actors.factory.MessageFactory;
import dev.sunbirdrc.plugin.components.SpringContext;
import dev.sunbirdrc.plugin.service.Service;
import dev.sunbirdrc.pojos.PluginRequestMessage;
import dev.sunbirdrc.pojos.PluginResponseMessage;
import dev.sunbirdrc.pojos.PluginResponseMessageCreator;
import dev.sunbirdrc.pojos.attestation.Action;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.sunbird.akka.core.ActorCache;
import org.sunbird.akka.core.BaseActor;
import org.sunbird.akka.core.MessageProtos;
import org.sunbird.akka.core.Router;

import java.util.Map;

public class FetchCredentialsActor extends BaseActor {
    private ObjectMapper objectMapper;

    public FetchCredentialsActor() {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void onReceive(MessageProtos.Message request) throws Throwable {
        ApplicationContext context = SpringContext.getAppContext();
        Service service = (Service) context.getBean("service");
        PluginRequestMessage message = objectMapper.readValue(request.getPayload().getStringValue(), PluginRequestMessage.class);
        RestTemplate restTemplate = new RestTemplate();
        final String keycloakSecret = service.fetchClientSecretToken(restTemplate);
        Map<String, ArrayNode> credentials = service.fetchCredentials(message, restTemplate, keycloakSecret);
        PluginResponseMessage pluginResponseMessage = PluginResponseMessageCreator.createPluginResponseMessage(message);
        buildPluginResponseMessage(credentials, pluginResponseMessage);
        MessageProtos.Message esProtoMessage = MessageFactory.instance().createPluginResponseMessage(pluginResponseMessage);
        ActorCache.instance().get(Router.ROUTER_NAME).tell(esProtoMessage, null);
    }

    private void buildPluginResponseMessage(Map<String, ArrayNode> credentials, PluginResponseMessage pluginResponseMessage) throws JsonProcessingException {
        if(credentials.size() != 0) {
            pluginResponseMessage.setStatus(Action.GRANT_CLAIM.name());
            pluginResponseMessage.setResponse(objectMapper.writeValueAsString(credentials));
            return;
        }
        pluginResponseMessage.setStatus(Action.REJECT_CLAIM.name());
        pluginResponseMessage.setResponse("No linked certificates found");
    }

}
