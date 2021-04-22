package xiaobo9.message.utils;

import java.util.UUID;

/**
 * uuid
 *
 * @author renxb
 */
public final class UUIDUtils {
    private UUIDUtils() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
