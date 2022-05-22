package xiaobo9.message.json.internel.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xiaobo9.message.json.JacksonAnnotationIntrospectorExtend;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name", visible = true)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = IEvent.PushEvent.class, name = "push"),
        @JsonSubTypes.Type(value = IEvent.MergeEvent.class, name = "merge"),
})
public interface IEvent {

    @Getter
    @Setter
    @ToString
    class PushEvent implements IEvent {
        private String name;
        private String author;
    }

    @Getter
    @Setter
    @ToString
    class MergeEvent implements IEvent {
        private String name;
        @JsonProperty("merge_user")
        private String mergeUser;
        @SerializedName("user_email")
        private String userEmail;
    }

    //    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name")
    public static interface IEvent2 {
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name", visible = true)
    public static class Event2 implements IEvent2 {
    }

    @Getter
    @Setter
    @ToString
    class PushEvent2 extends Event2 {
        private String name;
        private String author;
    }

    @Getter
    @Setter
    @ToString
    class MergeEvent2 extends Event2 {
        private String name;
        @JsonProperty("merge_user")
        private String mergeUser;
        @SerializedName("user_email")
        private String userEmail;
    }

    public static void main(String[] args) throws IOException {
        List<String> json = json();
        json.forEach(System.out::println);

        System.out.println("***********************");
        event(json);

        System.out.println("***********************");
        event2(json);
    }

    private static void event(List<String> strings) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospectorExtend());

        for (String s : strings) {
            System.out.println(mapper.readValue(s, IEvent.class));
        }
    }

    private static void event2(List<String> strings) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospectorExtend());
        mapper.registerSubtypes(Event2.class);
        mapper.registerSubtypes(new NamedType(PushEvent2.class, "push"));
        mapper.registerSubtypes(new NamedType(MergeEvent2.class, "merge"));
        for (String s : strings) {
            System.out.println(mapper.readValue(s, Event2.class));
        }
    }

    private static List<String> json() {
        String pushEvent = "{" +
                "\"name\":\"push\"," +
                "\"author\":\"push author\"" +
                "}";

        String mergeEvent = "{" +
                "\"name\":\"merge\"," +
                "\"merge_user\":\"merge user\"," +
                "\"user_email\":\"user email\"" +
                "}";

        return Arrays.asList(pushEvent, mergeEvent);
    }

}
