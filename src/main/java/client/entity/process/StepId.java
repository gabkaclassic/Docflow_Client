package client.entity.process;

import client.entity.deserializer.StepIdDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@JsonDeserialize(using = StepIdDeserializer.class)
public class StepId implements Serializable {
    
    private String processId;
    
    private String title;
}
