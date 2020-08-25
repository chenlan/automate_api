package framework;

import com.jayway.jsonpath.JsonPath;
import framework.common.DataFileManager;
import framework.extend.LifecycleCallbacksExt;
import framework.extend.Paramtrolect;
import framework.extend.WatcherExt;
import framework.pojo.*;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.provider.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith({WatcherExt.class, LifecycleCallbacksExt.class, Paramtrolect.class})
public class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeAll
    public static void loadAllApiModel(){
        ApiManager.loadAllApis();
        setAllureLog();
    }

    private static void setAllureLog(){
        try {
            //ToLoggerPrintStream loggerPrintStream = new ToLoggerPrintStream(logger);
            //RestAssured.config = RestAssured.config().logConfig(new LogConfig(loggerPrintStream.getPrintStream(), true));
            PrintStream ps = new PrintStream(new File("data/logs/test.log"));
            RestAssured.config = config().logConfig(new LogConfig(ps,true));
            PrintStream printStream = new PrintStream(new File("data/logs/log.log"));
            RestAssured.filters(new RequestLoggingFilter().logRequestTo(printStream),new ResponseLoggingFilter().logResponseTo(printStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected static Stream<Arguments> getCaseData(){
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        String method = stackTrace.getMethodName();
        String className = stackTrace.getClassName();
        List<ApiTestCaseModel> testDatas =  ApiManager.getApiTestCase(className,method);
        List<Arguments> argumentsList = new ArrayList<>();
        testDatas.stream().forEach(caseDataModel -> {
            argumentsList.add(arguments(caseDataModel.casename,caseDataModel));
        });
        return argumentsList.stream();
    }

    protected void run(ApiTestCaseModel testCase){
        HashMap<String,Object> tSavePramaters = new HashMap<>();
        for (ApiTestStep step:testCase.steps) {
            step(step,tSavePramaters);
        }
    }

    protected <T> void checkPoint(Response response, HashMap paramaters,List<CheckPoint> matchers){
        ResponseBody responseBody = response.getBody();
        if(matchers.size()>0){
            List<Executable> assertlist = new ArrayList<>();
            for (CheckPoint checkPoint : matchers) {
                T actualValue=responseBody.path(checkPoint.actual.replace("$.",""));
                assertlist.add(()->assertThat(actualValue, getMatcher(paramaters,checkPoint,actualValue)));
            }
            assertAll("assertAll test....",assertlist);
        }
        else {
            response.then().statusCode(200);
        }
    }

    private <T> Matcher getMatcher(HashMap<String,Object>data, CheckPoint checkPoint,T actualValue){
        T t = (T) getExpectValue(data,checkPoint.expect);
        String matcherString = checkPoint.match;
        String checkString ="";
        checkString = "actual:"+checkPoint.actual+"->"+actualValue+"\n";
        checkString+="matcher:"+matcherString+"\n";
        checkString+="expect:"+checkPoint.expect+"->"+t;
        Allure.addAttachment("校验",checkString);
        if(matcherString.equals("=") ||matcherString.equals("equals")){
            return equalTo(t);
        }
        else if(matcherString.equals("contains")){
            return Matchers.containsString(t.toString());
        }
        return null;
    }

    private <T> T getExpectValue(HashMap<String,Object> data,T expect){
       String jsonString = DataFileManager.toJsonString(data);
       if(expect.toString().substring(0,2).equals("$.")){
         return  JsonPath.parse(jsonString).read(expect.toString());
       }
       return expect;
    }

    @Step("{step.description}")
    private void step(ApiTestStep step,HashMap<String,Object> tSaveParameters){
        //setAlluerStepName(step); //step名称
        //request
        String[] apiMethod = step.api.split("/");
        ApiObjectMethod apiObjectMethod = ApiManager.getApiObjectMethod(apiMethod[0],apiMethod[1]);
        if(apiObjectMethod!=null){
            //setAllureParamater(step);  //请求参数
            Response response = apiObjectMethod.run(step.pramaters);
            setAllureRequestResponse(response); //响应
            checkPoint(response,step.pramaters,step.matchers);
            if(step.tsave!=null && step.tsave.size()>0){
                step.tsave.entrySet().stream().forEach(paramterEntry->{
                    String key = paramterEntry.getKey();
                    tSaveParameters.put(key,response.getBody().path(paramterEntry.getValue().toString().replace("$.","")));
                });
                Allure.addAttachment("保存局部变量：",tSaveParameters.toString());
            }
            if(step.gsave!=null&& step.gsave.size()>0){
                step.gsave.entrySet().stream().forEach(paramterEntry->{
                    String key = paramterEntry.getKey();
                    ApiManager.getGSaveParamaters().put(key,response.getBody().path(paramterEntry.getValue().toString().replace("$.","")));
                });
                Allure.addAttachment("保存全局变量：",ApiManager.getGSaveParamaters().toString());
            }
        }
    }

    private void setAlluerStepName(ApiTestStep step){
        //step name
        String stepName = "step1:";
        if(!step.description.isEmpty()){
            stepName = step.description;
        }
        Allure.step(stepName);
    }

    private void setAllureParamater(ApiTestStep step){
        step.pramaters.entrySet().forEach(p->{
            Allure.parameter(p.getKey(),p.getValue());
        });
    }

    private void setAllureRequestResponse(Response response){
        //try {
        //Allure.addAttachment("response","application/json",new FileInputStream(new File("data/logs/pp.json")),".json");
        //Allure.addAttachment("响应","application/json",response.asString(),".json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        try {
            Allure.addAttachment("接口请求响应日志",
                    new FileInputStream("data/logs/test.log"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
