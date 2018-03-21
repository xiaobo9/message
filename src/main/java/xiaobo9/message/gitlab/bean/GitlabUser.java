package xiaobo9.message.gitlab.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 用户信息
 *
 * @author renxb
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GitlabUser {
    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名称 咱们都是汉字 显示用的
     */
    private String name;

    /**
     * 用户标识 登陆用的标识，区分用户的
     */
    private String username;

    @JsonProperty("avatar_url")
    private String avatarUrl;
}
