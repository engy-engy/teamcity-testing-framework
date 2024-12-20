package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class BuildProjectElement extends BasePageElement {

    private SelenideElement list;
    private SelenideElement item;
    private SelenideElement button;

    public BuildProjectElement(SelenideElement element) {
        super(element);
        this.list = find("div[class*=BuildTypes__list]");
        this.item = find("span[class*=MiddleEllipsis]");
        this.button = find("span[class*=MiddleEllipsis__searchable]");
    }
}
