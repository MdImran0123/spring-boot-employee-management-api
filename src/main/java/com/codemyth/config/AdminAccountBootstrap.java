package com.codemyth.config;

import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codemyth.model.AppUser;
import com.codemyth.model.Role;
import com.codemyth.repository.AppUserRepository;
import com.codemyth.repository.RoleRepository;

@Component
public class AdminAccountBootstrap {

	private static final String DEV_ADMIN_USERNAME = "admin@dev";

	private final AppUserRepository appUserRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AppSecurityProperties securityProperties;
	private final Environment environment;

	public AdminAccountBootstrap(AppUserRepository appUserRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, AppSecurityProperties securityProperties, Environment environment) {
		this.appUserRepository = appUserRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.securityProperties = securityProperties;
		this.environment = environment;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void ensureAdminAccount() {
		if (environment.matchesProfiles("test")) {
			return;
		}

		String username = environment.matchesProfiles("dev") ? DEV_ADMIN_USERNAME
				: securityProperties.bootstrap().username();
		if (username == null || username.isBlank()) {
			return;
		}

		Role adminRole = roleRepository.findByName("ADMIN")
				.orElseThrow(() -> new IllegalStateException("ADMIN role missing; run Flyway migrations"));

		AppUser admin = appUserRepository.findByUsername(username).orElseGet(() -> {
			AppUser user = new AppUser();
			user.setUsername(username);
			user.setEnabled(true);
			return user;
		});

		admin.setPasswordHash(passwordEncoder.encode(securityProperties.bootstrap().password()));
		admin.setRoles(Set.of(adminRole));
		appUserRepository.save(admin);
	}
}
