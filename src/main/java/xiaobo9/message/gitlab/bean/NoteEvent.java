package xiaobo9.message.gitlab.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 评论信息
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public class NoteEvent {
    private String objectKind;

    /**
     * 事件的作者
     */
    private GitlabUser user;

    /**
     * 事件所属的项目
     */
    private Project project;


    /**
     * 事件本身的属性
     */
    @SerializedName("object_attributes")
    private ObjectAttributes objectAttributes;

    /**
     * 事件所针对的合并请求
     */
    @SerializedName("merge_request")
    private MergeRequest mergeRequest;
    /**
     * 事件所针对的合并请求
     */
    @SerializedName("issue")
    private Issue issue;

    private Commit commit;

    /**
     * 事件本身的属性
     *
     * @author renxb
     */
    @Getter
    @Setter
    @ToString
    public static class ObjectAttributes {
        private String id;

        /**
         * 评论的内容
         */
        private String note;

        /**
         * 评论的类型，合并请求，或者 问题 等等
         */
        @SerializedName("noteable_type")
        private String noteableType;

        /**
         * 评论的作者
         */
        @SerializedName("author_id")
        private String authorId;

        /**
         * 评论的子类型？MERGE_REQUEST 子类型可以是 NOTE_TYPE
         */
        private String type;

        /**
         * 评论的url
         */
        private String url;
    }

    /**
     * 咋写
     *
     * @author renxb
     */
    public enum NoteableType {
        /**
         * 合并请求
         */
        MERGE_REQUEST;
    }

    /**
     * 注释类型
     *
     * @author renxb
     */
    public enum NoteType {
        /**
         * diff note
         */
        NOTE_TYPE;
    }
}
