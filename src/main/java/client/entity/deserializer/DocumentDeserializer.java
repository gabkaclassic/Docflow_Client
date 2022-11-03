package client.entity.deserializer;

import client.entity.process.document.Document;
import client.entity.process.document.DocumentId;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.stream.Collectors;

public class DocumentDeserializer extends StdDeserializer<Document> {
    
    public DocumentDeserializer() {
        
        this(null);
    }
    
    public DocumentDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public Document deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        var document = new Document();
        document.setId(JSONUtils.getObject(node, "id", DocumentId.class));
        document.setFile(JSONUtils.getObject(node, "file", byte[].class));
        document.setFormat(node.get("format").textValue());
        document.setComments(JSONUtils.splitObjects(node, "comments", String.class)
                .collect(Collectors.toList())
        );
        
        document.setResources(JSONUtils.splitObjects(node, "resources", String.class)
                .collect(Collectors.toList())
        );
        
        return document;
    }
    
}