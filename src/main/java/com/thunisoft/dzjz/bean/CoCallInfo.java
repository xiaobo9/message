package com.thunisoft.dzjz.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author renxb
 */
@Slf4j
@Data
@ConfigurationProperties("cocall")
public class CoCallInfo {
    private String clientId;

    private String clientSecret;

    private String ccServerIp;

    private String ccServerPort;
}
