package com.example.cloudFriends.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


/**
 * Swagger使用的配置文件
 * @author CloudyW
 * @version 1.0
 */
@Configuration
@EnableSwagger2WebMvc
public class Swagger2Configuration {
    @Bean(value = "createRestApi")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 标注控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.example.cloudFriends.Controller"))
                .paths(PathSelectors.any())
                .build();
    }

    //基本信息的配置，信息会在api文档上显示
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("cloudFriends")
                .contact("cloudyW")
                .description("cloudFriends的接口文档")
                .termsOfServiceUrl("https://github.com/zimuwood")
                .version("1.0")
                .build();
    }
}