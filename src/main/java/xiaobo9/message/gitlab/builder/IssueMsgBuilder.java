package xiaobo9.message.gitlab.builder;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import xiaobo9.message.gitlab.GitlabService;
import xiaobo9.message.gitlab.bean.*;

import java.util.List;
import java.util.Optional;

/**
 * 问题
 *
 * @author renxb
 */
@Slf4j
@Getter
@Setter
public class IssueMsgBuilder extends MessageBuilder {
    private IssueEvent event;
    private GitlabService gitlabService;

    /**
     * 生成builder
     *
     * @param issueMessage  message
     * @param gitlabService gitlabService
     * @return builder
     */
    public static IssueMsgBuilder of(String issueMessage, GitlabService gitlabService) {
        IssueMsgBuilder builder = new IssueMsgBuilder();
        Gson gson = new Gson();
        IssueEvent event = gson.fromJson(issueMessage, IssueEvent.class);
        builder.setEvent(event);
        builder.setGitlabService(gitlabService);
        return builder;
    }

    @Override
    public Optional<Message> buildMessage() {
        Issue issue = event.getObjectAttributes();
        log.info("title: {}, state: {}, action: {}, url: {}", issue.getTitle(), issue.getState(), issue.getAction(), issue.getUrl());
        if ("closed".equals(issue.getState())) {
            log.info("{} 关闭了问题", event.getUser());
            return Optional.empty();
        }
        Optional<Assignee> optionalAssignee = getAssignee();
        return optionalAssignee.map(assignee -> {
            GitlabUser operationUser = event.getUser();
            String authorId = issue.getAuthorId();
            Message msg = new Message();

            msg.setTitle("问题");
            Optional<GitlabUser> user = gitlabService.getUser(event.getProject(), authorId);
            user.ifPresent(gitlabUser -> msg.setTitle(gitlabUser.getName() + " 开启的问题"));

            msg.setText(operationUser.getName() + " 在项目 " + event.getProject().getPathWithNamespace() + " 分配了 issue 给你\n");
            msg.setUrl(issue.getUrl());
            msg.addReceiver(assignee.getUsername(), assignee.getUsername());

            return msg;
        });
    }

    private Optional<Assignee> getAssignee() {
        Assignee assignee = event.getAssignee();
        if (assignee != null && StringUtils.isNotBlank(assignee.getUsername())) {
            return Optional.of(assignee);
        }
        List<Assignee> assignees = event.getAssignees();
        if (CollectionUtils.isNotEmpty(assignees)) {
            return Optional.of(assignees.get(0));
        }
        return Optional.empty();
    }
}
