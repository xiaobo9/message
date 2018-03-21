package xiaobo9.message.gitlab.builder;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import xiaobo9.message.gitlab.bean.Message;
import xiaobo9.message.gitlab.bean.PipelineEvent;
import xiaobo9.message.gitlab.bean.Project;
import xiaobo9.message.gitlab.bean.GitlabUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
public class PipelineMsgBuilder extends MessageBuilder {

    public static final String MERGE_REQUEST = "See merge request";
    private PipelineEvent event;

    private static Map<String, Builder> builders = Maps.newHashMap();

    static {
        builders.put("success", new PassedBuilder());
        builders.put("failed", new FailedBuilder());
    }

    /**
     * 生成builder
     *
     * @param pipelineEvent message
     * @return builder
     */
    public static PipelineMsgBuilder of(String pipelineEvent) {
        PipelineMsgBuilder builder = new PipelineMsgBuilder();
        Gson gson = new Gson();
        PipelineEvent event = gson.fromJson(pipelineEvent, PipelineEvent.class);
        builder.setEvent(event);
        return builder;
    }

    @Override
    public Optional<Message> buildMessage() {
        Builder builder = builders.get(event.getObjectAttributes().getStatus());

        return Optional.ofNullable(builder).flatMap(tmp -> tmp.build(event));
    }


    /**
     * builder
     */
    interface Builder {
        /**
         * 构建 pipeline 消息
         *
         * @param event 时间
         * @return 消息
         */
        Optional<Message> build(PipelineEvent event);

        /**
         * 拼接 url 和 接收者 信息
         *
         * @param msg     消息
         * @param event   事件信息
         * @param project 项目信息
         */
        default void urlAndReceiver(Message msg, PipelineEvent event, Project project) {
            String url = project.getWebUrl() + "/pipelines/" + event.getObjectAttributes().getId();
            msg.setUrl(url);

            GitlabUser user = event.getUser();
            msg.addReceiver(user.getUsername(), user.getUsername());
        }
    }

    static class PassedBuilder implements Builder {

        @Override
        public Optional<Message> build(PipelineEvent event) {
            String message = event.getCommit().getMessage();
            if (message.contains(MERGE_REQUEST)) {
                // TODO 如果是 gitlab 上合并触发的流水线，可以根据这个合并信息，找到 merge request， 然后同时提醒 MR 作者构建通过了
                log.info("pipeline_commit_message: {}", message);
            }
            Message msg = new Message();

            Project project = event.getProject();
            msg.setTitle("恭喜，在项目 " + project.getPathWithNamespace() + " 的构建完成了");

            // url 和 接收者
            urlAndReceiver(msg, event, project);

            String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");

            String msgText = date + "\n"
                    + event.getUser().getName() + " 在项目 " + project.getPathWithNamespace() + " 的构建完成了\n"
                    + "分支名：" + event.getObjectAttributes().getRef();

            if (event.getCommit() != null) {
                msgText = msgText + ", commit id: " + event.getCommit().getId();
            }
            msgText = msgText + "\n";

            msg.setText(msgText);

            return Optional.of(msg);
        }
    }

    static class FailedBuilder implements Builder {

        @Override
        public Optional<Message> build(PipelineEvent event) {
            List<PipelineEvent.Build> builds = event.getBuilds();
            for (PipelineEvent.Build build : builds) {
                if ("failed".equals(build.getStatus())) {
                    PipelineEvent.ObjectAttributes objectAttributes = event.getObjectAttributes();
                    Message msg = new Message();
                    msg.setTitle("Oh no!!! 你的构建失败了");

                    Project project = event.getProject();

                    // url 和 接收者
                    urlAndReceiver(msg, event, project);

                    String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
                    String msgText = date + "\n"
                            + build.getUser().getName() + " 在项目 " + project.getPathWithNamespace() + " 的构建失败了\n"
                            + "分支名：" + objectAttributes.getRef() + "\n"
                            + "失败阶段：" + build.getStage() + "\n";

                    msg.setText(msgText);

                    return Optional.of(msg);
                }
            }
            return Optional.empty();
        }
    }


}
