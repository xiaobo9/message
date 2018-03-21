package xiaobo9.message.gitlab.builder;

import com.google.common.collect.Maps;
import xiaobo9.message.gitlab.GitlabService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * 构造器工厂
 *
 * @author renxb
 */
@Slf4j
@Service
public class BuilderFactory implements InitializingBean {
    private final GitlabService gitlabService;

    /**
     * constructor
     *
     * @param gitlabService gitlab service
     */
    public BuilderFactory(GitlabService gitlabService) {
        this.gitlabService = gitlabService;
    }

    private static Map<String, IBuilderFactory> map = Maps.newHashMap();

    /**
     * builder
     *
     * @param hookName hook name
     * @param message  message
     * @return builder optional
     */
    public Optional<MessageBuilder> getBuilder(String hookName, String message) {
        return Optional
                .ofNullable(map.get(hookName))
                .map(factory -> factory.generateBuilder(message));
    }

    @Override
    public void afterPropertiesSet() {
        addFactory(new IssueFactory(gitlabService));
        addFactory(new MergeRequestFactory(gitlabService));
        addFactory(new PipelineFactory());
        addFactory(new NoteFactory(gitlabService));
        addFactory(new PushFactory());
    }

    private void addFactory(IBuilderFactory factory) {
        map.put(factory.name(), factory);
    }

    /**
     * 构建器工厂接口
     *
     * @author renxb
     */
    public interface IBuilderFactory {
        /**
         * 工厂名字
         *
         * @return 工厂名字
         */
        String name();

        /**
         * 生成 builder
         *
         * @param message 消息内容
         * @return 生成 builder
         */
        MessageBuilder generateBuilder(String message);

    }

    /**
     * issue factory
     *
     * @author renxb
     */
    public static class IssueFactory implements IBuilderFactory {
        GitlabService gitlabService;

        IssueFactory(GitlabService gitlabService) {
            this.gitlabService = gitlabService;
        }

        @Override
        public String name() {
            return "Issue Hook";
        }

        @Override
        public MessageBuilder generateBuilder(String message) {
            return IssueMsgBuilder.of(message, gitlabService);
        }

    }

    /**
     * merge request factory
     *
     * @author renxb
     */
    public static class MergeRequestFactory implements IBuilderFactory {
        GitlabService gitlabService;

        MergeRequestFactory(GitlabService gitlabService) {
            this.gitlabService = gitlabService;
        }

        @Override
        public String name() {
            return "Merge Request Hook";
        }

        @Override
        public MessageBuilder generateBuilder(String message) {
            return MergeRequestMsgBuilder.of(message, gitlabService);
        }

    }

    /**
     * pipeline factory
     *
     * @author renxb
     */
    public static class PipelineFactory implements IBuilderFactory {

        @Override
        public String name() {
            return "Pipeline Hook";
        }

        @Override
        public MessageBuilder generateBuilder(String message) {
            return PipelineMsgBuilder.of(message);
        }

    }

    /**
     * note factory
     *
     * @author renxb
     */
    public static class NoteFactory implements IBuilderFactory {
        GitlabService gitlabService;

        NoteFactory(GitlabService gitlabService) {
            this.gitlabService = gitlabService;
        }

        @Override
        public String name() {
            return "Note Hook";
        }

        @Override
        public MessageBuilder generateBuilder(String message) {
            return NoteMsgBuilder.of(message, gitlabService);
        }

    }

    /**
     * push factory
     *
     * @author renxb
     */
    public static class PushFactory implements IBuilderFactory {

        @Override
        public String name() {
            return "Push Hook";
        }

        @Override
        public MessageBuilder generateBuilder(String message) {
            return PushMsgBuilder.of(message);
        }

    }

}
