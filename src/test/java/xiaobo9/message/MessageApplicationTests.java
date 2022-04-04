package xiaobo9.message;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import xiaobo9.message.sonar.SonarConfig;
import xiaobo9.message.sonar.bean.SonarDBProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://spring.io/guides/gs/testing-web/
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class MessageApplicationTests {
    @Autowired
    private SonarConfig sonarConfig;

    @Autowired
    private SonarDBProperties sonarDBProperties;

    /**
     * contextLoads() is a test to verify if the application is able to load Spring context successfully or not.
     */
    @Test
    public void contextLoads() {
        log.info("{}", sonarDBProperties);
        assertThat(sonarConfig).isNotNull();
        Assertions.assertFalse(sonarConfig.isSonarCheck(), "默认关闭 sonarcheck ");
    }

}
