package xiaobo9.message.sonar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import xiaobo9.message.utils.UUIDUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * sonar qube 服务相关
 *
 * @author renxb
 */
@Slf4j
@Service
public class SonarQubeService {

    @Value("${gitlab-message.url}")
    private String serverUrl;

    private static final Map<String, String> severity = Maps.newLinkedHashMap();

    static {
        severity.put("BLOCKER", "阻断");
        severity.put("CRITICAL", "严重");
        severity.put("MAJOR", "主要");
        severity.put("MINOR", "次要");
        severity.put("INFO", "提示");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserSonarResultRepository userSonarResultRepository;

    private final IMessageSender messageService;

    private final SonarConfig config;

    private final SonarJdbcService sonarJdbcService;

    /**
     * 构造
     *
     * @param msg         messageService
     * @param config      config
     * @param jdbcService sonar jdbc service
     */
    @Autowired
    public SonarQubeService(
            IMessageSender msg, SonarConfig config, SonarJdbcService jdbcService, UserSonarResultRepository repository) {
        this.messageService = msg;
        this.config = config;
        this.sonarJdbcService = jdbcService;
        this.userSonarResultRepository = repository;
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

    private List<UserSonarResult> saveCheckResult(List<Tip> tips) {
        Date date = new Date();
        String checkId = UUIDUtils.uuid();
        List<UserSonarResult> list = Lists.newArrayList();
        for (Tip tip : tips) {

            UserSonarResult result = new UserSonarResult();
            list.add(result);

            result.setTip(tip);
            result.setId(UUIDUtils.uuid());
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

    private void buildAndSend(List<UserSonarResult> userSonarResults) {
        //根据用户名分组 一个用户名 多个项目的检查结果
        Map<String, List<UserSonarResult>> listMap = userSonarResults.stream().collect(Collectors.groupingBy(UserSonarResult::getEmail));

        Checker checker = new Checker();
        for (Map.Entry<String, List<UserSonarResult>> entry : listMap.entrySet()) {
            String author = entry.getKey();
            String authorWithOutEmail = StringUtils.substringBefore(author, "@");
            if (checker.contains(authorWithOutEmail)) {
                continue;
            }
            if (checker.check(authorWithOutEmail)) {
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
                checker.failure.add(authorWithOutEmail);
                log.info("{} {} {} {}", message.getReceivers(), message.getTitle(), message.getText(), message.getUrl());
            }

        }
        log.warn("发送失败 {}", checker.failure);
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


    private Message message(String author, String url, String message) {
        Message msg = new Message();
        msg.setSenderName("admin");
        msg.setTitle("SonarQube 检查出来的问题");

        msg.setText(message);
        msg.addReceiver(author, author);
        msg.setUrl(url);
        return msg;
    }

    @Getter
    @Setter
    static class Checker {
        private Set<String> failure;

        boolean contains(String userName) {
            return this.failure.contains(userName);
        }

        boolean check(String userName) {
            if (NumberUtils.isDigits(userName)) {
                failure.add(userName);
                return false;
            }
            return true;
        }
    }
}
