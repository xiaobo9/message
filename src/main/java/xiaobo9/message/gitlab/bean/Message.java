package xiaobo9.message.gitlab.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public class Message {
    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息发送者名称
     */
    private String senderName;

    /**
     * 接受者列表<cc登录名,业务系统登录名或别称>
     */
    private Map<String, String> receivers = new HashMap<>();

    /**
     * 操作地址
     */
    private String url;

    /**
     * 消息内容
     */
    private String text;

    /**
     * 添加接收人
     *
     * @param ccName  登录名
     * @param misName 该用户在业务系统中的名称(不需要记录则填写ccName即可)
     */
    public void addReceiver(String ccName, String misName) {
        receivers.put(ccName, misName);
    }
}
