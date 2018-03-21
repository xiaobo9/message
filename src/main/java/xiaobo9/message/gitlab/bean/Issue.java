package xiaobo9.message.gitlab.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * issue
 *
 * @author renxb
 */
@Getter
@Setter
public class Issue {

    private Long id;

    private String title;

    @SerializedName("assignee_id")
    private String assigneeId;

    @SerializedName("author_id")
    private String authorId;

    private String description;

    private String state;

    private Long iid;

    @SerializedName("updated_by_id")
    private Long updatedById;

    private String action;

    private String url;
}
