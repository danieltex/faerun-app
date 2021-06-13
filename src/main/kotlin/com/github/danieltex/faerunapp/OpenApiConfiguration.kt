package com.github.danieltex.faerunapp

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Bean
    fun openAPI(): OpenAPI {
        val contact = Contact()
        contact.email = "danieltex@gmail.com";
        contact.name = "Daniel Teixeira dos Santos";
        contact.url = "https://github.com/danieltex/faerun-app"
        return OpenAPI()
            .info(
                Info()
                    .title("Faerun API")
                    .description("Uma API para gerenciar o saldo e empréstimos entre bolsões de água.")
                    .version("1.2")
                    .contact(contact)
            )
    }
}
