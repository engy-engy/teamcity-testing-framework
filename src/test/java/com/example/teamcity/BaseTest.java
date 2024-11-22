package com.example.teamcity;

import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.TestData;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BaseTest {

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

}