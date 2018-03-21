package xiaobo9.message.gitlab.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * hook 信息
 *
 * @author renxb
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class WebHook {
    private Integer id;

    private String url;

    @JsonProperty("project_id")
    private Integer projectId;

    @JsonProperty("push_events")
    private Boolean pushEvents;

    @JsonProperty("issues_events")
    private Boolean issuesEvents;

    @JsonProperty("confidential_issues_events")
    private Boolean confidentialIssuesEvents;

    @JsonProperty("merge_requests_events")
    private Boolean mergeRequestsEvents;

    @JsonProperty("tag_push_events")
    private Boolean tagPushEvents;

    @JsonProperty("note_events")
    private Boolean noteEvents;

    @JsonProperty("job_events")
    private Boolean jobEvents;

    @JsonProperty("pipeline_events")
    private Boolean pipelineEvents;

    @JsonProperty("enable_ssl_verification")
    private Boolean enableSslVerification = false;

    @JsonProperty("created_at")
    private String createdAt;
}