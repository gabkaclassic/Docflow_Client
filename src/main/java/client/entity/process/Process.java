package client.entity.process;

import client.entity.deserializer.ProcessDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@JsonDeserialize(using = ProcessDeserializer.class)
public class Process {
    
    private String id;
    
    private String title;
    
    private List<Step> steps;
    
    private Integer currentStep;
    
    public Step nextStep() {
        
        var curr = steps.stream()
                .filter(step -> Objects.equals(step.getNumber(), currentStep))
                .findFirst().orElse(null);
        var next = steps.stream()
                .filter(step -> step.getNumber() == currentStep + 1)
                .findFirst().orElse(null);
        
        if(next != null) {
            next.addDocuments(curr.getDocuments());
            currentStep = next.getNumber();
        }
        
        return next;
    }
    public Step previousStep() {
        currentStep = steps.stream()
                .map(Step::getNumber)
                .filter(n -> n == currentStep - 1)
                .findFirst().orElse(currentStep);
        
        return steps.stream()
                .filter(s -> Objects.equals(s.getNumber(), currentStep))
                .findFirst().orElse(null);
    }
    
    public Step currentStep() {
        
        return steps.stream()
                .filter(s -> Objects.equals(s.getNumber(), currentStep))
                .findFirst().orElse(null);
    }
    
    public boolean started() {
        return steps.stream()
                .noneMatch(step -> step.getNumber() == currentStep - 1);
    }
    public boolean finished() {
        
        return steps.stream()
                .noneMatch(step -> step.getNumber() == currentStep + 1);
    }
    
    public boolean checkRules(Participant participant) {
        
        var rule = currentStep().getRules().get(participant.getUsername());
        
        return rule != null && rule.getLevel() > Rules.READ.getLevel();
    }
}