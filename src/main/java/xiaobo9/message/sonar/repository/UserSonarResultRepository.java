package xiaobo9.message.sonar.repository;

import xiaobo9.message.sonar.entity.UserSonarResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSonarResultRepository extends JpaRepository<UserSonarResult, String> {

    /**
     * 根据 用户 和 checkId 查询检查结果
     *
     * @param email   email
     * @param checkId 用户id
     * @return 结果
     */
    List<UserSonarResult> findByEmailAndCheckId(String email, String checkId);
}
