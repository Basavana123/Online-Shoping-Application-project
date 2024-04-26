package com.retail.ecom.utility;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/Api").allowedMethods("GET","POST","PUT","Delete").allowedHeaders("*").allowCredentials(true);
		//WebMvcConfigurer.super.addCorsMappings(registry);
	}
	

}
