package xiaobo9.message.gitlab.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 被指定人
 *
 * @author renxb
 */
@Getter
@Setter
@ToString
public
class Assignee {
    private String name;

    private String username;
}