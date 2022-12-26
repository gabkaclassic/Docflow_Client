package client.sender;

import client.entity.team.Invite;
import client.entity.team.Team;
import client.entity.process.Process;
import client.entity.process.step.Step;
import client.entity.process.document.Document;
import client.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Класс-реактивный-клиент для обмена информацией с сервером
 * */
public class Sender {
    
    private static String BASE_URL = System.getenv("BASE_URL");
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
    
    private static final Function<ClientResponse, Mono<String>> request = clientResponse -> {
    
        var sessionCookie = clientResponse.cookies().getFirst("SESSION");
    
        if (sessionCookie != null)
            session = sessionCookie.getValue();
    
        return clientResponse.bodyToMono(String.class);
    };
    
    private static String session = "";

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static Response registration(String username, String password) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        return mapper.readValue(send(HttpMethod.POST,"user/registry", params), Response.class);
    }
    
    public static Response createTeam(Team team, List<String> participants, String teamLeaderNick) throws JsonProcessingException {
        
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        var teamString = writer.writeValueAsString(team);
        params.add("team", teamString);
        var response = send(HttpMethod.POST, "create/team", params);
        
        new Thread(() -> {
            try {
                invites(participants, team.getTitle());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        ).start();
        
        return mapper.readValue(response, Response.class);
    }
    
    /**
     * Общий шаблон всех запросов
     * */
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
    
        String response = send(HttpMethod.POST, "user/login", params);
        
        if(response == null) {
            return getUserInfo();
        }
        
        return mapper.readValue(response, InfoResponse.class);
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
    
    public static Response invite(String username, String title) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("teamId", title);
        return mapper.readValue(send(HttpMethod.POST,"invite", params), Response.class);
    }
    
    public static Response updateStep(Step step) throws JsonProcessingException {
        
        var params = new LinkedMultiValueMap<String, String>();
        params.add("step", writer.writeValueAsString(step));
        return mapper.readValue(send(HttpMethod.POST, "update/step", params), Response.class);
    }
    public static Response createProcess(Team team, Process process) throws JsonProcessingException {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("team", writer.writeValueAsString(team));
        params.add("process", writer.writeValueAsString(process));
        
        return mapper.readValue(send(HttpMethod.POST, "update/team/addProcess", params), Response.class);
    }
    
    public static void invites(List<String> usernames, String title) throws JsonProcessingException {
        
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
    public static Response kickParticipant(String username, String teamId) throws JsonProcessingException {
    
        var  params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("teamId", teamId);
        return mapper.readValue(send(HttpMethod.POST, "invite/kick", params), Response.class);
    }
    
    public static Response accessInvite(Invite invite) throws JsonProcessingException {
        
        var  params = new LinkedMultiValueMap<String, String>();
        params.add("inviteId", invite.getId().toString());
        return mapper.readValue(send(HttpMethod.POST, "invite/access", params), Response.class);
    }
    
    public static Response refuseInvite(Invite invite) throws JsonProcessingException {
        
        var  params = new LinkedMultiValueMap<String, String>();
        params.add("inviteId", invite.getId().toString());
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
    
    public static ProcessResponse getProcessInfo(Process process) throws JsonProcessingException {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("processId", process.getId());
    
        return mapper.readValue(send(HttpMethod.GET, "info/process", params), ProcessResponse.class);
    }
    
    public static Response finishProcess(Process process) throws JsonProcessingException {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("processId", process.getId());
    
        return mapper.readValue(send(HttpMethod.POST, "update/process/finish", params), Response.class);
    }
}