package client.entity.process;

import client.entity.deserializer.ProcessDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(using = ProcessDeserializer.class)
public class Process {
    
    private String id;
    
    private String title;
    
    private List<Step> steps;
    
    private Integer currentStep;
    
    public void nextStep() {
        
        var curr = steps.stream()
                .filter(step -> step.getNumber() == currentStep)
                .findFirst().orElse(null);
        var next = steps.stream()
                .filter(step -> step.getNumber() == currentStep + 1)
                .findFirst().orElse(null);
        
        if(next == null)
            return;
        
        next.addDocuments(curr.getDocuments());
        currentStep = next.getNumber();
    }
    public void previousStep() {
        currentStep = steps.stream()
                .map(Step::getNumber)
                .filter(n -> n == currentStep - 1)
                .findFirst().orElse(currentStep);
    }
    
    public boolean started() {
        return steps.stream()
                .noneMatch(step -> step.getNumber() == currentStep - 1);
    }
    public boolean finished() {
        
        return steps.stream()
                .noneMatch(step -> step.getNumber() == currentStep + 1);
    }
}