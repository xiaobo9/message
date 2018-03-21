package xiaobo9.message.gitlab.builder;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import xiaobo9.message.gitlab.GitlabService;
import xiaobo9.message.gitlab.bean.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;
import java.util.Set;

/**
 * 合并请求构建器
 *
 * @author renxb
 */
@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MergeRequestMsgBuilder extends MessageBuilder {

    private MergeRequestEvent event;

    private Project project;

    private GitlabService gitlabService;

    /**
     * 生成builder
     *
     * @param mergeRequestMessage message
     * @param gitlabService       gitlab 服务
     * @return builder
     */
    public static MergeRequestMsgBuilder of(String mergeRequestMessage, GitlabService gitlabService) {
        MergeRequestMsgBuilder builder = new MergeRequestMsgBuilder();
        Gson gson = new Gson();
        MergeRequestEvent event = gson.fromJson(mergeRequestMessage, MergeRequestEvent.class);
        builder.setEvent(event);
        builder.setGitlabService(gitlabService);
        builder.setProject(event.getProject());
        return builder;
    }

    /**
     * TODO 对于 fork 模式下的合并请求可能需要做些另外的处理
     *
     * @return Optional<Message> 消息
     */
    @Override
    public Optional<Message> buildMessage() {

        MergeRequestEvent.MergeReqState state = MergeRequestEvent.MergeReqState.of(this.event.getObjectAttributes().getState());
        switch (state) {
            case OPENED:
                // TODO 在 redis记录下，A 让 B 合并
                return openMergeRequestMsg();
            case MERGED:
                return mergedMergeRequestMsg();
            case CLOSED:
            default:
                break;
        }
        return Optional.empty();
    }

    private Optional<Message> openMergeRequestMsg() {
        MergeRequestEvent.Changes changes = event.getChanges();
        String action = event.getObjectAttributes().getAction();
        if (StringUtils.isBlank(action) || "open".equalsIgnoreCase(action)) {
            Assignee assignee = event.getAssignee();
            if (assignee == null) {
                return Optional.empty();
            }
            GitlabUser operationUser = event.getUser();

            Message msg = newMessage(operationUser.getName() + " 指定给你的合并请求");

            msg.addReceiver(assignee.getUsername(), assignee.getUsername());

            String message = operationUser.getName() + "在项目 " + project.getPathWithNamespace() + " 开启了合并请求\n";
            if (operationUser.getUsername().equals(assignee.getUsername())) {
                message = message + "你咋把合并请求提给自己了呢？\n";
            }
            msg.setText(message);

            return Optional.of(msg);
        } else {

            MergeRequestEvent.AssigneeChange assignees = changes.getAssignee();
            if (assignees == null) {
                return Optional.empty();
            }
            Assignee assignee = event.getAssignee();
            if (assignee == null) {
                return Optional.empty();
            }
            GitlabUser operationUser = event.getUser();

            Message msg = newMessage(String.format("项目 %s 中合并请求变更", project.getPathWithNamespace()));

            String previous = assignees.getPrevious() == null ? "" : assignees.getPrevious().getName();
            String current = assignees.getCurrent().getName();
            msg.addReceiver(assignee.getUsername(), assignee.getUsername());

            String message = String.format("%s 在项目 %s 的合并请求从 %s 指定给了 %s。",
                    operationUser.getName(), project.getPathWithNamespace(), previous, current);
            message = message + "\n";
            msg.setText(message);

            return Optional.of(msg);
        }
    }

    private Optional<Message> mergedMergeRequestMsg() {
        MergeRequest attributes = event.getObjectAttributes();
        if (!"merge".equals(attributes.getAction())) {
            return Optional.empty();
        }

        Optional<GitlabUser> authorOptional = gitlabService.getUser(project, attributes.getAuthorId());
        if (!authorOptional.isPresent()) {
            return Optional.empty();
        }

        String authorUserName = authorOptional.get().getUsername();
        GitlabUser operationUser = event.getUser();

        Message msg = newMessage("分支 " + attributes.getSourceBranch() + " 已被合并");
        String message = authorUserName + " 在项目 " + project.getPathWithNamespace() + " 的合并请求已被 " + operationUser.getName() + " 合并。\n"
                + "源分支：" + attributes.getSourceBranch() + "\n"
                + "目的分支：" + attributes.getTargetBranch() + "\n";

        msg.setText(message);
        msg.addReceiver(authorUserName, authorUserName);

        Set<String> set = Sets.newHashSet(authorUserName, event.getUser().getUsername());
        gitlabService.participants(event).stream()
                .filter(user -> !set.contains(user.getUsername()))
                .forEach(user -> msg.addReceiver(user.getUsername(), user.getUsername()));

        return Optional.of(msg);
    }

    private Message newMessage(String title) {
        Message msg = new Message();
        msg.setUrl(project.getWebUrl() + "/merge_requests/" + event.getObjectAttributes().getIid());
        msg.setTitle(title);
        return msg;
    }

}
