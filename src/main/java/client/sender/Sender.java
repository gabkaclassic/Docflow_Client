package client.sender;

import client.entity.Team;
import client.entity.process.Participant;
import client.response.InfoResponse;
import client.response.StepResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import java.io.IOException;
import java.util.List;


public class Sender {
    
    private static final String BASE_URL = "http://localhost:8080/";
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static String session = "";
    
    public static void registration(String username, String password) {
        
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        sendPost("user/registry", params);
        
    }
    
    public static void createTeam(String teamname, Participant leader) throws JsonProcessingException {
        
        // Как пример работы, реализация будет другой
        
        var team = new Team();
        team.setTitle(teamname);
        team.setTeamLeaderId(leader.getId());
        team.addParticipant(leader.getOwner().getUsername());
        leader.setTeams(List.of(team));
        
        var writer = mapper.writer().withDefaultPrettyPrinter();

        var params = new LinkedMultiValueMap();
        var teamString = writer.writeValueAsString(team);
        params.add("team", teamString);
        sendPost("/create/team", params);
        
    }
    
    private static String sendPost(String url, LinkedMultiValueMap<String, String> params) {
        
        var client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultCookie("SESSION", session)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        UriSpec<RequestBodySpec> uriSpec = client.post();
        RequestBodySpec bodySpec = uriSpec.uri(BASE_URL + url);
        RequestHeadersSpec<?> headersSpec = bodySpec.body(
                BodyInserters.fromFormData(params)
        );
        var response = headersSpec.exchangeToMono(clientResponse -> {
    
            var sessionCookie = clientResponse.cookies().getFirst("SESSION");
            
            if(sessionCookie != null)
                session = sessionCookie.getValue();
            
             return clientResponse.bodyToMono(String.class);
        });
    
        return response.block();
    }
    
    private static String sendGet(String url, LinkedMultiValueMap<String, String> params) {
        
        var client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultCookie("SESSION", session)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        UriSpec<RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        RequestBodySpec bodySpec = uriSpec.uri(BASE_URL + url);
        RequestHeadersSpec<?> headersSpec = bodySpec.body(
                BodyInserters.fromFormData(params)
        );
        var response = headersSpec.exchangeToMono(clientResponse -> {
            
            var sessionCookie = clientResponse.cookies().getFirst("SESSION");
            
            if(sessionCookie != null)
                session = sessionCookie.getValue();
            
            return clientResponse.bodyToMono(String.class);
        });

        return response.block();
    }
    
    public static void login(String username, String password) {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        try {
            sendPost("user/login", params);
        }
        catch (Exception e) {
            sendPost("user/login", params);
        }
    }
//    123456?C
    public static InfoResponse GetUserInfo() throws IOException {

        var params = new LinkedMultiValueMap();
        String send = sendGet("/info", params);
        var response = mapper.readValue(send, InfoResponse.class);
        return response;


    }
    
    public static void createProcess(Process process) {
    

    }
    
    public static void invite(String username, String title) {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("teamId", title);
        sendPost("invite", params);
    }
}