package com.example.teamcity.api;

import com.example.teamcity.api.models.audit.AuditEvents;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.AUDIT;
import static com.example.teamcity.api.enums.Endpoint.USERS;

@Test(groups = {"Regression"})
public class AuditEventsTest extends BaseApiTest {

    @Test(description = "User should be able get all audit events", groups = {"Positive", "CRUD"})
    public void userGetAllAuditEventsTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var checkedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = checkedRequests.<AuditEvents>getRequest(AUDIT).read("");
        softy.assertThat(response).isNotNull();
        softy.assertThat(response.getAuditEvent().get(0).getAction()).isNotNull();
    }

    @Test(description = "User should be able get all audit events with field", groups = {"Positive", "CRUD"})
    public void userGetAllAuditEventsWithFieldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var checkedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var response = checkedRequests.<AuditEvents>getRequest(AUDIT).read("?fields=auditEvent");
        softy.assertThat(response).isNotNull();
        softy.assertThat(response.getCount()).isEqualTo(0);
        softy.assertThat(response.getAuditEvent().get(0).getAction()).isNotNull();
    }

    @Test(description = "User should be able get all audit event with field", groups = {"Positive", "CRUD"})
    public void userGetAllAuditEventWithFieldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var checkedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var auditEvent = checkedRequests.<AuditEvents>getRequest(AUDIT).read("?fields=auditEvent");
        var response = checkedRequests.<AuditEvents>getRequest(AUDIT)
                .read("?auditEventLocator:" + auditEvent.getAuditEvent().get(0).getId() + "&fields=auditEvent");

        softy.assertThat(response).isNotNull();
        softy.assertThat(response.getCount()).isEqualTo(0);
        softy.assertThat(response.getAuditEvent().get(0).getAction()).isNotNull();
    }
}
