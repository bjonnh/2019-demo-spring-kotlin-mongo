package net.nprod.demo.swagger

import com.google.common.collect.Lists.newArrayList
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("net.nprod.demo"))
                .paths(PathSelectors.ant("/people/*"))
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, newArrayList(ResponseMessageBuilder().code(500)
                        .message("500 message")
                        .responseModel(ModelRef("Error"))
                        .build(),
                        ResponseMessageBuilder().code(403)
                                .message("Forbidden!!!!!")
                                .build()))
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo("My REST API", "Some custom description of API.", "API TOS", "Terms of service", Contact("John Doe", "www.example.com", "myeaddress@company.com"), "License of API", "API license URL", listOf())
    }
}