package com.mktgus.autoatendimento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AutoatendimentoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AutoatendimentoApplication.class, args);

		// Obtém a porta do servidor web
		WebServerApplicationContext webContext = (WebServerApplicationContext) context;
		System.out.println("Aplicação rodando na porta: " + webContext.getWebServer().getPort());
	}
}
