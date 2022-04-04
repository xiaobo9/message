package xiaobo9.message.gitlab.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BuilderFactoryTest {

    @Test
    public void test() {
        BuilderFactory factory = new BuilderFactory(null);
        factory.afterPropertiesSet();

        Assertions.assertTrue(factory.getBuilder("Issue Hook", "{}").orElse(null) instanceof IssueMsgBuilder);
        Assertions.assertTrue(factory.getBuilder("Merge Request Hook", "{}").orElse(null) instanceof MergeRequestMsgBuilder);
        Assertions.assertTrue(factory.getBuilder("Pipeline Hook", "{}").orElse(null) instanceof PipelineMsgBuilder);
        Assertions.assertTrue(factory.getBuilder("Note Hook", "{}").orElse(null) instanceof NoteMsgBuilder);
        Assertions.assertTrue(factory.getBuilder("Push Hook", "{}").orElse(null) instanceof PushMsgBuilder);

        Assertions.assertFalse(factory.getBuilder("abc", "{}").isPresent());
    }
}