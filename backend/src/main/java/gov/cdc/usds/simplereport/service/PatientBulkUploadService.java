package gov.cdc.usds.simplereport.service;

import gov.cdc.usds.simplereport.api.model.errors.CsvProcessingException;
import gov.cdc.usds.simplereport.api.uploads.PatientBulkUploadResponse;
import gov.cdc.usds.simplereport.config.AuthorizationConfiguration;
import gov.cdc.usds.simplereport.db.model.Facility;
import gov.cdc.usds.simplereport.db.model.Organization;
import gov.cdc.usds.simplereport.db.model.auxiliary.UploadStatus;
import gov.cdc.usds.simplereport.service.PatientBulkUploadServiceAsync.*;
import gov.cdc.usds.simplereport.service.model.reportstream.FeedbackMessage;
import gov.cdc.usds.simplereport.validators.PatientBulkUploadFileValidator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service to upload a roster of patient data given a CSV input. Formerly restricted to superusers
 * but now available to end users.
 *
 * <p>Updated by emmastephenson on 10/24/2022
 */
@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class PatientBulkUploadService {

  private final OrganizationService _organizationService;
  private final PatientBulkUploadFileValidator _patientBulkUploadFileValidator;
  private final PatientBulkUploadServiceAsync _async;
  private final PersonService _personService;

  // This authorization will change once we open the feature to end users
  @AuthorizationConfiguration.RequireGlobalAdminUser
  public PatientBulkUploadResponse processPersonCSV(
      InputStream csvStream, UUID facilityId, HttpServletRequest request)
      throws IllegalArgumentException {

    PatientBulkUploadResponse result = new PatientBulkUploadResponse();
    result.setStatus(UploadStatus.FAILURE);

    Organization org = _organizationService.getCurrentOrganization();

    byte[] content;

    try {
      content = csvStream.readAllBytes();
    } catch (IOException e) {
      log.error("Error reading patient bulk upload CSV", e);
      throw new CsvProcessingException("Unable to read csv");
    }

    List<FeedbackMessage> errors =
        _patientBulkUploadFileValidator.validate(new ByteArrayInputStream(content));

    if (!errors.isEmpty()) {
      result.setErrors(errors.toArray(FeedbackMessage[]::new));
      return result;
    }

    // This is the point where we need to figure out multithreading
    // because what needs to happen is that we return a success message to the end user
    // but continue to process the csv (create person records) in the background.
    // Putting a pin in it for now.
    Optional<Facility> facility =
        Optional.ofNullable(facilityId).map(_organizationService::getFacilityInCurrentOrg);

    log.info(
        "Outside the @Async logic: "
            + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    _async.savePeople(facility, facilityId, org, content, request, _personService);
    result.setStatus(UploadStatus.SUCCESS);
    // eventually want to send an email here instead of return success
    return result;
  }
}
