package com.prgrms.artzip.common.config;

import com.fasterxml.classmate.TypeResolver;
import com.prgrms.artzip.common.util.MyPageable;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

  private final TypeResolver typeResolver = new TypeResolver();

  @Bean
  public Docket apiV1() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .securitySchemes(List.of(accessToken(), refreshToken()))
        .securityContexts(List.of(securityContext()))
        .alternateTypeRules(
            AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class), typeResolver.resolve(MyPageable.class)))
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/api/v1/**"))
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("ArtZip API Docs")
        .description("Descriptions of ArtZip API - Programmers Devcourse Team BackFro")
        .version("1.0")
        .build();
  }

  private ApiKey accessToken() {
    return new ApiKey("accessJWT", "accessToken", "header");
  }
  private ApiKey refreshToken() {
    return new ApiKey("refreshJWT", "refreshToken", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder().securityReferences(defaultAuth()).build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return List.of(new SecurityReference("accessJWT", authorizationScopes), new SecurityReference("refreshJWT", authorizationScopes));
  }
}
