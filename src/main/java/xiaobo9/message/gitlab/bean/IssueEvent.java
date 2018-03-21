package xiaobo9.message.gitlab.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * issue event
 *
 * @author renxb
 */
@Getter
@Setter
public class IssueEvent {
    private String objectKind;

    private GitlabUser user;

    private Project project;

    @JsonProperty("object_attributes")
    @SerializedName("object_attributes")
    private Issue objectAttributes;

    private Assignee assignee;

    private List<Assignee> assignees;


}
