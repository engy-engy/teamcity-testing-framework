package com.example.teamcity.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.BasePageElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public abstract class BasePage {
    protected static final Duration BASE_WAITING = Duration.ofSeconds(30);
    protected static final Duration LONG_WAITING = Duration.ofSeconds(180);

    protected <T extends BasePageElement> List<T> generatePageElements(
            ElementsCollection collection, Function<SelenideElement, T> cretor)
    {
        return collection.stream().map(cretor).toList();
    }
    // ElementCollection: Selenide element 1, Selenide element 2 ...
    // collection.stream() -> Конвеер: Selenide element 1, Selenide element 2 ...
    // creator(Selenide element 1) -> T -> add to list
    // creator(Selenide element 2) -> T -> add to list
}
