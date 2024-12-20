package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.models.AuthModules;
import com.example.teamcity.api.models.ServerAuthSettings;
import com.example.teamcity.api.requests.ServerAuthRequest;
import com.example.teamcity.api.spec.Specifications;
import io.qameta.allure.awaitility.AllureAwaitilityListener;
import org.awaitility.Awaitility;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;

// Сюда складываем то что нужно для апи теста
public class BaseApiTest extends BaseTest {

    public final ServerAuthRequest serverAuthRequest = new ServerAuthRequest(Specifications.superUserSpec());

    private AuthModules authModules;

    private boolean perProjectPermissions;

    @BeforeSuite(alwaysRun = true)
    public void setUpServerAuthSettings() {
        // Отображение Awaitility действий в Allure репорте, настройка Awaitility
        Awaitility.setDefaultConditionEvaluationListener(new AllureAwaitilityListener());
        Awaitility.setDefaultPollInterval(Duration.ofSeconds(3));
        Awaitility.setDefaultTimeout(Duration.ofSeconds(30));
        Awaitility.pollInSameThread();

        // Получаем текущие настройки perProjectPermissions
        perProjectPermissions = serverAuthRequest.read().getPerProjectPermissions();

        authModules = generate(AuthModules.class);

        // Обновляем значение perProjectPermissions на true
        serverAuthRequest.update(ServerAuthSettings.builder()
                        .perProjectPermissions(true)
                        .modules(authModules)
                .build());
    }

    @AfterSuite(alwaysRun = true)
    public void cleanUpServerAuthSettings() {
        // Возвращаем настройки perProjectPermissions в исходное значение
        serverAuthRequest.update(ServerAuthSettings.builder()
                .perProjectPermissions(perProjectPermissions)
                .modules(authModules)
                .build());
    }

}
