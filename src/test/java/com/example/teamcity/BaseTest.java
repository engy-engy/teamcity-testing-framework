package com.example.teamcity;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.TestData;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.BaseUiTest;
import io.qameta.allure.Allure;
import org.assertj.core.api.SoftAssertions;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.util.ResultsUtils.TAG_LABEL_NAME;

public class BaseTest implements IHookable {

    protected CheckedRequests superUserCheckRequests = new CheckedRequests(Specifications.superUserSpec());
    protected UncheckedRequests uncheckedSuperUser = new UncheckedRequests(Specifications.superUserSpec());
    protected TestData testData;
    protected SoftAssertions softy;

    /**
     * Автоматически генерируются тестовые данные с помощью метода generate(),
     * который использует генератор тестовых данных
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        softy = new SoftAssertions();
        testData = generate();
    }

    /**
     * Удаляются созданные в ходе теста сущности с помощью TestDataStorage.
     */
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        softy.assertAll();
        TestDataStorage.getStorage().deleteCreatedEntities();
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        softy = new SoftAssertions();
        // Добавляем сьют и тэг для лучшей информативности и возможности фильтрации тестов в Allure репорте
        if (BaseUiTest.class.isAssignableFrom(testResult.getTestClass().getRealClass())) {
            var browser = Config.getProperty("browser");
            Allure.suite(browser);
            Allure.label(TAG_LABEL_NAME, browser);
        }
        callBack.runTestMethod(testResult);
        softy.assertAll();
    }

}