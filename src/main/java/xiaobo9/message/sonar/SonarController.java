package xiaobo9.message.sonar;

import xiaobo9.message.sonar.entity.UserSonarResult;
import xiaobo9.message.sonar.bean.SonarWebHookMessage;
import xiaobo9.message.sonar.repository.UserSonarResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 提供给页面查询展现sonar结果的接口
 *
 * @author renxb
 */
@Slf4j
@RestController
@RequestMapping("sonar")
public class SonarController {

    @Autowired
    private UserSonarResultRepository userSonarResultRepository;

    @Autowired
    private SonarQubeService sonarQubeService;

    private String sonarUrl = "http://sonar.company.com";

    /**
     * 手动通知检查结果
     *
     * @return success
     */
    @GetMapping("checkResult")
    public ResponseEntity<List<UserSonarResult>> checkResult(@RequestParam String author, @RequestParam String checkId) {
        List<UserSonarResult> list = userSonarResultRepository.findByEmailAndCheckId(author, checkId);
        return ResponseEntity.ok(list);
    }

    /**
     * 打开 sonar
     */
    @GetMapping("redirect")
    public void redirect(HttpServletResponse response, @RequestParam String email, @RequestParam String projectId) throws IOException {
        String encodeAuthor = URLEncoder.encode(email, StandardCharsets.UTF_8.displayName());
        String url = sonarUrl + "/project/issues?resolved=false&id=" + projectId + "&authors=" + encodeAuthor;
        response.sendRedirect(url);
    }

    /**
     * sonar web hook
     *
     * @param project project
     * @param message message
     * @return result
     */
    @PostMapping("webHook")
    @ResponseBody
    public ResponseEntity<String> webHook(
            @RequestHeader(name = "X-SonarQube-Project") String project,
            @RequestBody SonarWebHookMessage message
    ) {
        log.info("{} {}", project, message);
        return ResponseEntity.ok("ok");
    }
}
