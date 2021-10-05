package gov.cdc.usds.simplereport.db.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.cdc.usds.simplereport.api.model.accountrequest.OrganizationAccountRequest;
import gov.cdc.usds.simplereport.db.model.OrganizationQueueItem;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrganizationQueueRepositoryTest extends BaseRepositoryTest {

  @Autowired private OrganizationQueueRepository _repo;

  @Test
  void createAndFindQueueItem_success() {
    String orgName = "My House";
    String orgExtId = "My-House-External-Id";
    String email = "fake@email.org";
    OrganizationQueueItem saved =
        _repo.save(
            new OrganizationQueueItem(
                orgName,
                orgExtId,
                new OrganizationAccountRequest(
                    "First", "Last", email, "800-555-1212", "CA", null, null)));
    assertNotNull(saved);
    assertNotNull(saved.getInternalId());
    List<OrganizationQueueItem> unverifiedQueueItems = _repo.findAllNotIdentityVerified();
    assertEquals(1, unverifiedQueueItems.size());

    OrganizationQueueItem fetched = unverifiedQueueItems.get(0);
    assertEquals(orgName, fetched.getOrganizationName());
    assertEquals(orgExtId, fetched.getExternalId());
    assertEquals(email, fetched.getRequestData().getEmail());
  }
}