package framework.pojo;

import java.util.HashMap;

public class ServerPojo {
    private String name;
    private String basePath;
    private String apiFilePath;
    private String testCasePath;
    private HashMap<String, ApiObjectModel> allApis;
    private HashMap<String, HashMap<String, ApiActionMethod>> allTestCase;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getApiFilePath() {
        return apiFilePath;
    }

    public void setApiFilePath(String apiFilePath) {
        this.apiFilePath = apiFilePath;
    }

    public HashMap<String, ApiObjectModel> getAllApis() {
        return allApis;
    }

    public void setAllApis(HashMap<String, ApiObjectModel> allApis) {
        this.allApis = allApis;
    }

    public String getTestCasePath() {
        return testCasePath;
    }

    public void setTestCasePath(String testCasePath) {
        this.testCasePath = testCasePath;
    }

    public HashMap<String, HashMap<String, ApiActionMethod>> getAllTestCase() {
        return allTestCase;
    }

    public void setAllTestCase(HashMap<String, HashMap<String, ApiActionMethod>> allTestCase) {
        this.allTestCase = allTestCase;
    }
}
