package xiaobo9.message.sonar;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 提醒
 *
 * @author renxb
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Tip {

    private String id;

    private String email;

    private int count;

    private Map<String, Integer> issuesCount;

    private String projectId;

    private String projectName;

    private String message;

    private String url;

    /**
     * 构造
     *
     * @param email 作者
     */
    Tip(String email) {
        this.email = email;
    }

    /**
     * add issues number
     *
     * @param key   issues key
     * @param count issues count
     */
    void add(String key, Integer count) {
        if (issuesCount == null) {
            issuesCount = Maps.newHashMap();
        }
        issuesCount.put(key, count);
    }

    /**
     * sonar project
     *
     * @author renxb
     */
    @Getter
    @Setter
    public static class SonarProject {
        private String key;
        private String name;
    }
}