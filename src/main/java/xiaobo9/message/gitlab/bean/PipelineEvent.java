package xiaobo9.message.gitlab.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 流水线事件的信息
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public class PipelineEvent {
    private String objectKind;

    /**
     * 触发流水线的人
     */
    private GitlabUser user;

    /**
     * 项目信息
     */
    private Project project;

    /**
     * 流水线事件本身的信息
     */
    @SerializedName("object_attributes")
    private ObjectAttributes objectAttributes;

    /**
     * 流水线的build信息
     */
    private List<Build> builds;

    private Commit commit;

    /**
     * 流水线事件本身的信息
     *
     * @author renxb
     */
    @Getter
    @Setter
    @ToString
    public static class ObjectAttributes {
        private String id;

        private String ref;

        private String status;

        @SerializedName("detailed_status")
        private String detailedStatus;

        private List<String> stages;

        private Long duration;
    }

    /**
     * 流水线的build信息
     */
    @Getter
    @Setter
    @ToString
    public static class Build {
        private Long id;

        private String stage;

        private String name;

        private String status;

        private boolean manual;

        private GitlabUser user;
    }

}

