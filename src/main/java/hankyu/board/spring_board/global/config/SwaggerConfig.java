package hankyu.board.spring_board.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("hankyu.board.spring_board.domain"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(apiKey())
                .securityContexts(List.of(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Board")
                .description("Talk_More REST API Documentation </br> admin : Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IlJPTEVfQURNSU4iLCJlbWFpbCI6ImFkbWluQGFkbWluLmNvbSIsIm1lbWJlcklkIjoiMSIsInN1YiI6ImFkbWluQGFkbWluLmNvbSIsImlhdCI6MTcwMjg2NDM2NiwiZXhwIjo1NTAzMDgwMzY2fQ.ApZDA_4viIy9bWr8zw6WxkBM4eov6sQHgFOD2jKNBrE")
                .license("finebears@naver.com")
                .licenseUrl("https://github.com/hankyu0301/spring_board")
                .version("1.0")
                .build();
    }
    private static List<SecurityScheme> apiKey() {
        List<SecurityScheme> apiKeyList = new ArrayList<>();
        apiKeyList.add(new ApiKey("Access", "Bearer Token", "header"));
        return apiKeyList;
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .operationSelector(oc -> oc.requestMappingPattern().startsWith("/api/")).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "global access");
        return List.of(new SecurityReference("Authorization", new AuthorizationScope[] {authorizationScope}));
    }
}
