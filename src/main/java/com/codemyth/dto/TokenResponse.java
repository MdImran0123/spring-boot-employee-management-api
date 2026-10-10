package com.codemyth.dto;

import java.util.List;

public record TokenResponse(String token, String tokenType, List<String> roles) {

	public TokenResponse(String token, List<String> roles) {
		this(token, "Bearer", roles);
	}
}
