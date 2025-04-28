package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow CORS for all paths
              //  .allowedOrigins("http://localhost:3001")
                .allowedOrigins("*") // Allow all origins (frontend domains like localhost:3000 etc.)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
               // .allowCredentials(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // (Optional) If you have static pages, you can map paths directly here
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // (Optional) Serve static resources if needed
    }
}
