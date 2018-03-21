package xiaobo9.message.message;

import xiaobo9.message.gitlab.bean.Message;
import org.springframework.stereotype.Service;

@Service
public class DefaultMessageSender implements IMessageSender {
    @Override
    public boolean sendMessage(Message msg) {
        return false;
    }
}
