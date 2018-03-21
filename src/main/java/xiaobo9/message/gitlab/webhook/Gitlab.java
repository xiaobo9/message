package xiaobo9.message.gitlab.webhook;

import lombok.Getter;
import lombok.Setter;

/**
 * gitlab 信息
 *
 * @author renxb
 */
@Getter
@Setter
public class Gitlab {
    private String name;
    private String url;

    private String token;

    private boolean doModify = true;

    public static final String TOKEN_NAME = "PRIVATE-TOKEN";

    static final int PER_PAGE = 100;

    static final int MAX_INDEX = 100;


    String projects(int page) {
        return String.format("%s/api/v4/projects?simple=true&per_page=100&page=%s", url, page);
    }

    String project(String id) {
        return String.format("%s/api/v4/projects/%s", url, id);
    }

    String hooks(int projectId) {
        return String.format("%s/api/v4/projects/%s/hooks", url, projectId);
    }

    String hook(int projectId, int hookId) {
        return String.format("%s/api/v4/projects/%s/hooks/%s", url, projectId, hookId);
    }
}
