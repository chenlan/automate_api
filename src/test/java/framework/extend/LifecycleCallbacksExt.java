package framework.extend;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LifecycleCallbacksExt implements BeforeAllCallback, BeforeEachCallback,
        BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterEachCallback,AfterAllCallback{


    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {

    }
}
