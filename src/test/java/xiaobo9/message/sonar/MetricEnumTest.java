package xiaobo9.message.sonar;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MetricEnumTest {

    @Test
    public void test() {
        List<String> list = Arrays.stream(MetricEnum.values()).map(MetricEnum::key).collect(Collectors.toList());
        Assert.assertTrue(list.contains("blocker_violations"));
    }

}