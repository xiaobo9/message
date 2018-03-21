package com.thunisoft.dzjz.configuration;

import com.thunisoft.dzjz.bean.CoCallInfo;
import com.thunisoft.dzjz.filter.GitlabAccessFilter;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author renxb
 */
@Configuration
@EnableConfigurationProperties({ CoCallInfo.class })
public class MessageConfiguration {

    /**
     * 应用在负载均衡后的话，获取真正的用户ip地址
     * @return remoteIpFilter
     */
    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }

    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //添加过滤器
        registration.setFilter(new GitlabAccessFilter());
        //设置过滤路径，/*所有路径
        registration.addUrlPatterns("/message/gitlab/*");
        //设置优先级
        registration.setName("GitlabAccessFilter");
        //设置优先级
        registration.setOrder(1);
        return registration;
    }

}
