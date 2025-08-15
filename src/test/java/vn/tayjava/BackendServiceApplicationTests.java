package vn.tayjava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.tayjava.controller.AuthenticationController;
import vn.tayjava.controller.EmailController;
import vn.tayjava.controller.UserController;

@SpringBootTest
class BackendServiceApplicationTests {

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private EmailController emailController;

	@InjectMocks
	private AuthenticationController authenticationController;


	@Test
	void contextLoads() {

		Assertions.assertNotNull(userController);
		Assertions.assertNotNull(emailController);
		Assertions.assertNotNull(authenticationController);
	}

}
