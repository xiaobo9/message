package xiaobo9.message.gitlab.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 提交记录
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public class Commit {
    private String id;

    private String message;

    private String url;

    private Author author;

    /**
     * 提交记录的作者
     */
    @Getter
    @Setter
    @ToString
    public static class Author {
        private String name;

        private String email;
    }
}