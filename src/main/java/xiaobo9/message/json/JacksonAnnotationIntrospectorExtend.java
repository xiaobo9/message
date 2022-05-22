package xiaobo9.message.json;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.google.gson.annotations.SerializedName;

public class JacksonAnnotationIntrospectorExtend extends JacksonAnnotationIntrospector {
    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        SerializedName annotation = a.getAnnotation(SerializedName.class);
        if (annotation != null) {
            String value = annotation.value();
            if (!value.isEmpty()) {
                return PropertyName.construct(value);
            }
        }
        return super.findNameForDeserialization(a);
    }
}
