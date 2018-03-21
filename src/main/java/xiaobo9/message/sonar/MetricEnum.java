package xiaobo9.message.sonar;

/**
 * 指标枚举
 *
 * @author renxb
 */
public enum MetricEnum {
    // 阻断问题
    BLOCKER("blocker_violations", "阻断问题"),
    // 严重问题
    CRITICAL("critical_violations", "严重问题"),
    // 主要问题
    MAJOR("major_violations", "主要问题"),
    MINOR("minor_violations", "次要问题"),

//        NEW_BLOCKER("new_blocker_violations", "新增阻断问题"),
//        NEW_CRITICAL("new_critical_violations", "新增严重问题"),
//        NEW_MAJOR("new_major_violations", "新增主要问题"),

    // 代码行数
    NCLOC("ncloc", "代码行数"),
    // 注释行
    COMMENT_LINES("comment_lines", "注释行"),

    // 单元测试覆盖率
    COVERAGE("coverage", "单元测试覆盖率"),
    // 分支覆盖率
    BRANCH_COVERAGE("branch_coverage", "分支覆盖率"),

    // 重复行
    DUPLICATED_LINES("duplicated_lines", "重复行"),
    // 重复行百分比
    DUPLICATED_LINES_DENSITY("duplicated_lines_density", "重复行百分比"),
    TESTS("tests", "单元测试数"),
    TEST_FAILURES("test_failures", "单元测试失败数"),
    TEST_SUCCESS_DENSITY("test_success_density", "单元测试成功百分比"),
    // 占位
    ;
    private String key;
    private String desc;

    MetricEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    /**
     * key
     *
     * @return key
     */
    public String key() {
        return this.key;
    }

    /**
     * desc
     *
     * @return desc
     */
    public String desc() {
        return this.desc;
    }
}