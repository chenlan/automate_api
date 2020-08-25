package framework.pojo;

import framework.common.UtilTool;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.output.WriterOutputStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ApiObjectMethod {
    public HashMap<String,Object> querys;
    public HashMap<String,Object> headers;
    public HashMap<String,Object> form;
    public String description;
    public String body;
    public String method = "get";
    public String url = "";
    public String filter;


    private HashMap<String, Object> params;
    private String basePath;

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Response run(HashMap<String, Object> params) {
        this.params = params;
        return run();
    }

    private Response run(){
        RequestSpecification requestSpecification = given();

        if(filter!=null){
            try {
                Object filterObj =  Class.forName(filter).getDeclaredConstructor().newInstance();
                requestSpecification.filter((Filter) filterObj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        String fullUrl = basePath+url;
        HashMap<String,Object> pathParamsMap  = getPathParamsMap(url);
        if(pathParamsMap.size()>0){
            requestSpecification.pathParams(pathParamsMap);
        }

        if(querys!=null){
            requestSpecification.queryParams(replaceMap(querys));
        }
        if(headers!=null){
            requestSpecification.headers(replaceMap(headers));
        }
        if(form!=null){
            if(form.containsKey("File")){
                HashMap<String,Object> outputReplaceParamsMap = new HashMap();
                HashMap<String,Object> fileMap = this.getReplaceFileMap(form,outputReplaceParamsMap);
                for (Map.Entry<String,Object> item : fileMap.entrySet()){
                    requestSpecification.multiPart(item.getKey(),this.getFile(item.getValue().toString()));
                }
                if(outputReplaceParamsMap.size()>0){
                    requestSpecification.params(outputReplaceParamsMap);
                }
            }else{
                requestSpecification.params(replaceMap(form));
            }
        }
        if(body!=null){
            requestSpecification.header(new Header("Content-Type","application/json"));
            requestSpecification.body(this.params.get("body"));
        }

        //Allure.addAttachment("请求：",method+" "+fullUrl);
        return requestSpecification
                .when().log().all().request(method,fullUrl)
                .then().log().all().extract().response();
    }


    private File getFile(String filePath){
        File file = new File(filePath);
        return file;
    }

    private HashMap<String,Object> replaceMap(HashMap<String,Object> map){
        HashMap<String,Object> replaceNewMap = new HashMap<>();
        for (Map.Entry<String,Object> item : map.entrySet()){
            if(this.params.containsKey(item.getKey())){
                replaceNewMap.put(item.getKey(),this.params.get(item.getKey()));
            }
        }
        return replaceNewMap;
    }

    private HashMap<String,Object> getReplaceFileMap(HashMap<String,Object> inputMap,HashMap<String,Object> outputReplaceParamsMap){
        HashMap<String,Object> replaceFileMap = new HashMap<>();
        for (Map.Entry<String,Object> item : inputMap.entrySet()){
            if(item.getKey().equals("File") && this.params.containsKey(item.getValue())){
                replaceFileMap.put(item.getValue().toString(),this.params.get(item.getValue()));
            }
            else if(this.params.containsKey(item.getKey())){
                outputReplaceParamsMap.put(item.getKey(),this.params.get(item.getKey()));
            }
        }
        return replaceFileMap;
    }

//    private HashMap<String,Object> replaceMap2(HashMap<String,Object> map){
//        HashMap<String,Object> replaceNewMap = new HashMap<>();
//        for (Map.Entry<String,Object> item : map.entrySet()){
//            replaceNewMap.put(item.getKey(),replaceString(item.getValue().toString()));
//        }
//        return replaceNewMap;
//    }

    private HashMap<String,Object> getPathParamsMap(String url){
        HashMap<String,Object> pathParamsMap= new HashMap<String, Object>();
        List<String> params = UtilTool.getParam(url);
        for (String param : params){
             if(this.params.containsKey(param)){
                pathParamsMap.put(param,this.params.get(param));
             }
        }
        return pathParamsMap;
    }

}
