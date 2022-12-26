package client.entity.process.document;

import client.entity.deserializer.DocumentIdDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ID для сущности "Документ", состоящее из наименования шага, на котором документ был создан, и названия документа
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = DocumentIdDeserializer.class)
public class DocumentId implements Serializable {
    
    private String stepTitle;
    
    private String title;
 
}