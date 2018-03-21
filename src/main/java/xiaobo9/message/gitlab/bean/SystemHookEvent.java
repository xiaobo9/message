package xiaobo9.message.gitlab.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * system hook event
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public class SystemHookEvent {
    @SerializedName("object_kind")
    private String objectKind;

    @SerializedName("event_name")
    private String eventName;

    @SerializedName("project_path_with_namespace")
    private String projectPathWithNamespace;

    @SerializedName("user_username")
    private String userName;

    @SerializedName("created_at")
    private String createdAt;

    private String name;

    private String path;

    @SerializedName("path_with_namespace")
    private String pathWithNamespace;

    @SerializedName("project_id")
    private String projectId;
}