package xiaobo9.message.sonar;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xiaobo9.message.sonar.bean.SonarDBProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * test
 *
 * @author renxb
 */
@Slf4j
@Getter
@Setter
@Service
public class SonarJdbcService {
    private final SonarDBProperties sonarDb;

    @Autowired
    public SonarJdbcService(SonarDBProperties sonarDb) {
        this.sonarDb = sonarDb;
    }

    List<Tip> getTips() {
        String sql = "select p.name,p.long_name,p.kee,issue.author_login,severity, sl\n" +
                "from projects p, (\n" +
                "	select project_uuid, author_login,severity,count(*) sl\n" +
                "	from issues\n" +
                "	where status in  ('OPEN','REOPENED') and author_login is not null\n" +
                "	group by project_uuid, author_login,severity\n" +
                ") issue\n" +
                "where p.project_uuid is not null and p.module_uuid is null\n" +
                "and p.project_uuid = issue.project_uuid\n" +
                "order by sl desc\n";
        Map<String, Tip> map = Maps.newHashMap();
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String kee = rs.getString("kee");

                String email = rs.getString("author_login");
                Tip tip = map.computeIfAbsent(kee + "!!abc!!" + email, k -> new Tip(email));
                tip.setProjectId(kee);
                tip.setProjectName(rs.getString("name"));
                int sl = rs.getInt("sl");
                tip.add(rs.getString("severity"), sl);
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return Lists.newArrayList(map.values());
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(sonarDb.getDriver());
            conn = DriverManager.getConnection(sonarDb.getUrl(), sonarDb.getUser(), sonarDb.getPassword());
        } catch (Exception e) {
            log.warn("", e);
        }
        return conn;
    }
}
