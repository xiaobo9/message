package com.thunisoft.dzjz.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author renxb
 */
@Data
public class Project {
    private String name;

    private String description;

    @SerializedName("web_url")
    private String webUrl;
}
