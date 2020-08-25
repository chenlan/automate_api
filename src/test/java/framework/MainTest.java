package framework;

import framework.pojo.*;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class MainTest extends BaseTest {

    @Feature("接口测试")
    @TestFactory
    public Stream<DynamicNode> dynamicTestsWithContainers2() {
        List<DynamicNode> nodes = new ArrayList<>();
        for (ServerPojo serverPojo:ApiManager.apiServer) {
            for (Map.Entry<String, HashMap<String, ApiActionMethod>> apiEntry:serverPojo.getAllTestCase().entrySet()) {
                List<DynamicNode> apiTest = new ArrayList<>();
                for (Map.Entry<String, ApiActionMethod> testMethodEntry :apiEntry.getValue().entrySet()) {
                    List<DynamicTest> methodTest = new ArrayList<>();
                    if(testMethodEntry.getValue().cases!=null){
                        for (ApiTestCaseModel testCase:testMethodEntry.getValue().cases) {
                            methodTest.add(dynamicTest(testCase.casename, () -> {
                                HashMap<String,Object> tSavePramaters = new HashMap<>();
                                for (ApiTestStep step:testCase.steps) {
                                    //step name
                                    String stepName = testMethodEntry.getKey();
                                    if(!step.description.isEmpty()){
                                       stepName = step.description;
                                    }
                                    Allure.step(stepName);

                                    //request
                                    String[] apiMethod = step.api.split("/");
                                    ApiObjectMethod apiObjectMethod = ApiManager.getApiObjectMethod(apiMethod[0],apiMethod[1]);
                                    if(apiObjectMethod!=null){
                                        Response response = apiObjectMethod.run(step.pramaters);
                                        checkPoint(response,step.pramaters,step.matchers);
                                        if(step.tsave!=null && step.tsave.size()>0){
                                            Allure.addAttachment("保存局部变量：","sss");
                                           step.tsave.entrySet().stream().forEach(paramterEntry->{
                                               tSavePramaters.put(paramterEntry.getKey(),response.getBody().path(paramterEntry.getValue().toString().replace("$.","")));
                                           });
                                        }
                                        if(step.gsave!=null&& step.gsave.size()>0){
                                            Allure.addAttachment("保存全局变量：","sss");
                                            step.gsave.entrySet().stream().forEach(paramterEntry->{
                                                ApiManager.getGSaveParamaters().put(paramterEntry.getKey(),response.getBody().path(paramterEntry.getValue().toString().replace("$.","")));
                                            });
                                        }
                                    }
                                }
                            }));
                        }
                        apiTest.add(dynamicContainer(testMethodEntry.getKey(), methodTest));
                    }
                }
               nodes.add(dynamicContainer(apiEntry.getKey(),apiTest));
            }
        }
        return nodes.stream();
    }
}
