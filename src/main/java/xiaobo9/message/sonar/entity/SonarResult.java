package xiaobo9.message.sonar.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(schema = "message", name = "t_sonar_issues")
public class SonarResult {
    @Id
    @Column(name = "c_project_id")
    private String projectId;
    @Column(name = "c_project_name")
    private String projectName;
    @Column(name = "dt_check_date")
    private Date checkDate;

    private Integer majorCount;
}
