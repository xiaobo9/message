package xiaobo9.message.gitlab.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 合并请求事件 bean
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public class MergeRequestEvent {
    private String objectKind;

    private GitlabUser user;

    private Project project;

    @JsonProperty("object_attributes")
    @SerializedName("object_attributes")
    private MergeRequest objectAttributes;

    private Assignee assignee;

    private Changes changes;

    /**
     * 合并请求事件的状态
     */
    public enum MergeReqState {
        /**
         * 开启
         */
        OPENED,
        //
        /**
         * 已合并
         */
        MERGED,
        //
        /**
         * 关闭
         */
        CLOSED;

        private static Map<String, MergeReqState> map;

        static {
            map = new HashMap<>();
            MergeReqState[] values = MergeReqState.values();
            for (MergeReqState tmp : values) {
                map.put(tmp.name().toLowerCase(), tmp);
            }
        }

        /**
         * 状态
         *
         * @param state 状态
         * @return 状态
         */
        public static MergeReqState of(String state) {
            return map.get(state);
        }
    }

    /**
     * mr 的变化信息
     */
    @Getter
    @Setter
    @ToString
    public static class Changes {
        @SerializedName("assignee")
        private AssigneeChange assignee;

        private Description description;
    }

    /**
     * 被指定的合并操作者的信息变化
     */
    @Getter
    @Setter
    @ToString
    public static class AssigneeChange {
        private GitlabUser previous;
        private GitlabUser current;
    }

    /***
     * changes 的 描述信息
     * @author renxb
     */
    @Getter
    @Setter
    @ToString
    public static class Description {
        private String previous;
        private String current;
    }
}

