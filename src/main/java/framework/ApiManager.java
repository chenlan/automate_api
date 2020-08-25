package framework;

import com.fasterxml.jackson.core.type.TypeReference;
import framework.common.DataFileManager;
import framework.pojo.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ApiManager {

    private static final String SERVERCONFIGFILE ="/ServerApiConfig.yaml";
    public static List<ServerPojo> apiServer;

    public static void loadAllApis(){
        apiServer = DataFileManager.readValue(SERVERCONFIGFILE,new TypeReference<List<ServerPojo>>(){});
        apiServer.forEach(serverPojo -> {
            //该服务API集合
            HashMap<String, ApiObjectModel> allApis = new HashMap<>();

            //获取该服务路径下的api yaml文件
            List<File> files = DataFileManager.findFile("src/main/resources"+serverPojo.getApiFilePath(),".yaml");

            //文件数据转换ApiModel,添加到API集合中
            files.stream().forEach(file->{
                    ApiObjectModel apiObjectModel = DataFileManager.readValue(file,ApiObjectModel.class);
                    apiObjectModel.setBasePath(serverPojo.getBasePath());
                    setApiMethodBaseUrl(apiObjectModel,serverPojo.getBasePath());
                    allApis.put(apiObjectModel.name,apiObjectModel);
            });
            serverPojo.setAllApis(allApis);

            //获取该服务路径下的yaml testcase文件
            List<File> testcaseDic = DataFileManager.findApiDir("src/main/resources"+serverPojo.getTestCasePath());
            HashMap<String, HashMap<String, ApiActionMethod>> testCaseMap = new HashMap<>();
            testcaseDic.stream().forEach(fileApi->{
                HashMap<String, ApiActionMethod> apiActionModelList = new HashMap<>();
                for (File caseDatafile : fileApi.listFiles()) {
                    ApiActionMethod actionTestCase = DataFileManager.readValue(caseDatafile, new TypeReference<ApiActionMethod>(){});
                    apiActionModelList.put(caseDatafile.getName().replace(".yaml",""), actionTestCase);
                }
                testCaseMap.put(fileApi.getName(),apiActionModelList);
            });
            serverPojo.setAllTestCase(testCaseMap);
        });
    }

    private static void setApiMethodBaseUrl(ApiObjectModel apiModel,String baseUrl){
        for (ApiObjectMethod apiMethod:apiModel.methods.values()) {
            apiMethod.setBasePath(baseUrl);
        }
    }

    public static ApiObjectMethod getApiObjectMethod(String apiName, String method){
        if(apiServer!=null){
            for (int i = 0; i < apiServer.size(); i++) {
                ServerPojo serverPojo = apiServer.get(i);
                ApiObjectMethod apiMethod = serverPojo.getAllApis().get(apiName).methods.get(method);
                return apiMethod;
            }
        }
        return null;
    }

    public static List<ApiTestCaseModel> getApiTestCase(String className,String method){
        if(apiServer!=null){
            String testClassName = className.substring(className.lastIndexOf(".")+1);
            String pageUrl = className.substring(0,className.indexOf("."));
            for (int i = 0; i < apiServer.size(); i++) {
                ServerPojo serverPojo = apiServer.get(i);
                String apiPath = serverPojo.getApiFilePath();
                if(apiPath.split("/")[1].equals(pageUrl)){
                    List<ApiTestCaseModel> caseDataList = serverPojo.getAllTestCase().get(testClassName).get(method).cases;
                    return caseDataList;
                }
            }
        }
        return null;
    }

    private static HashMap<String,Object> gSaveParamaters = new HashMap<>();
    public static HashMap<String,Object> getGSaveParamaters(){
        return gSaveParamaters;
    }

    private static String getClassName(String filePath){
        String classPath = filePath.replaceAll("src/main/resources/","").replaceAll(".yaml","");
        return classPath.replace("/",".");
    }

    public static <T> T getApiByClass(Class<T> t){
        if(apiServer!=null){
            String name = t.getName();
            String pageUrl = "/"+name.replace(".","/");
            for (int i = 0; i < apiServer.size(); i++) {
                ServerPojo serverPojo = apiServer.get(i);
                if(pageUrl.contains(serverPojo.getApiFilePath())){
                    T api = (T)serverPojo.getAllApis().get(t.getSimpleName());
                    return api;
                }
            }
        }
        return null;
    }



}
