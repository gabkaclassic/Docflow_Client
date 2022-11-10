package client.sender;

import client.entity.Team;
import client.entity.process.Process;
import client.entity.process.Step;
import client.entity.process.document.Document;
import client.response.ExistResponse;
import client.response.InfoResponse;
import client.response.Response;
import client.response.StepResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public class Sender {
    
    private static final String BASE_URL = System.getenv("BASE_URL");
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
    
    private static final Function<ClientResponse, Mono<String>> request = clientResponse -> {
    
        var sessionCookie = clientResponse.cookies().getFirst("SESSION");
    
        if (sessionCookie != null)
            session = sessionCookie.getValue();
    
        return clientResponse.bodyToMono(String.class);
    };
    
    private static String session = "";
    
    public static Response registration(String username, String password) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        return mapper.readValue(send(HttpMethod.POST,"user/registry", params), Response.class);
    }
    
    public static Response createTeam(Team team) throws JsonProcessingException {
        
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        var teamString = writer.writeValueAsString(team);
        params.add("team", teamString);
        var response = send(HttpMethod.POST, "create/team", params);
        invites(team.getParticipants(), team.getTitle());
        
        return mapper.readValue(response, Response.class);
    }
    
    private static String send(HttpMethod method, String url, LinkedMultiValueMap<String, String> params) {
        
        var client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultCookie("SESSION", session)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    
        RequestHeadersSpec<?> headersSpec;
        
        if(method.name().equals("POST")) {
            UriSpec<RequestBodySpec> uriSpec = client.post();
            RequestBodySpec bodySpec = uriSpec.uri(BASE_URL + url);
            headersSpec = bodySpec.body(BodyInserters.fromFormData(params));
        }
        else {
            headersSpec = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(url)
                            .replaceQueryParams(params)
                            .build()
                    );
        }
        String response;
        
        try {
            response = headersSpec.exchangeToMono(request).block();
        }
        catch (Exception exception) {
            response = headersSpec.exchangeToMono(request).block();
        }
        
        return response;
    }
    public static InfoResponse login(String username, String password) throws IOException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
    
        String send = send(HttpMethod.POST, "user/login", params);
        
        if(send == null)
            return getUserInfo();
        
        return mapper.readValue(send, InfoResponse.class);
    }
    public static Response logout(String username) throws JsonProcessingException {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        
        return mapper.readValue(send(HttpMethod.GET, "user/logout", params), Response.class);
    }
    
    public static InfoResponse getUserInfo() throws IOException {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String send = send(HttpMethod.GET, "/info", params);
    
        return mapper.readValue(send, InfoResponse.class);
    }
    
    public static Response createProcess(Process process) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("process", writer.writeValueAsString(process));
        
        return mapper.readValue(send(HttpMethod.POST, "create/process", params), Response.class);
    }
    
    public static void invite(String username, String title) {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("teamId", title);
        send(HttpMethod.POST,"invite", params);
    }
    
    public static Response updateStep(Step step) throws JsonProcessingException {
        
        var params = new LinkedMultiValueMap<String, String>();
        params.add("step", writer.writeValueAsString(step));
        return mapper.readValue(send(HttpMethod.POST, "update/step", params), Response.class);
    }
    
    public static Response updateTeam(Team team, Process process) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("team", writer.writeValueAsString(team));
        params.add("process", writer.writeValueAsString(process));
        
        return mapper.readValue(send(HttpMethod.POST, "update/team/addProcess", params), Response.class);
    }
    
    public static void invites(Set<String> usernames, String title) throws JsonProcessingException {
        
        var params = new LinkedMultiValueMap<String, String>();
        params.add("usernames", writer.writeValueAsString(usernames));
        params.add("teamId", title);
        send(HttpMethod.POST,"invite/many", params);
    }
    
    public static ExistResponse teamExists(String title) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("title", title);
        var result = send(HttpMethod.GET, "exist/team", params);
        
        return mapper.readValue(result, ExistResponse.class);
    }
    
    public static ExistResponse userExists(String username) throws JsonProcessingException {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
    
        return mapper.readValue(send(HttpMethod.GET, "exist/user", params), ExistResponse.class);
    }
    public static Response approve(Process process) throws JsonProcessingException {
        
        var params = new LinkedMultiValueMap<String, String>();
        params.add("processId", process.getId());
        
        return mapper.readValue(send(HttpMethod.GET, "step/approve", params), Response.class);
    }
    
    public static Response refuse(Process process) throws JsonProcessingException {
        
        var params = new LinkedMultiValueMap<String, String>();
        params.add("processId", process.getId());
        
        return mapper.readValue(send(HttpMethod.GET, "step/refuse", params), Response.class);
    }
    
    public static ExistResponse processExists(String title) throws JsonProcessingException {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("title", title);
    
        return mapper.readValue(send(HttpMethod.GET, "exist/process", params), ExistResponse.class);
    }
    public static Response refuseInvite(String username, String teamId) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("teamId", teamId);
    
        return mapper.readValue(send(HttpMethod.POST, "invite/refuse", params), Response.class);
    }
    
    public static ExistResponse documentExists(Document document) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("stepTitle", String.valueOf(document.getId().getStepTitle()));
        params.add("title", document.getTitle());
        
        return mapper.readValue(send(HttpMethod.GET, "exist/document", params), ExistResponse.class);
    }
    
    public static Response updateDocuments(Set<Document> documents) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("documents", writer.writeValueAsString(documents));
        
        return mapper.readValue(send(HttpMethod.POST, "update/documents", params), Response.class);
    }
    
    public static StepResponse getStepInfo(Step step) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("title", step.getTitle());
        params.add("processId", step.getProcessId());
        
        return mapper.readValue(send(HttpMethod.GET, "step", params), StepResponse.class);
    }
}