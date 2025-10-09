package com.brenda.recetario.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API de Recetas", version = "1.0", description = "Documentación de los endpoints del backend de recetas desarrollada por Brenda Giambelluca", contact = @Contact(name = "Brenda Giambelluca", email = "brengiambelluca@gmail.com")))
public class SwaggerConfig {
}
