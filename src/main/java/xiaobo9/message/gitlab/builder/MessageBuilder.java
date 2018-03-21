package xiaobo9.message.gitlab.builder;

import xiaobo9.message.gitlab.bean.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息构建类
 *
 * @author renxb
 */
@Slf4j
@Getter
@Setter
public class MessageBuilder {

    /**
     * 构建消息
     *
     * @return 消息
     */
    public Optional<Message> buildMessage() {
        return Optional.empty();
    }

    /**
     * 提取被 @ 的人
     */
    private Pattern pattern = Pattern.compile("((?<=@)[A-Za-z\\-\\d\\\\]+)+");

    /**
     * 提取被 @ 的人 从消息中获取用户信息
     *
     * @param message 消息
     * @return 用户们
     */
    Set<String> getUserFromAt(String message) {
        Set<String> set = new HashSet<>();
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            int count = matcher.groupCount();
            for (int i = 0; i < count; i++) {
                set.add(matcher.group(i).replace("\\", ""));
            }
        }
        return set;
    }

}
