package xiaobo9.message.sonar.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties("sonar.db")
public class SonarDBProperties {

    private String url;

    private String user;

    private String password;

    private String driver;

}
