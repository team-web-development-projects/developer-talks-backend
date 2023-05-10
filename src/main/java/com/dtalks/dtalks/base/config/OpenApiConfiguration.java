package com.dtalks.dtalks.base.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Value("${spring.swagger.url}")
    String url;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("dtalks API Document")
                .version("1.0.0");
        return new OpenAPI()
                .addServersItem(new Server().url(url))
                .addServersItem(new Server().url("http://localhost:8080"))
                .components(new Components())
                .info(info);
    }
}
