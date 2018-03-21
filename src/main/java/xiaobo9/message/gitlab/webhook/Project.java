package xiaobo9.message.gitlab.webhook;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 项目信息
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private int id;
    private String name;
    @JsonProperty("name_with_namespace")
    @SerializedName("name_with_namespace")
    private String nameWithNameSpace;
    @JsonProperty("web_url")
    @SerializedName("web_url")
    private String webUrl;

    Project() {
        hooks = Lists.newArrayList();
    }

    private List<WebHook> hooks;

    void addHook(WebHook hook) {
        hooks.add(hook);
    }
}
