package service_swaggerdemo;

import framework.BaseTest;
import framework.pojo.ApiTestCaseModel;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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



}
