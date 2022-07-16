package com.studyolls.studyolls.infra.config;

import com.studyolls.studyolls.modules.notification.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class Webconfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String>staticResourcePath=Arrays.stream(StaticResourceLocation.values())
                        .flatMap(StaticResourceLocation::getPatterns)
                        .collect(Collectors.toList());
        staticResourcePath.add("/node_modules/**");


        registry.addInterceptor(notificationInterceptor)
                .excludePathPatterns(staticResourcePath);
    }
}
