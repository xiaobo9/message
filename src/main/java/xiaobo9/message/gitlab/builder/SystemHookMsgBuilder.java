package xiaobo9.message.gitlab.builder;

import xiaobo9.message.gitlab.bean.Message;
import xiaobo9.message.gitlab.bean.SystemHookEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * add user message builder
 *
 * @author renxb
 */
@Slf4j
@Getter
@Setter
public class SystemHookMsgBuilder extends MessageBuilder {

    private SystemHookEvent event;

    private String token;

    /**
     * system hook
     *
     * @param token token
     * @param event system hook event
     */
    public SystemHookMsgBuilder(String token, SystemHookEvent event) {
        this.token = token;
        this.event = event;
    }

    @Override
    public Optional<Message> buildMessage() {
        Message msg = new Message();
        msg.setTitle("获得访问项目的权限");
        String message = String.format("获得了项目 【%s】 的访问权限 ", event.getProjectPathWithNamespace());
        msg.setText(message);
        if ("open".equals(token)) {
            msg.setUrl("http://open.company.com/" + event.getProjectPathWithNamespace());
        } else {
            msg.setUrl("http://gitlab.company.com/" + event.getProjectPathWithNamespace());
        }
        msg.addReceiver(event.getUserName(), event.getUserName());
        return Optional.of(msg);
    }

}
