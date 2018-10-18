package io.microvibe.booster.test;

import io.microvibe.booster.config.ProfileNames;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import io.microvibe.booster.BootstrapApplication;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * @author Qt
 * @since Aug 22, 2018
 */
@RunWith(SpringRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@SpringBootTest(classes = {BootstrapApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles(ProfileNames.SUT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseTestCase {
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
}
