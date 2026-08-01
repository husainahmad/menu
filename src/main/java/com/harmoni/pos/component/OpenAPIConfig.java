package com.harmoni.pos.component;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Harmoni POS Menu API")
                        .description("REST API for Harmoni POS Menu microservice")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Harmoni POS")
                                .email("support@harmonipos.com")));
    }
}
