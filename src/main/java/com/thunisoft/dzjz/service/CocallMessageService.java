package com.thunisoft.dzjz.service;

import com.google.gson.Gson;
import com.thunisoft.cocall.open.client.CoCallMisClient;
import com.thunisoft.cocall.open.commons.OpenMisMessage;
import com.thunisoft.dzjz.bean.CoCallInfo;
import com.thunisoft.dzjz.bean.MergeRequestEvent;
import com.thunisoft.dzjz.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * 发送消息
 * @author renxb
 */
@Slf4j
@ConfigurationProperties(prefix = "cocall")
@Service
public class CocallMessageService implements InitializingBean {

    @Autowired
    private CoCallInfo coCallInfo;

    private CoCallMisClient client;

    /**
     * 发送测试信息
     * @param user cocall用户
     */
    public void testMessage(String user) {
        OpenMisMessage msg = new OpenMisMessage();
        msg.setSendtime(System.currentTimeMillis());
        msg.setSenderName("admin");
        msg.setTitle("测试消息");
        msg.setText("测试消息第一行\n测试消息第二行\n");
        msg.setUrl("http://gitlab.thunisoft.com/Team_dzjz/FY_PRD_DZJZ_PRO");
        msg.addParam("test", "test");
        msg.setType(OpenMisMessage.TYPE_OPER);
        msg.addReceiver(user, user);
        client.misDealMethod().sendMisMessage(msg);
    }

    /**
     * gitlab信息
     * @param eventString 事件类型
     * @param messageString 消息内容
     */
    public void dealEvent(String eventString, String messageString) {

        if (eventString.equals("Merge Request Hook")) {
            Gson gson = new Gson();
            MergeRequestEvent event = gson.fromJson(messageString, MergeRequestEvent.class);
            MergeRequestEvent.Assignee assignee = event.getAssignee();
            if (assignee != null && "opened".equals(event.getObjectAttributes().getState())) {
                OpenMisMessage msg = buildMessage(event);
                client.misDealMethod().sendMisMessage(msg);
            }
        }
    }

    private OpenMisMessage buildMessage(MergeRequestEvent event) {
        User operationUser = event.getUser();
        MergeRequestEvent.ObjectAttributes attributes = event.getObjectAttributes();
        MergeRequestEvent.Assignee assignee = event.getAssignee();
        StringBuilder builder = new StringBuilder(operationUser.getName()).append("在项目 ").append(event.getProject().getName())
                .append(" 开启了合并请求\n\n");
        builder.append("如果你不需要收到这条消息 或者 消息发送有误，请联系 任晓波\n");
        OpenMisMessage msg = new OpenMisMessage();
        msg.setSendtime(System.currentTimeMillis());
        msg.setSenderName("admin");
        msg.setTitle(operationUser.getName() + " 指定给你的合并请求");
        msg.setText(builder.toString());
        msg.setUrl("http://gitlab.thunisoft.com/Team_dzjz/FY_PRD_DZJZ_PRO/merge_requests/" + attributes.getIid());
        msg.setType(OpenMisMessage.TYPE_OPER);
        msg.addReceiver(assignee.getUsername(), assignee.getUsername());
        return msg;
    }

    @Override
    public void afterPropertiesSet() {
        client = new CoCallMisClient(coCallInfo.getClientId(), coCallInfo.getClientSecret(), coCallInfo.getCcServerIp(),
                coCallInfo.getCcServerPort());
    }

}
