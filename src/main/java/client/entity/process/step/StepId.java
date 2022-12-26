package client.entity.process.step;

import client.entity.deserializer.StepIdDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ID для сущности "Шаг", состоящее их наименования шага и ID процесса, в котором он был создан
 * */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@JsonDeserialize(using = StepIdDeserializer.class)
public class StepId implements Serializable {
    
    private String processId;
    
    private String title;
}
