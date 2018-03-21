package xiaobo9.message.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import xiaobo9.message.gitlab.bean.GitlabUser;
import xiaobo9.message.gitlab.bean.MergeRequest;
import xiaobo9.message.gitlab.bean.MergeRequestEvent;
import xiaobo9.message.gitlab.bean.Project;
import xiaobo9.message.gitlab.webhook.Gitlab;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * gitlab 相关操作的服务类
 *
 * @author renxb
 */
@Slf4j
@Service
public class GitlabService implements InitializingBean {
    @Setter
    private GitlabInfo gitlabInfo;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * gitlab 服务信息
     *
     * @param gitlabInfo gitlab 服务信息
     */
    @Autowired
    public GitlabService(GitlabInfo gitlabInfo) {
        this.gitlabInfo = gitlabInfo;
    }

    /**
     * 获取用户信息
     *
     * @param gitlabUrl gitlab服务的地址
     * @param userId    用户的id
     * @return gitlab用户信息
     */
    Optional<GitlabUser> getUser(String gitlabUrl, String userId) {
        String url = gitlabUrl + "/api/v4/users/" + userId;

        Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            String result = response.body().string();
            Gson gson = new Gson();
            return Optional.of(gson.fromJson(result, GitlabUser.class));
        } catch (Exception e) {
            log.warn("{}", url, e);
        }
        return Optional.empty();
    }

    /**
     * 获取用户信息
     *
     * @param project gitlab服务信息
     * @param userId  用户的id
     * @return gitlab用户信息
     */
    public Optional<GitlabUser> getUser(Project project, String userId) {
        return this.getUser(project.getGitlabUrl(), userId);
    }

    /**
     * 获取用户信息
     *
     * @param event gitlab服务信息
     * @return gitlab用户信息
     */
    public List<GitlabUser> participants(MergeRequestEvent event) {
        try {
            return doParticipants(event);
        } catch (Exception e) {
            log.info("", e);
            return Collections.emptyList();
        }
    }

    private List<GitlabUser> doParticipants(MergeRequestEvent event) {
        Project project = event.getProject();
        String gitlabUrl = project.getGitlabUrl();
        MergeRequest attributes = event.getObjectAttributes();
        String url = gitlabUrl + "/api/v4/projects/" + project.getId() + "/merge_requests/" + attributes.getIid() + "/participants";
        GitlabToken token = this.getGitlabInfoByUrl(gitlabUrl);
        Request request = new Request.Builder().url(url).header(Gitlab.TOKEN_NAME, token.getToken()).get().build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            String result = response.body().string();
            if (response.isSuccessful()) {
                return objectMapper.readValue(result, new TypeReference<List<GitlabUser>>() {
                });
            }
            log.info("participants {} {}", url, result);
        } catch (Exception e) {
            log.warn("participants {}", url, e);
        }
        return Collections.emptyList();
    }

    @Override
    public void afterPropertiesSet() {
        log.info("{}", gitlabInfo.getUrl());
        Holder.initGitlabInfo(gitlabInfo);
    }

    /**
     * 获取 gitlab 信息
     *
     * @param id id
     * @return 结果
     */
    public GitlabToken getGitlabInfo(String id) {
        return Holder.getGitlabInfo(id);
    }

    /**
     * 获取 gitlab 信息
     *
     * @param url gitlab url
     * @return 结果
     */
    GitlabToken getGitlabInfoByUrl(String url) {
        return Holder.getGitlabInfoByUrl(url);
    }

    /**
     * 数据持有者
     *
     * @author renxb
     */
    private static class Holder {
        /**
         * 对应gitlab服务的认证token，有些gitlab的接口需要验证的情况下，请求的时候带上认证用的token信息
         */
        private static Map<String, GitlabToken> map;
        private static Map<String, GitlabToken> urlMap;

        static GitlabToken getGitlabInfo(String id) {
            return map.get(id);
        }

        static GitlabToken getGitlabInfoByUrl(String gitlabUrl) {
            return urlMap.get(gitlabUrl);
        }

        private static void initGitlabInfo(GitlabInfo gitlabInfo) {
            map = Maps.newHashMap();
            urlMap = Maps.newHashMap();
            String[] url = gitlabInfo.getUrl().split(";");
            for (String s : url) {
                String[] split = s.split("=");
                GitlabToken gitlabToken = new GitlabToken();
                gitlabToken.setId(split[0]);
                gitlabToken.setUrl(split[1]);
                map.put(gitlabToken.getId(), gitlabToken);
                urlMap.put(gitlabToken.getUrl(), gitlabToken);

            }
            String[] privateToken = gitlabInfo.getPrivateToken().split(";");

            for (String s : privateToken) {
                String[] split = s.split("=");
                GitlabToken gitlabToken = map.get(split[0]);
                if (gitlabToken != null) {
                    gitlabToken.setToken(split[1]);
                }
            }
        }
    }

    /**
     * gitlab token
     *
     * @author renxb
     */
    @Getter
    @Setter
    @ToString
    public static class GitlabToken {
        private String id;
        private String url;
        private String token;
    }
}

