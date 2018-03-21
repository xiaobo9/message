package com.thunisoft.dzjz.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author renxb
 */
@Data
public class User {
    private String name;

    private String username;

    @JsonProperty("avatar_url")
    private String avatarUrl;
}
