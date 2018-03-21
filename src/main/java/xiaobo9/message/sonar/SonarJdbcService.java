package xiaobo9.message.sonar;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${db.host:sonar.company.com}")
    private String dbHost;
    @Value("${db.port:3306}")
    private String dbPort;
    @Value("${db.user}")
    private String dbUser;
    @Value("${db.password}")
    private String dbPassword;
    @Value("${gitlab-message.url}")
    private String serverUrl;

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
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%s/sonar", dbHost, dbPort);
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (Exception e) {
            log.warn("", e);
        }
        return conn;
    }
}
