package xiaobo9.message.gitlab.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 合并请求信息
 *
 * @author renxb
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class MergeRequest {
    private String id;

    /**
     * 合并请求的目标分支
     */
    @SerializedName("target_branch")
    private String targetBranch;

    /**
     * 合并请求的源分支
     */
    @SerializedName("source_branch")
    private String sourceBranch;

    /**
     * 合并请求作者的id
     */
    @SerializedName("author_id")
    private String authorId;

    /**
     * 合并请求的标题
     */
    private String title;

    /**
     * 合并请求的状态
     */
    private String state;

    private String iid;

    /**
     * 合并请求的源分支信息
     */
    private Branch source;

    private String action;

    @SerializedName("merge_params")
    private MergeParams mergeParams;

    /**
     * 分支信息
     */
    @Getter
    @Setter
    @ToString
    public static class Branch {
        /**
         * 分支名称
         */
        private String name;

        /**
         * 分支所在的项目名称
         */
        @SerializedName("web_url")
        private String webUrl;
    }

    /**
     * 合并请求参数
     */
    @Getter
    @Setter
    @ToString
    public static class MergeParams {
        /**
         * 删除原分支
         */
        @SerializedName("force_remove_source_branch")
        private String forceRemoveSourceBranch;

        /**
         * 是否会自动删除原分支
         *
         * @return 是否
         */
        public boolean willAutoRemoveSourceBranch() {
            return "1".equals(forceRemoveSourceBranch);
        }
    }

}
