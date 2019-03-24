package ca.mcgill.ecse428.foodme;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/**
 * This class is used to faciliate test coverage
 * Uncomment annotation to run the class
 * */
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
