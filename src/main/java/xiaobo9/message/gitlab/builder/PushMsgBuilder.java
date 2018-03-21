package xiaobo9.message.gitlab.builder;

import com.google.gson.Gson;
import xiaobo9.message.gitlab.bean.Commit;
import xiaobo9.message.gitlab.bean.Message;
import xiaobo9.message.gitlab.bean.PushEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 推送消息
 *
 * @author renxb
 */
@Slf4j
@Getter
@Setter
public class PushMsgBuilder extends MessageBuilder {
    private PushEvent event;

    /**
     * 生成builder
     *
     * @param pipelineEvent message
     * @return builder
     */
    public static PushMsgBuilder of(String pipelineEvent) {
        PushMsgBuilder builder = new PushMsgBuilder();
        Gson gson = new Gson();
        PushEvent event = gson.fromJson(pipelineEvent, PushEvent.class);
        builder.setEvent(event);
        return builder;
    }

    @Override
    public Optional<Message> buildMessage() {
        if (event.getTotalCommitsCount() == 0) {
            return Optional.empty();
        }

        Commit commit = event.getCommits().get(0);
        String email = commit.getAuthor().getEmail();
        if (email.endsWith("@company.com") || commit.getUrl().contains("open.company.com")) {
            return Optional.empty();
        }

        log.info("error commit, user: {}, commit user: {}, url: {}", event.getUserUserName(), commit.getAuthor(), commit.getUrl());
        Message msg = new Message();
        msg.setTitle("可能有误的 commit 信息");
        String message = "当前 commit 作者邮箱是: " + email + " 请改为公司统一的邮箱\n"
                + "可通过下面两条命令之一修改\n"
                + "git config user.email " + event.getUserUserName() + "@company.com\n"
                + "git config --global user.email " + event.getUserUserName() + "@company.com\n";
        msg.setText(message);
        msg.setUrl(commit.getUrl());
        msg.addReceiver(event.getUserUserName(), event.getUserUserName());
        return Optional.of(msg);
    }

}
