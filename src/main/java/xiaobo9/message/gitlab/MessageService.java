package xiaobo9.message.gitlab;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xiaobo9.message.gitlab.bean.Message;
import xiaobo9.message.gitlab.bean.SystemHookEvent;
import xiaobo9.message.gitlab.builder.BuilderFactory;
import xiaobo9.message.gitlab.builder.MessageBuilder;
import xiaobo9.message.gitlab.builder.SystemHookMsgBuilder;
import xiaobo9.message.gitlab.webhook.WebHookService;
import xiaobo9.message.message.IMessageSender;

import java.io.IOException;

/**
 * 直接怼 MessageController 的类
 *
 * @author renxb
 */
@Slf4j
@Service
public class MessageService {

    @Value("${salt:message}")
    private String SALT = "message";

    private final IMessageSender messageSender;

    private final BuilderFactory builderFactory;

    private final WebHookService webHookService;

    /**
     * message service
     *
     * @param messageSender  message sender
     * @param builderFactory builder factory
     */
    @Autowired
    public MessageService(IMessageSender messageSender, BuilderFactory builderFactory, WebHookService webHookService) {
        this.messageSender = messageSender;
        this.builderFactory = builderFactory;
        this.webHookService = webHookService;
    }

    /**
     * gitlab信息
     *
     * @param token         token
     * @param eventType     事件类型
     * @param messageString 消息内容
     */
    void dealEvent(String token, String eventType, String messageString) throws IOException {
        if ("System Hook".equals(eventType)) {
            dealSystemHook(token, messageString);
            return;
        }

        builderFactory.getBuilder(eventType, messageString)
                .flatMap(MessageBuilder::buildMessage)
                .ifPresent(this::dealMessage);
    }

    private static Gson gson = new Gson();

    private void dealSystemHook(String token, String messageString) throws IOException {
        SystemHookEvent event = gson.fromJson(messageString, SystemHookEvent.class);
        if (StringUtils.isNotBlank(event.getObjectKind())) {
            return;
        }
        if ("user_add_to_team".equals(event.getEventName())) {
            new SystemHookMsgBuilder(token, event).buildMessage().ifPresent(this::dealMessage);
        } else if ("project_create".equals(event.getEventName())) {
            log.info("{} 创建了新项目，增加 message web hook", event);
            webHookService.addHook2Project(token, event.getProjectId());
        }
    }

    private void dealMessage(Message message) {
        boolean result = messageSender.sendMessage(message);
        if (!result) {
            log.info("failed to send message, receivers: {}", message.getReceivers());
        }
    }

    /**
     * 生成md5
     *
     * @param url url
     * @return md5
     */
    public String register(String url) {
        return DigestUtils.md5Hex(url + SALT);
    }

    /**
     * 具体的 git 名字
     *
     * @param gitName Git name
     */
    void addWebHooks(String gitName) {
        webHookService.allAddHook(gitName);
    }
}
