package xiaobo9.message.gitlab.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 评论信息
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public class PushEvent {
    @SerializedName("object_kind")
    private String objectKind;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_username")
    private String userUserName;

    /**
     * 事件所属的项目
     */
    private Project project;

    private List<Commit> commits;

    @SerializedName("total_commits_count")
    private int totalCommitsCount;

}
