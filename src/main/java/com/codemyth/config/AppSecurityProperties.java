package com.codemyth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProperties(Bootstrap bootstrap) {

	public record Bootstrap(String username, String password) {
	}
}
