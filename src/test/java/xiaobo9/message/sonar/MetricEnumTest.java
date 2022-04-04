package xiaobo9.message.sonar;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MetricEnumTest {

    @Test
    public void test() {
        List<String> list = Arrays.stream(MetricEnum.values()).map(MetricEnum::key).collect(Collectors.toList());
        Assertions.assertTrue(list.contains("blocker_violations"));
    }

}