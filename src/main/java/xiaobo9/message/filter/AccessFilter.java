package xiaobo9.message.filter;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import xiaobo9.message.utils.RequestUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 访问记录
 *
 * @author renxb
 */
@Slf4j
@WebFilter(filterName = "accessFilter", urlPatterns = {"/*"})
public class AccessFilter implements Filter {

    private static final String REDIRECT = "/redirect";

    private final Set<String> excludes = Sets.newHashSet("/css", "/images", "/js", "/favicon.ico");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String uri = request.getRequestURI();

        for (String exclude : excludes) {
            if (uri.startsWith(exclude)) {
                filterChain.doFilter(request, res);
                return;
            }
        }

        String serverName = request.getServerName();
        String remoteIpAddr = RequestUtils.getRemoteIpAddr(request);
        if (REDIRECT.equals(uri)) {
            String location = parseRedirectUrl(request);

            log.info("要废弃的逻辑 {} {} 访问了 {} {} {}", request.getRemoteAddr(), remoteIpAddr, serverName, uri, location);

            ((HttpServletResponse) res).sendRedirect(location);
            return;
        }
        if (uri.endsWith(REDIRECT)) {
            log.info("{} {} 访问了 {} {} {}", request.getRemoteAddr(), remoteIpAddr, serverName, uri, parseRedirectUrl(request));
        } else {
            log.info("{} {} 访问了 {} {}", request.getRemoteAddr(), remoteIpAddr, serverName, uri);
        }
        filterChain.doFilter(request, res);
    }

    private String parseRedirectUrl(HttpServletRequest request) {
        String url = request.getParameter("url");
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        return new String(Base64.decodeBase64(url), StandardCharsets.UTF_8);
    }

}
