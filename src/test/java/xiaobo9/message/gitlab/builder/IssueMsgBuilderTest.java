package xiaobo9.message.gitlab.builder;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import xiaobo9.message.gitlab.GitlabService;
import xiaobo9.message.gitlab.bean.GitlabUser;
import xiaobo9.message.gitlab.bean.Message;
import xiaobo9.message.gitlab.bean.Project;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("IssueMsgBuilder 问题消息")
public class IssueMsgBuilderTest {
    @Mock
    private GitlabService service;

    @Test
    public void buildMessage() throws IOException {
        GitlabUser user = new GitlabUser();
        user.setName("测试用户");
        user.setUsername("issueAuthor");
        File file = new File(IssueMsgBuilderTest.class.getResource("/issues/issue.json").getFile());
        String string = FileUtils.readFileToString(file, "UTF-8");
        Mockito.when(service.getUser(Mockito.any(Project.class), anyString())).thenReturn(Optional.of(user));
        IssueMsgBuilder builder = IssueMsgBuilder.of(string, service);
        Optional<Message> optionalMessage = builder.buildMessage();

        Assert.assertTrue(optionalMessage.isPresent());

        optionalMessage.ifPresent(message -> {
            log.info("{}", message);
            Map<String, String> receivers = message.getReceivers();
            log.info("{}", receivers);
            assertEquals("", 1, receivers.size());
        });
    }
}