package xiaobo9.message.sonar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xiaobo9.message.gitlab.bean.Message;
import xiaobo9.message.message.IMessageSender;
import xiaobo9.message.sonar.entity.UserSonarResult;
import xiaobo9.message.sonar.repository.UserSonarResultRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * sonar qube 服务相关
 *
 * @author renxb
 */
@Slf4j
@Service
public class SonarQubeService {
    @Setter
    @Getter
    private String sonarUrl = "http://sonar.company.com";

    @Value("${gitlab-message.url}")
    private String serverUrl;

    private IMessageSender messageService;

    private SonarConfig config;

    private SonarJdbcService sonarJdbcService;

    @Autowired
    private UserSonarResultRepository userSonarResultRepository;

    private static Map<String, String> severity = Maps.newLinkedHashMap();

    static {
        severity.put("BLOCKER", "阻断");
        severity.put("CRITICAL", "严重");
        severity.put("MAJOR", "主要");
        severity.put("MINOR", "次要");
        severity.put("INFO", "提示");
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 构造
     *
     * @param messageService   messageService
     * @param config           config
     * @param sonarJdbcService sonar jdbc service
     */
    @Autowired
    public SonarQubeService(IMessageSender messageService, SonarConfig config, SonarJdbcService sonarJdbcService) {
        this.messageService = messageService;
        this.config = config;
        this.sonarJdbcService = sonarJdbcService;
    }

    /**
     * 定时任务
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void scheduledCheck() {
        if (!config.isSonarCheck()) {
            return;
        }
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        if (DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek) {
            log.info("周末就不执行了。");
            return;
        }
        log.info("开始检查 sonar 问题");
        List<Tip> tips = sonarJdbcService.getTips();

        List<UserSonarResult> userSonarResults = saveCheckResult(tips);

        buildAndSend(userSonarResults);

    }

    private void buildAndSend(List<UserSonarResult> userSonarResults) {
        Map<String, List<UserSonarResult>> listMap = userSonarResults.stream().collect(Collectors.groupingBy(UserSonarResult::getEmail));

        Set<String> failure = Sets.newHashSet();
        for (Map.Entry<String, List<UserSonarResult>> entry : listMap.entrySet()) {
            String author = entry.getKey();
            String authorWithOutEmail = StringUtils.substringBefore(author, "@");
            if (failure.contains(authorWithOutEmail)) {
                continue;
            }
            if (NumberUtils.isDigits(authorWithOutEmail)) {
                failure.add(authorWithOutEmail);
                log.info("sonar 问题作者信息不对 [{}] {}", authorWithOutEmail, entry.getValue());
                continue;
            }

            String url = serverUrl + "/sonar.html?author=" + author + "&checkId=" + entry.getValue().get(0).getCheckId();
            String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
            StringBuilder builder = new StringBuilder().append(date);
            List<Tip> list = entry.getValue().stream().map(UserSonarResult::getTip).collect(Collectors.toList());
            multiMessage(list, builder);
            builder.append("点击查看详细信息");

            Message message = message(authorWithOutEmail, url, builder.toString());
            log.info("{} 的 sonar 问题", authorWithOutEmail);
            boolean sendResult = messageService.sendMessage(message);
            if (!sendResult) {
                failure.add(authorWithOutEmail);
                log.info("{} {} {} {}", message.getReceivers(), message.getTitle(), message.getText(), message.getUrl());
            }

        }
        log.warn("发送失败 {}", failure);
    }

    void multiMessage(List<Tip> tips, StringBuilder builder) {
        for (Tip tip : tips) {
            builder.append("\n项目【").append(tip.getProjectName()).append("】中的问题");
            Set<Map.Entry<String, String>> entries = severity.entrySet();
            for (Map.Entry<String, String> subEntry : entries) {
                Integer count = tip.getIssuesCount().get(subEntry.getKey());
                if (count != null) {
                    builder.append(", ").append(subEntry.getValue()).append(": ").append(count).append("个");
                }
            }
        }
        builder.append("\n");
    }

    private String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private List<UserSonarResult> saveCheckResult(List<Tip> tips) {
        Date date = new Date();
        String checkId = uuid();
        List<UserSonarResult> list = Lists.newArrayList();
        for (Tip tip : tips) {

            UserSonarResult result = new UserSonarResult();
            list.add(result);

            result.setTip(tip);
            result.setId(uuid());
            result.setCheckId(checkId);
            result.setEmail(tip.getEmail());
            result.setProjectId(tip.getProjectId());
            result.setProjectName(tip.getProjectName());
            result.setCheckDate(date);

            try {
                result.setResult(objectMapper.writeValueAsString(tip.getIssuesCount()));
            } catch (Exception e) {
                log.info("", e);
            }

        }

        List<List<UserSonarResult>> partition = Lists.partition(list, 100);
        for (List<UserSonarResult> userSonarResults : partition) {
            userSonarResultRepository.saveAll(userSonarResults);
        }
        return list;
    }

    private Message message(String author, String url, String message) {
        Message msg = new Message();
        msg.setSenderName("admin");
        msg.setTitle("SonarQube 检查出来的问题");

        msg.setText(message);
        msg.addReceiver(author, author);
        msg.setUrl(url);
        return msg;
    }

}
