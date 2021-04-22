package xiaobo9.message.sonar;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

class SonarQubeServiceTest {


    @Test
    void multiMessage() {
        SonarQubeService sonarQubeService = new SonarQubeService(null, null, null, null);
        HashMap<String, Integer> issuesCount = Maps.newHashMap();
        issuesCount.put("BLOCKER", 1);
        issuesCount.put("CRITICAL", 1);
        issuesCount.put("abc", 1);
        List<Tip> list = Lists.newArrayList(
                new Tip("abc@company.com").setProjectName("测试1").setIssuesCount(issuesCount),
                new Tip("abc@company.com").setProjectName("测试2").setIssuesCount(issuesCount)
        );

        StringBuilder builder = new StringBuilder();

        sonarQubeService.multiMessage(list, builder);

        System.out.println(builder);

    }
}