//package com.tms.tm_auth_service.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.tms.tm_auth_service.dto.request.RegisterRequest;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//class AuthServiceTest {
//
////	@Autowired
////	private AuthService authService;
////
////	@Autowired
////	private UserRepository userRepository;
////
////	@Autowired
////	private PasswordEncoder passwordEncoder;
////
////	@Test
////	void register_encodesPasswordBeforePersisting() {
////		RegisterRequest request = new RegisterRequest();
////		request.setUsername("encoded_user");
////		request.setPassword("SecurePass1");
////
////		authService.register(request);
////
////		var user = userRepository.findByUsername("encoded_user").orElseThrow();
////		assertThat(user.getPassword()).isNotEqualTo("SecurePass1");
////		assertThat(passwordEncoder.matches("SecurePass1", user.getPassword())).isTrue();
////	}
////
////	@Test
////	void register_normalizesUsernameToLowerCase() {
////		RegisterRequest request = new RegisterRequest();
////		request.setUsername("MixedCase_User");
////		request.setPassword("SecurePass1");
////
////		authService.register(request);
////
////		assertThat(userRepository.findByUsername("mixedcase_user")).isPresent();
////	}
////
////	@Test
////	void register_withDuplicateUsername_throwsConflict() {
////		RegisterRequest request = new RegisterRequest();
////		request.setUsername("dup_user");
////		request.setPassword("SecurePass1");
////
////		authService.register(request);
////
////		assertThatThrownBy(() -> authService.register(request))
////				.isInstanceOf(DuplicateResourceException.class);
////	}
//}
