package framework;

import org.junit.jupiter.api.BeforeAll;

public class ApiDDTest {
    @BeforeAll
    static void beforeAll(){
        ApiManager.loadAllApis();
    }

}
