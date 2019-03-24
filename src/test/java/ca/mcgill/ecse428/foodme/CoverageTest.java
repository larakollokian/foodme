package ca.mcgill.ecse428.foodme;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuthenticationServiceTests.class,
        ControllerTests.class,
        FoodmeApplicationTests.class,
        ModelTests.class,
        SearchControllerTests.class
})
public class CoverageTest {

}
