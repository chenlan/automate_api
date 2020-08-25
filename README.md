# automate_api

RestAssured+Junit5+Allure

相关主要文件介绍（若有swagger文件，可通过swagger-codegen自动生成自定义格式文件）：

1、接口配置文件PetApi.yaml 
    
    name: PetApi
    methods:

      addPet:
        description: Add a new pet to the store
        url: /pet
        method: POST
        body: Pet body # Pet body| Pet object that needs to be added to the store

      deletePet:
        description: Deletes a pet
        url: /pet/{petId}
        method: DELETE
        headers:
          apiKey: $apiKey # String apiKey|

      findPetsByStatus:
        description: Finds Pets by status
        url: /pet/findByStatus
        method: GET
        querys:
          status: $status # List<String> status| Status values that need to be considered for filter

2、每一个接口的测试用例配置文件，如：addPet_test.yaml、deletePet_test.yaml。可配置多个接口关联数据关系和多个断言。

    name: addPet_test
    description: addpet描述
    cases:
      - casename: 添加Pet用例001
        steps:
          - description: 请求Pet接口
            api: PetApi/addPet
            pramaters:
              body:
                id: 12
                category: null
                name: "cat"
                photoUrls: []
                tags:
                  - id: 343
                    name: "buzhidao"
                status: "AVAILABLE"
            tsave:
              petId: $.id
            matchers:
              - actual: $.id
                match: =
                expect: $.body.id

3、测试用例执行文件，PetApiTest.java。利用Junit5控制接口的执行，结合Allure生成易读性报表。

    @DisplayName("PetApi")
    @Epic("大标")
    @Feature("Pet")
    public class PetApiTest extends BaseTest {

        private static Stream<Arguments> addPet_test(){
            return getCaseData();
        }

        @Tag("接口")
        @Story("addPet")
        @DisplayName("addPet")
        @ParameterizedTest(name="{0}")
        @MethodSource("addPet_test")
        public void addPet_test(String casename, ApiTestCaseModel testCase){
            //TODO 实时数据参数替换
            //DataFileManager.templateStringReplace(rawData.data,map);
            run(testCase);
        }
        
4、测试用例基类BaseTest.java

    @ExtendWith({WatcherExt.class, LifecycleCallbacksExt.class, Paramtrolect.class})
    public class BaseTest {
        private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

        @BeforeAll
        public static void loadAllApiModel(){
            ApiManager.loadAllApis();
            setAllureLog();
        }
        
        private static void setAllureLog(){...}
        protected static Stream<Arguments> getCaseData(){...}
        
        protected void run(ApiTestCaseModel testCase){
            HashMap<String,Object> tSavePramaters = new HashMap<>();
            for (ApiTestStep step:testCase.steps) {
                step(step,tSavePramaters);
            }
        }
        protected <T> void checkPoint(Response response, HashMap paramaters,List<CheckPoint> matchers){...}
        
        @Step("{step.description}")
        private void step(ApiTestStep step,HashMap<String,Object> tSaveParameters){...}
        ...
        ...
    }
    
5、ApiManager管理ApiModel.
    
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
    }
    
6、根据ServerApiConfig.yaml配置加载不用服务的API

    - name: "swaggerdemo server"
      basePath: "https://petstore.swagger.io/v2"
      apiFilePath: "/service_swaggerdemo/api"
      testCasePath: "/service_swaggerdemo/case"
 
注：该框架只是初版，还有许多待优化的地方，后期会继续更新...
