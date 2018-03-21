package xiaobo9.message.gitlab.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

/**
 * 项目信息
 *
 * @author renxb
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Project {
    private String id;
    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目地址
     */
    @SerializedName("web_url")
    private String webUrl;

    /**
     * 项目路径含命名空间
     */
    @SerializedName("path_with_namespace")
    private String pathWithNamespace;

    @SerializedName("")
    private Commit commit;

    /**
     * 项目所在的gitlab服务器的地址
     */
    private String gitlabUrl;

    public String getGitlabUrl() {
        if (StringUtils.isBlank(gitlabUrl)) {
            this.gitlabUrl = StringUtils.substringBefore(webUrl, pathWithNamespace);
        }
        return this.gitlabUrl;
    }
}
