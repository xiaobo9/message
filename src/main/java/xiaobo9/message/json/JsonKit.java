package xiaobo9.message.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonKit {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        supportGsonAnnotation();
    }

    public static <T> T readValue(String jsonStr) throws IOException {
        return objectMapper.readValue(jsonStr, new TypeReference<T>() {
        });
    }

    public static <T> T readValue(String content, Class<T> valueType) throws IOException {
        return objectMapper.readValue(content, valueType);
    }

    public static <T> T readValue2(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeAsString(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }

    public static String writeAsStringIfErrorNull(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.info("", e);
            return null;
        }
    }

    /**
     * 支持 gson 注解
     */
    public static void supportGsonAnnotation() {
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospectorExtend());
    }

}
