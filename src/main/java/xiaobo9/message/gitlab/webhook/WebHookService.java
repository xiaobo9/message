package xiaobo9.message.gitlab.webhook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xiaobo9.message.gitlab.GitlabService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * web hook service
 *
 * @author renxb
 */
@Slf4j
@Service
public class WebHookService {
    @Value("${gitlab-message.url}")
    private String serverUrl;

    private static final OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private final GitlabService gitlabService;

    public WebHookService(GitlabService gitlabService) {
        this.gitlabService = gitlabService;
    }

    /**
     * 给项目添加 web hook
     *
     * @param token     token
     * @param projectId project id
     * @throws IOException exception
     */
    public void addHook2Project(String token, String projectId) throws IOException {
        log.info("{} {} 新建的项目", token, projectId);
        if (StringUtils.isBlank(token) || "null".equalsIgnoreCase(token)) {
            token = "gitlab";
        }
        Gitlab gitlab = new Gitlab();
        GitlabService.GitlabToken info = gitlabService.getGitlabInfo(token);
        gitlab.setUrl(info.getUrl());
        gitlab.setToken(info.getToken());
        gitlab.setDoModify(true);

        Project project = this.getProject(gitlab, projectId);

        this.addHook(gitlab, project);
    }

    public void allAddHook(String gitName) {

        GitlabService.GitlabToken info = gitlabService.getGitlabInfo(gitName);
        if (info == null) {
            log.info("{} 不添加", gitName);
            return;
        }

        Gitlab gitlab = new Gitlab();
        gitlab.setUrl(info.getUrl());
        gitlab.setToken(info.getToken());
        gitlab.setDoModify(true);

        List<Project> projects = projects(gitlab);

        log.info("projects size: {}", projects.size());
        for (Project project : projects) {
            log.info("{} {} 如果没有就加一下 message web hook", project.getId(), project.getWebUrl());
            fetchHook(gitlab, project);
            ifNeedAddMessageHook(gitlab, project);
        }
    }

    private void ifNeedAddMessageHook(Gitlab gitlab, Project project) {
        Boolean hasNoHook = Optional.of(project.getHooks())
                .map(hooks -> {
                    for (WebHook hook : hooks) {
                        if (hook.getUrl().startsWith("http://gitmsg")) {
                            return false;
                        }
                    }
                    return true;
                }).orElse(true);

        if (hasNoHook) {
            addHook(gitlab, project);
        } else {
            log.info("{} {} has hook", project.getId(), project.getWebUrl());
        }
    }

    public List<Project> projects(Gitlab gitlab) {
        List<Project> result = Lists.newArrayList();
        for (int index = 1; index < Gitlab.MAX_INDEX; index++) {
            List<Project> projects = getProjects(gitlab, index);
            if (projects.isEmpty()) {
                break;
            }
            result.addAll(projects);
        }
        return result;
    }

    private Project getProject(Gitlab gitlab, String id) throws IOException {
        String url = gitlab.project(id);
        Request request = new Request.Builder().url(url).header(Gitlab.TOKEN_NAME, gitlab.getToken()).get().build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            String result = response.body().string();
            if (response.isSuccessful()) {
                return objectMapper.readValue(result, Project.class);
            }
            log.warn("获取项目信息 {} {}", response.code(), result);
        }
        throw new NullPointerException("未获取到指定的项目信息");
    }

    private List<Project> getProjects(Gitlab gitlab, int page) {
        String url = gitlab.projects(page);
        long start = System.currentTimeMillis();

        Request request = new Request.Builder().url(url).header(Gitlab.TOKEN_NAME, gitlab.getToken()).get().build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            String result = response.body().string();
            if (response.isSuccessful()) {
                List<Project> list = objectMapper.readValue(result, new TypeReference<List<Project>>() {
                });

                log.info("{}", list.size());
                return list;
            }
            log.warn("获取项目信息 {} {}", response.code(), result);

        } catch (Exception e) {
            log.warn("{} {}", (System.currentTimeMillis() - start), url, e);
        }
        return Collections.emptyList();
    }

    private void fetchHook(Gitlab gitlab, Project project) {
        String url = gitlab.hooks(project.getId());
        Request request = new Request.Builder().url(url).header(Gitlab.TOKEN_NAME, gitlab.getToken()).get().build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            String result = response.body().string();
            if (response.isSuccessful()) {
                List<WebHook> list = objectMapper.readValue(result, new TypeReference<List<WebHook>>() {
                });

                list.forEach(project::addHook);
            }
        } catch (Exception e) {
            log.warn("{}", url, e);
        }
    }

    /**
     * 修改 hook 信息
     *
     * @param gitlab  gitlab
     * @param project project
     * @param hook    hook
     */
    public void putHook(Gitlab gitlab, Project project, WebHook hook) {
        log.info("{} {}", project.getWebUrl(), hook);
        if (gitlab.isDoModify()) {
            return;
        }
        String url = gitlab.hook(project.getId(), hook.getId());
        try {
            String content = objectMapper.writeValueAsString(hook);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
            Request request = new Request.Builder()
                    .url(url)
                    .header(Gitlab.TOKEN_NAME, gitlab.getToken())
                    .put(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            try (Response response = call.execute()) {
                log.info("{}", response.body().string());
            }
        } catch (Exception e) {
            log.warn("", e);
        }
    }

    private void addHook(Gitlab gitlab, Project project) {
        log.info("{} {} to add hook", project.getId(), project.getWebUrl());
        WebHook hook = new WebHook();
        hook.setUrl(serverUrl + "/message/gitlab?token=" + DigestUtils.md5Hex(project.getWebUrl() + "company"));
        hook.setMergeRequestsEvents(true);
        hook.setConfidentialIssuesEvents(true);
        hook.setIssuesEvents(true);
        hook.setNoteEvents(true);
        hook.setPipelineEvents(true);
        hook.setPushEvents(true);
        hook.setTagPushEvents(true);

        String url = String.format("%s/api/v4/projects/%s/hooks", gitlab.getUrl(), project.getId());

        try {
            String content = objectMapper.writeValueAsString(hook);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
            Request request = new Request.Builder()
                    .url(url)
                    .header(Gitlab.TOKEN_NAME, gitlab.getToken())
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            try (Response response = call.execute()) {
                log.info("{}", response.body().string());
            }
        } catch (Exception e) {
            log.warn("", e);
        }
    }

}
