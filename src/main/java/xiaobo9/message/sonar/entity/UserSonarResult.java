package xiaobo9.message.sonar.entity;

import xiaobo9.message.sonar.Tip;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(schema = "message", name = "t_user_sonar_issues")
public class UserSonarResult {
    @Id
    @Column(name = "c_id")
    private String id;
    @Column(name = "c_check_id")
    private String checkId;
    @Column(name = "c_author")
    private String author;
    @Column(name = "c_email")
    private String email;
    @Column(name = "c_project_id")
    private String projectId;
    @Column(name = "c_project_name")
    private String projectName;
    @Column(name = "c_result")
    private String result;
    @Column(name = "dt_check_date")
    private Date checkDate;

    @Transient
    private Tip tip;
}
