package client.sender;

import client.entity.Team;
import client.entity.process.Participant;
import client.response.ExistResponse;
import client.response.InfoResponse;
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
import java.util.function.Function;

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
    
    public static void registration(String username, String password) {
        
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        send(HttpMethod.POST,"user/registry", params);
    }
    
    public static void createTeam(String teamTitle, Participant leader, List<String> participants) throws JsonProcessingException {
    
        var writer = mapper.writer().withDefaultPrettyPrinter();
        var team = new Team();
        
        team.setTitle(teamTitle);
        team.setTeamLeaderId(leader.getId());
        participants.add(leader.getOwner().getUsername());
        team.addParticipants(participants);
        
        var params = new LinkedMultiValueMap();
        var teamString = writer.writeValueAsString(team);
        params.add("team", teamString);
        send(HttpMethod.POST, "create/team", params);
        invites(participants, teamTitle);
    }
    
    private static String send(HttpMethod method, String url, LinkedMultiValueMap<String, String> params) {
        
        var client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultCookie("SESSION", session)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    
        RequestHeadersSpec<?> headersSpec;
        
        if(method.name().equals("post")) {
            UriSpec<RequestBodySpec> uriSpec = client.post();
            RequestBodySpec bodySpec = uriSpec.uri(BASE_URL + url);
            headersSpec = bodySpec.body(BodyInserters.fromFormData(params));
        }
        else {
            headersSpec = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(url)
                            .queryParams(params)
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
    public static void login(String username, String password) {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        send(HttpMethod.POST,"user/login", params);
    }
    
    public static InfoResponse GetUserInfo() throws IOException {

        var params = new LinkedMultiValueMap();
        String send = send(HttpMethod.GET, "/info", params);
    
        return mapper.readValue(send, InfoResponse.class);
    }
    
    public static void createProcess(Process process) {
    
    
    }
    
    public static void invite(String username, String title) {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("teamId", title);
        send(HttpMethod.POST,"invite", params);
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
        var result = send(HttpMethod.GET, "exist/user", params);
    
        return mapper.readValue(result, ExistResponse.class);
    }
}