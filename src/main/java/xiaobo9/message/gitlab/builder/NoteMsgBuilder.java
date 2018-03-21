package xiaobo9.message.gitlab.builder;

import com.google.gson.Gson;
import xiaobo9.message.gitlab.GitlabService;
import xiaobo9.message.gitlab.bean.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 评论信息
 *
 * @author renxb
 */
@Slf4j
@Getter
@Setter
public class NoteMsgBuilder extends MessageBuilder {
    private NoteEvent event;
    private GitlabService gitlabService;

    /**
     * 生成builder
     *
     * @param pipelineEvent message
     * @param gitlabService gitlab 服务
     * @return builder
     */
    public static NoteMsgBuilder of(String pipelineEvent, GitlabService gitlabService) {
        NoteMsgBuilder builder = new NoteMsgBuilder();
        Gson gson = new Gson();
        NoteEvent event = gson.fromJson(pipelineEvent, NoteEvent.class);
        builder.setEvent(event);
        builder.setGitlabService(gitlabService);
        return builder;
    }

    @Override
    public Optional<Message> buildMessage() {
        NoteEvent.ObjectAttributes attributes = event.getObjectAttributes();
        String noteableType = attributes.getNoteableType();
        if (noteableType.equalsIgnoreCase("MergeRequest")) {
            return mergeNote(attributes);
        } else if ("Issue".equals(noteableType)) {
            return issueNote(attributes);
        } else if ("Commit".equalsIgnoreCase(noteableType)) {
            return commitNote(attributes);
        }
        return Optional.empty();
    }

    private Optional<Message> mergeNote(NoteEvent.ObjectAttributes attributes) {
        String note = attributes.getNote();
        Set<String> users = this.getUserFromAt(note);

        // 合并请求的作者
        String authorId = event.getMergeRequest().getAuthorId();
        gitlabService.getUser(event.getProject(), authorId).ifPresent(user -> users.add(user.getUsername()));

        // 评论的作者
        GitlabUser user = event.getUser();
        log.info("merge note, author: {}, users: {}", user.getUsername(), users);
        // 过滤掉这条评论的作者
        List<String> list = users.stream().filter(tmp -> !tmp.equals(user.getUsername())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(list)) {
            Message msg = new Message();
            msg.setTitle("note event of merge request");
            msg.setUrl(attributes.getUrl());
            msg.setText("merge request title: " + event.getMergeRequest().getTitle() + "\n" + user.getName() + " 说:\n" + note);

            list.forEach(tmp -> msg.addReceiver(tmp, tmp));
            return Optional.of(msg);
        }
        return Optional.empty();
    }

    private Optional<Message> issueNote(NoteEvent.ObjectAttributes attributes) {
        String note = attributes.getNote();

        Set<String> users = this.getUserFromAt(note);

        // 问题的作者
        Issue issue = event.getIssue();
        log.info("issue note, title: {}, state: {}, action: {}, url: {}",
                issue.getTitle(), issue.getState(), issue.getAction(), issue.getUrl());
        String authorId = issue.getAuthorId();
        gitlabService.getUser(event.getProject(), authorId).ifPresent(user -> users.add(user.getUsername()));

        // 评论的作者
        GitlabUser user = event.getUser();
        log.info("author: {}, users: {}", user.getUsername(), users);
        // 过滤掉这条评论的作者
        List<String> list =
                users.stream().filter(tmp -> StringUtils.isNotBlank(tmp) && !tmp.equals(user.getUsername()))
                        .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(list)) {
            String title = "note event of issue";
            Message msg = message(attributes, user, note, title);
            list.forEach(tmp -> msg.addReceiver(tmp, tmp));
            return Optional.of(msg);
        }
        return Optional.empty();
    }


    private Optional<Message> commitNote(NoteEvent.ObjectAttributes attributes) {
        Commit.Author author = event.getCommit().getAuthor();

        String authorId = StringUtils.substringBefore(author.getEmail(), "@");

        // 评论的作者
        GitlabUser eventUser = event.getUser();
        log.info("commit note, author: {}, users: {}", eventUser.getUsername(), authorId);

        String note = attributes.getNote();
        note = StringUtils.substringBefore(note, "Note: ");

        String title = "note event of commit";
        Message msg = message(attributes, eventUser, note, title);
        msg.addReceiver(authorId, authorId);
        return Optional.of(msg);
    }

    private Message message(NoteEvent.ObjectAttributes attributes, GitlabUser eventUser, String note, String title) {
        Message msg = new Message();
        msg.setTitle(title);
        msg.setUrl(attributes.getUrl());
        msg.setText(eventUser.getName() + " 说:\n" + note);
        return msg;
    }
}
