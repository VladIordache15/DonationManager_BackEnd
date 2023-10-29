package demo.msg.javatraining.donationmanager.config.security;

import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Cors {

        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {

                @Override
                public void addCorsMappings(@Nullable CorsRegistry registry) {
                    assert registry != null;
                    registry.addMapping("/**");
                    registry.addMapping("/users/update/**");
                }
            };
        }
}
