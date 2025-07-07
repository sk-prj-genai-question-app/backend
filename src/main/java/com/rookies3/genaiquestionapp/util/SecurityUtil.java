package com.rookies3.genaiquestionapp.util;

import com.rookies3.genaiquestionapp.auth.entity.CustomUserDetails;
import org.springframework.security.core.Authentication;

public class SecurityUtil {

    public static Long extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}