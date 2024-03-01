package com.ricky.userinfo.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class DefaultValue {
    @Value("${defaultValue.userSex}")
    public String userSex;

    @Value("${defaultValue.userAbout}")
    public String userAbout;

    @Value("${defaultValue.userNickname}")
    public String userNickname;

    @Value("${defaultValue.avatarSuffix}")
    public String avatarPrefix;

    @Value("${defaultValue.avatar}")
    public String avatar;  //头像和壁纸的默认文件名

    @Value("${defaultValue.coverSuffix}")
    String coverPrefix;
    @Bean
    public String getUserSex() {
        return userSex;
    }

    @Bean
    public String getUserAbout() {
        return userAbout;
    }

    @Bean
    public String getUserNickname() {
        return userNickname;
    }

    @Bean
    public String getAvatarPrefix() {
        return avatarPrefix;
    }

    @Bean
    public String getAvatar() {
        return avatar;
    }

    @Bean
    public String getCoverPrefix() {
        return coverPrefix;
    }
}
