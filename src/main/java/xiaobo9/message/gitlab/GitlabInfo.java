package xiaobo9.message.gitlab;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * gitlab 信息
 *
 * @author renxb
 */
@Getter
@Setter
@ConfigurationProperties("gitlab")
@ToString
public class GitlabInfo {
    private String url;

    private String privateToken;
}
