package framework.pojo;

import java.util.HashMap;
import java.util.List;

public class ApiTestStep {
    public String description;
    public String api;
    public HashMap<String, Object> pramaters;
    public List<CheckPoint> matchers;
    public HashMap<String, Object> tsave;
    public HashMap<String, Object> gsave;

}
