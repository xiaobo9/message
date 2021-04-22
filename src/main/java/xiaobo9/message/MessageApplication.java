package xiaobo9.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import xiaobo9.message.gitlab.GitlabInfo;
import xiaobo9.message.sonar.SonarConfig;
import xiaobo9.message.sonar.bean.SonarDBProperties;

/**
 * MessageApplication
 *
 * @author renxb
 * @version 4.0
 */
@EnableScheduling
@EnableSwagger2
@EnableConfigurationProperties({GitlabInfo.class, SonarConfig.class, SonarDBProperties.class})
@ComponentScan(value = {"xiaobo9.message"})
@ServletComponentScan(basePackages = {"xiaobo9.message.filter"})
@EnableAutoConfiguration
@SpringBootConfiguration
public class MessageApplication {

    /**
     * main
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MessageApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        springApplication.run(args);
    }
}
