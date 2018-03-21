package xiaobo9.message.sonar;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 是否启用某项功能
 *
 * @author renxb
 */
@Getter
@Setter
@ConfigurationProperties("gitlab-message.config-enable")
public class SonarConfig {
    /**
     * 提取 sonar 问题
     */
    private boolean sonarCheck;

}
