package xiaobo9.message.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import xiaobo9.message.utils.RequestUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息接口
 *
 * @author renxb
 * @version 4.0
 */
@Slf4j
@RestController
@RequestMapping("message")
public class MessageController {

    private Logger gitlabMessageLogger = LoggerFactory.getLogger("gitlabMessageLogger");

    @Autowired
    private MessageService service;

    /**
     * 处理事件
     *
     * @param eventType    消息类型
     * @param eventContent 消息体
     * @param token        token
     * @return 结果
     */
    @RequestMapping("gitlab")
    public ResponseEntity<Object> gitlab(
            @RequestHeader("X-Gitlab-Event") String eventType,
            @RequestBody String eventContent,
            @RequestParam(name = "token", required = false) String token) {
        log.info(eventType);

        gitlabMessageLogger.info("event type: {}, event content: {}", eventType, eventContent);
        try {
            service.dealEvent(token, eventType, eventContent);
        } catch (Exception e) {
            log.info("eventContent: {}", eventContent, e);
        }
        return ResponseEntity.ok("I hold the message which you send! thank you. have a nice day.");
    }

    /**
     * 注册 gitlab 工程
     *
     * @param url 要注册的 gitlab 工程地址
     * @return 注册结果
     */
    @RequestMapping("register")
    public ResponseEntity<Object> register(@RequestParam("url") String url) {
        if (StringUtils.isBlank(url)) {
            Map<String, String> result = new HashMap<>();
            result.put("url", "url 错误");
            result.put("token", "url 错误");
            return ResponseEntity.badRequest().body(result);
        }
        String id = service.register(url);
        log.info("id: {}, url:{}", id, url);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("token", id);
        return ResponseEntity.ok(result);
    }

    /**
     * 重定向到 gitlab
     *
     * @param url      目的url
     * @param response 响应
     * @throws IOException 异常
     */
    @GetMapping("redirect")
    public void redirect(@RequestParam("url") String url, HttpServletResponse response) throws IOException {
        String location = new String(Base64.decodeBase64(url), StandardCharsets.UTF_8);
        response.sendRedirect(location);
    }

    /**
     * 批量添加 hook
     *
     * @param gitName name
     * @return result
     */
    @GetMapping("addWebHook")
    public ResponseEntity<String> addWebHook(@RequestParam String gitName) {
        service.addWebHooks(gitName);
        return ResponseEntity.ok("ok");
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @PostMapping("postUser")
    public ResponseEntity<String> postUser(HttpServletRequest request, @RequestBody String user) throws IOException {
        List<User> value = objectMapper.readValue(user, new TypeReference<List<User>>() {
        });
        log.info("{} {} {}", RequestUtils.getRemoteIpAddr(request), user, value);
        return ResponseEntity.ok("ok");
    }

    @Getter
    @Setter
    @ToString
    public static class User {
        private String id;
    }
}
