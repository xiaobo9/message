package xiaobo9.message.sonar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * sonar web 钩子
 *
 * @author renxb
 * @version 4.0
 */
@Slf4j
@RestController
@RequestMapping("sonar")
public class SonarWebHookController {

    /**
     * sonar web hook
     *
     * @param message message
     * @return 结果
     */
    @PostMapping("webhook")
    public ResponseEntity<String> sendToUser(String message) {
        log.info("message: {}", message);
        return ResponseEntity.ok("没有启用发送测试消息的功能");
    }

}
