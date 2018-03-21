package xiaobo9.message.gitlab.builder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

@Slf4j
public class MessageBuilderTest {

    @Test
    public void getUserFromAt() {
        String message = "@abc @abc\\-1 巴拉巴拉";
        log.info(message);
        Set<String> user = new MessageBuilder().getUserFromAt(message);
        log.info("{}", user);
        Assert.assertEquals("", 2, user.size());
        Assert.assertTrue("", user.contains("abc") && user.contains("abc-1"));
    }
}