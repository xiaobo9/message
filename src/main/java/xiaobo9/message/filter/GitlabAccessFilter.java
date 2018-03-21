package xiaobo9.message.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 访问记录吧？
 *
 * @author renxb
 */
@Slf4j
@WebFilter(filterName = "gitlabAccessFilter", urlPatterns = "/message/gitlab/*")
public class GitlabAccessFilter implements Filter {
    @Override
    public void destroy() {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // TODO 这里要校验一下 是不是 gitlab 服务器发起的请求，避免有人发送垃圾信息，通过 访问者的 ip 和已知的 gitlab 服务器的地址来做
        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig arg0) {
        // Do nothing
    }
}
