package client.entity.process;

import client.entity.deserializer.ProcessDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(using = ProcessDeserializer.class)
public class Process {
    

    private Long id;
    
    private String title;
    private List<Step> steps;

    private Step currentStep;
    
    public void nextStep() {
        
        currentStep = steps.stream()
                    .filter(step -> step.getNumber() == currentStep.getNumber() + 1)
                    .findFirst().orElse(null);
    }
    
    public void previousStep() {
        currentStep = steps.stream()
                .filter(step -> step.getNumber() == currentStep.getNumber() - 1)
                .findFirst().orElse(null);
    }
    
    public boolean started() {
        return steps.stream()
                .noneMatch(step -> step.getNumber() == currentStep.getNumber() - 1);
    }
    
    public boolean finished() {
        
        return steps.stream()
                .noneMatch(step -> step.getNumber() == currentStep.getNumber() + 1);
    }
}