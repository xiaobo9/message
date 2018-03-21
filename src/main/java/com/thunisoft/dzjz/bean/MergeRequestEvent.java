package com.thunisoft.dzjz.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author renxb
 */
@Data
public class MergeRequestEvent {
    private String objectKind;

    private User user;

    private Project project;

    @JsonProperty("object_attributes")
    @SerializedName("object_attributes")
    private ObjectAttributes objectAttributes;

    private Assignee assignee;

    /**
     * message
     * @return message
     */
    public Message toMessage() {
        User operationUser = this.getUser();
        StringBuilder builder = new StringBuilder(operationUser.getName()).append("在项目 ").append(this.getProject().getName())
                .append(" 开启了合并请求\n\n");
        builder.append("如果你不需要收到这条消息 或者 消息发送有误，请联系 任晓波\n");
        Message message = new Message();
        message.setTitle("");
        message.setText(builder.toString());
        return message;
    }

    /**
     * @author renxb
     */
    @Data
    public static class ObjectAttributes {
        private String id;

        @JsonProperty("target_branch")
        @SerializedName("target_branch")
        private String targetBranch;

        @JsonProperty("source_branch")
        @SerializedName("source_branch")
        private String sourceBranch;

        private String title;

        private String state;

        private String iid;

    }

    /**
     * @author renxb
     */
    @Data
    public static class Assignee {
        private String name;

        private String username;
    }

    /**
     * @author renxb
     */
    @Data
    public static class Message {
        private String receiver;

        private String title;

        private String text;

    }
}

