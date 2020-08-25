package framework.pojo;

import io.restassured.response.Response;

import java.util.HashMap;

public class ApiObjectModel {
    public String name;
    public HashMap<String, ApiObjectMethod> methods;

    private String basePath;
    public void setBasePath(String basePath){
        this.basePath = basePath;
    }

    public Response run(String methodString, HashMap<String, Object> params) {
        ApiObjectMethod method = methods.get(methodString);
        method.setBasePath(basePath);
        return method.run(params);
    }


}
