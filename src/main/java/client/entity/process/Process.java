package client.entity.process;

import client.entity.deserializer.ProcessDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@JsonDeserialize(using = ProcessDeserializer.class)
public class Process {
    

    private Long id;
    
    private String title;
    private List<Step> steps;

    private Step currentStep;
    
    public boolean nextStep() {
        
        return Objects.nonNull(currentStep = steps.stream()
                    .filter(step -> step.getNumber() == currentStep.getNumber() + 1)
                    .findFirst().orElse(null)
        );
        
    }
}