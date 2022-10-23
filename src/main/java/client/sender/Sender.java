package client.sender;

import client.entity.Team;
import client.entity.process.Participant;
import client.response.InfoResponse;
import client.response.StepResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
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
        send("user/registry", params);
        
    }
    
    public static void createTeam(String teamname, long id, Participant leader) throws JsonProcessingException {
        
        // Как пример работы, реализация будет другой
        
        var team = new Team();
        var participant = new Participant();
        participant.setId(id);
        team.setTitle(teamname);
        team.setTeamLeader(leader);
        //team.addParticipant(participant);
        var writer = mapper.writer().withDefaultPrettyPrinter();

        var params = new LinkedMultiValueMap();
        var teamString = writer.writeValueAsString(team);
        params.add("team", teamString);
        send("/create/team", params);
        
    }
    
    private static JsonParser send(String url, LinkedMultiValueMap<String, String> params) {
        
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
    
        System.out.println(response.block());
        return null;
    }
    
    public static void login(String username, String password) {
    
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        send("user/login", params);
    }
    public static InfoResponse GetUserInfo(String username) throws IOException {

        var params = new LinkedMultiValueMap();
        params.add("username", username);
        var response = mapper.readValue(send("/info", params), InfoResponse.class);
        return response;


    }
    
    public static void createProcess(Process process) {
    

    }
    
    public static void invite(String username, Long teamId) {
    
//        var params = new LinkedMultiValueMap<String, String>();
//        params.add("username", username);
//        params.add("teamId", String.valueOf(teamId));
    }
}