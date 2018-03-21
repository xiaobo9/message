package xiaobo9.message.gitlab.builder;

import org.junit.Assert;
import org.junit.Test;

public class BuilderFactoryTest {

    @Test
    public void test() {
        BuilderFactory factory = new BuilderFactory(null);
        factory.afterPropertiesSet();

        Assert.assertTrue(factory.getBuilder("Issue Hook", "{}").orElse(null) instanceof IssueMsgBuilder);
        Assert.assertTrue(factory.getBuilder("Merge Request Hook", "{}").orElse(null) instanceof MergeRequestMsgBuilder);
        Assert.assertTrue(factory.getBuilder("Pipeline Hook", "{}").orElse(null) instanceof PipelineMsgBuilder);
        Assert.assertTrue(factory.getBuilder("Note Hook", "{}").orElse(null) instanceof NoteMsgBuilder);
        Assert.assertTrue(factory.getBuilder("Push Hook", "{}").orElse(null) instanceof PushMsgBuilder);

        Assert.assertFalse(factory.getBuilder("abc", "{}").isPresent());
    }
}