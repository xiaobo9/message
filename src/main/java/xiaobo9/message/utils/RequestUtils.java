package xiaobo9.message.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * request utils
 *
 * @author renxb
 */
public final class RequestUtils {
    private RequestUtils() {
    }

    public static String getRemoteIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (isInValidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInValidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInValidIp(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static boolean isInValidIp(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

}
