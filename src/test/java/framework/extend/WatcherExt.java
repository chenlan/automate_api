package framework.extend;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class WatcherExt implements TestWatcher {
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("执行失败："+context.getTestClass()+"->"+context.getTestMethod());
        errStep(context.getRequiredTestMethod().getName());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.out.println("Disabled："+context.getTestClass()+"->"+context.getTestMethod());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("执行成功："+context.getTestClass()+"->"+context.getTestMethod());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        System.out.println("Aborted："+context.getTestClass()+"->"+context.getTestMethod());
    }

    @Step("失败")
    public void errStep(String meth){
        Allure.addAttachment("监控测试失败",meth);
    }
}
