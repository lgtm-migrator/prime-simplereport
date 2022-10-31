package gov.cdc.usds.simplereport.service;

import static gov.cdc.usds.simplereport.api.Translators.parsePersonRole;
import static gov.cdc.usds.simplereport.api.Translators.parsePhoneType;
import static gov.cdc.usds.simplereport.api.Translators.parseUserShortDate;
import static gov.cdc.usds.simplereport.api.Translators.parseYesNo;

import com.fasterxml.jackson.databind.MappingIterator;
import gov.cdc.usds.simplereport.db.model.Facility;
import gov.cdc.usds.simplereport.db.model.Organization;
import gov.cdc.usds.simplereport.db.model.PhoneNumber;
import gov.cdc.usds.simplereport.db.model.auxiliary.StreetAddress;
import gov.cdc.usds.simplereport.validators.CsvValidatorUtils;
import gov.cdc.usds.simplereport.validators.PatientBulkUploadFileValidator;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientBulkUploadServiceAsync {

  private final AddressValidationService _addressValidationService;
  private final OrganizationService _orgService;

  //    @Async("threadPoolTaskExecutor")
  @Async
  @Transactional
  public void savePeople(
      Optional<Facility> facility,
      UUID facilityId,
      Organization currentOrg,
      byte[] content,
      HttpServletRequest requestFromOtherThread,
      PersonService _personService) {
    System.out.println("BOOOYAH here we are in the async method");
    log.info(
        "Inside the @Async logic: "
            + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    final MappingIterator<Map<String, String>> valueIterator =
        CsvValidatorUtils.getIteratorForCsv(new ByteArrayInputStream(content));

    while (valueIterator.hasNext()) {
      System.out.println("here we are in the whillllle loop");
      final Map<String, String> row = CsvValidatorUtils.getNextRow(valueIterator);

      try {

        PatientBulkUploadFileValidator.PatientUploadRow extractedData =
            new PatientBulkUploadFileValidator.PatientUploadRow(row);

        // Fetch address information
        StreetAddress address =
            _addressValidationService.getValidatedAddress(
                extractedData.getStreet().getValue(),
                extractedData.getStreet2().getValue(),
                extractedData.getCity().getValue(),
                extractedData.getState().getValue(),
                extractedData.getZipCode().getValue(),
                null);

        String country = "USA";

        if (_personService.isDuplicatePatient(
            extractedData.getFirstName().getValue(),
            extractedData.getLastName().getValue(),
            parseUserShortDate(extractedData.getDateOfBirth().getValue()),
            currentOrg,
            facility)) {
          continue;
        }

        _personService.addPatient(
            currentOrg,
            facilityId,
            null, // lookupID
            extractedData.getFirstName().getValue(),
            extractedData.getMiddleName().getValue(),
            extractedData.getLastName().getValue(),
            extractedData.getSuffix().getValue(),
            parseUserShortDate(extractedData.getDateOfBirth().getValue()),
            address,
            country,
            List.of(
                new PhoneNumber(
                    parsePhoneType(extractedData.getPhoneNumberType().getValue()),
                    extractedData.getPhoneNumber().getValue())),
            parsePersonRole(extractedData.getRole().getValue(), false),
            List.of(extractedData.getEmail().getValue()),
            extractedData.getRace().getValue(),
            extractedData.getEthnicity().getValue(),
            null,
            extractedData.getBiologicalSex().getValue(),
            parseYesNo(extractedData.getResidentCongregateSetting().getValue()),
            parseYesNo(extractedData.getEmployedInHealthcare().getValue()),
            null,
            null);
      } catch (IllegalArgumentException e) {
        String errorMessage = "Error uploading patient roster";
        log.error(
            errorMessage
                + " for organization "
                + currentOrg.getExternalId()
                + " and facility "
                + facilityId);
        throw new IllegalArgumentException(errorMessage);
      }
    }

    log.info("CSV patient upload completed for {}", currentOrg.getOrganizationName());
    // send email here
  }
}
