package cn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class MyWebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    private MyInterceptor interceptor;
    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("/login");
        registry.addViewController("/").setViewName("/login");
        registry.addViewController("/main").setViewName("/main");
        registry.addViewController("/hello").setViewName("/hello");
        registry.addViewController("/banlance").setViewName("/banlance");
        registry.addViewController("/error").setViewName("/error");
        registry.addViewController("/transfer").setViewName("/transfer");
        registry.addViewController("/repassword").setViewName("/repassword");
        registry.addViewController("/log").setViewName("/log");
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/login","/getKaptcha","/doLogin");
    }
}
