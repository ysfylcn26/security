package com.business.auth.security.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlList {

    public static final String[] WHITE_LIST_URL = {
            "/api/auth/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html"};

    public static final String[] ADMIN_LIST_URL = {
            "/api/account/**",
            "/api/admin/**"
            };

    public static final String[] MANAGER_LIST_URL = {
            "/api/account/**",
            "/api/manager/**"
            };

    public static final String[] USER_LIST_URL = {
            "/api/account/**",
            "/api/user/**"
            };

}
