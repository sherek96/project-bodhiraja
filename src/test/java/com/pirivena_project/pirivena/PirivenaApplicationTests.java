// Tests backend rules directly, without using the frontend.
package com.pirivena_project.pirivena;

// Purpose: Tests the pirivena application behavior without using the frontend.

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.pirivena_project.pirivena.service.UserService;
import com.pirivena_project.pirivena.service.ClassroomRosterService;
import com.pirivena_project.pirivena.repository.ClassroomRepository;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PirivenaApplicationTests {

	@Autowired
	private UserService userService;
	@Autowired
	private ClassroomRosterService classroomRosterService;
	@Autowired
	private ClassroomRepository classroomRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void userPagingQueryExecutes() {
		assertNotNull(userService.searchUsers("", null, null, null, null, null,
				0, 10, "accountType", false));
		assertNotNull(userService.searchUsers("", null, null, null, "ACTIVE", null,
				0, 10, "username", false));
	}

	@Test
	void classroomRosterQueryExecutes() {
		classroomRepository.findAll().stream().findFirst()
				.ifPresent(classroom -> assertNotNull(classroomRosterService.getRoster(classroom.getId())));
	}

}
