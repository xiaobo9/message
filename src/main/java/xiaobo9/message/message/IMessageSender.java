package xiaobo9.message.message;

import xiaobo9.message.gitlab.bean.Message;

public interface IMessageSender {
    /**
     * 发送消息啦
     *
     * @param msg 消息
     * @return 发送结果啦
     */
    boolean sendMessage(Message msg);

}
