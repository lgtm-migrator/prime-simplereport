package gov.cdc.usds.simplereport.validators;

import static org.assertj.core.api.Assertions.assertThat;

import gov.cdc.usds.simplereport.api.model.filerow.PatientUploadRow;
import gov.cdc.usds.simplereport.api.model.filerow.TestResultRow;
import gov.cdc.usds.simplereport.service.model.reportstream.FeedbackMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FileValidatorTest {

  FileValidator<PatientUploadRow> patientBulkUploadFileValidator =
      new FileValidator<>(PatientUploadRow::new);
  FileValidator<TestResultRow> testResultFileValidator = new FileValidator<>(TestResultRow::new);

  @Test
  void emptyFile_returnsError() {
    // GIVEN
    InputStream input = loadCsv("patientBulkUpload/empty.csv");
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(1);
    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    assertThat(errorMessages).contains("File is missing headers and other required data");
    assertThat(errors.get(0).getIndices()).isEqualTo(null);
  }

  @Test
  void malformedCsv_returnsError() {
    // GIVEN
    InputStream input = loadCsv("patientBulkUpload/malformed.csv");
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(1);
    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    assertThat(errorMessages)
        .contains(
            "File has the incorrect number of columns or empty rows. Please make sure all columns match the data template, and delete any empty rows.");
    assertThat(errors.get(0).getIndices()).isEqualTo(List.of(2, 4));
  }

  @Test
  void missingHeaders_returnsErrors() {
    // GIVEN
    InputStream input = loadCsv("patientBulkUpload/missingHeaders.csv");
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(12);
    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    List<List<Integer>> indices =
        errors.stream().map(FeedbackMessage::getIndices).collect(Collectors.toList());
    assertThat(errorMessages)
        .contains(
            "first_name is a required column.",
            "last_name is a required column.",
            "race is a required column.",
            "date_of_birth is a required column.",
            "biological_sex is a required column.",
            "ethnicity is a required column.",
            "street is a required column.",
            "state is a required column.",
            "zip_code is a required column.",
            "phone_number is a required column.",
            "employed_in_healthcare is a required column.",
            "resident_congregate_setting is a required column.");
    indices.forEach(i -> assertThat(i).isEqualTo(List.of(2)));
  }

  @Test
  void missingRequiredFields_returnsErrors() {
    // GIVEN
    InputStream input = loadCsv("patientBulkUpload/missingRequiredFields.csv");
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(2);
    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    List<List<Integer>> indices =
        errors.stream().map(FeedbackMessage::getIndices).collect(Collectors.toList());
    assertThat(errorMessages)
        .contains("race is a required column.", "ethnicity is a required column.");
    indices.forEach(i -> assertThat(i).isEqualTo(List.of(2)));
  }

  @Test
  void invalidValues_returnError() {
    // GIVEN
    InputStream input = loadCsv("patientBulkUpload/invalidValues.csv");
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(13);
    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    List<List<Integer>> indices =
        errors.stream().map(FeedbackMessage::getIndices).collect(Collectors.toList());
    assertThat(errorMessages)
        .contains(
            "11/3/8 is not an acceptable value for the date_of_birth column",
            "african american is not an acceptable value for the race column",
            "androgynous is not an acceptable value for the biological_sex column",
            "latinx is not an acceptable value for the ethnicity column",
            "group home is not an acceptable value for the resident_congregate_setting column",
            "n/a is not an acceptable value for the employed_in_healthcare column",
            "doctor is not an acceptable value for the role column",
            "Alaska is not an acceptable value for the state column",
            "1234 is not an acceptable value for the zip_code column",
            "America is not an acceptable value for the country column",
            "4108675309 is not an acceptable value for the phone_number column",
            "cell is not an acceptable value for the phone_number_type column",
            "janedoe.com is not an acceptable value for the email column");
    indices.forEach(i -> assertThat(i).isEqualTo(List.of(2)));
  }

  @Test
  void invalidValuesAndMissingFieldsInMultipleRows_returnError() {
    // GIVEN
    InputStream input =
        loadCsv("patientBulkUpload/invalidValuesAndMissingFieldsInMultipleRows.csv");
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(3);
    assertThat(errors.get(0).getScope()).isEqualTo("item");
    assertThat(errors.get(0).getMessage())
        .isEqualTo("african american is not an acceptable value for the race column");
    assertThat(errors.get(0).getIndices()).isEqualTo(List.of(2, 3));
    assertThat(errors.get(1).getScope()).isEqualTo("item");
    assertThat(errors.get(1).getMessage()).isEqualTo("ethnicity is a required column.");
    assertThat(errors.get(1).getIndices()).isEqualTo(List.of(4, 5));
    assertThat(errors.get(2).getScope()).isEqualTo("item");
    assertThat(errors.get(2).getMessage()).isEqualTo("race is a required column.");
    assertThat(errors.get(2).getIndices()).isEqualTo(List.of(4, 5));
  }

  @Test
  void emptyRow_returnsError() {
    // GIVEN
    InputStream input = loadCsv("patientBulkUpload/emptyRow.csv");
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(12);
    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    List<List<Integer>> indices =
        errors.stream().map(FeedbackMessage::getIndices).collect(Collectors.toList());
    assertThat(errorMessages)
        .contains(
            "first_name is a required column.",
            "last_name is a required column.",
            "race is a required column.",
            "date_of_birth is a required column.",
            "biological_sex is a required column.",
            "ethnicity is a required column.",
            "street is a required column.",
            "state is a required column.",
            "zip_code is a required column.",
            "phone_number is a required column.",
            "employed_in_healthcare is a required column.",
            "resident_congregate_setting is a required column.");
    indices.forEach(i -> assertThat(i).isEqualTo(List.of(4)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "patientBulkUpload/missingOptionalFields.csv",
        "patientBulkUpload/extraColumns.csv",
        "patientBulkUpload/valid.csv"
      })
  void file_isValid(String csvFile) {
    // GIVEN
    InputStream input = loadCsv(csvFile);
    // WHEN
    List<FeedbackMessage> errors = patientBulkUploadFileValidator.validate(input);
    // THEN
    assertThat(errors).isEmpty();
  }

  @Test
  void testResults_validFile() {
    // GIVEN
    InputStream input = loadCsv("testResultUpload/test-results-upload-valid.csv");
    // WHEN
    List<FeedbackMessage> errors = testResultFileValidator.validate(input);
    // THEN
    assertThat(errors).isEmpty();
  }

  @Test
  void testResultsFile_invalidHeaders() {
    // GIVEN
    InputStream input = new ByteArrayInputStream("invalid\nyes".getBytes());
    // WHEN
    List<FeedbackMessage> errors = testResultFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(33);

    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    List<List<Integer>> indices =
        errors.stream().map(FeedbackMessage::getIndices).collect(Collectors.toList());
    assertThat(errorMessages)
        .contains(
            "patient_last_name is a required column.",
            "patient_first_name is a required column.",
            "patient_street is a required column.",
            "patient_city is a required column.",
            "patient_state is a required column.",
            "patient_zip_code is a required column.",
            "patient_county is a required column.",
            "patient_phone_number is a required column.",
            "patient_dob is a required column.",
            "patient_gender is a required column.",
            "patient_race is a required column.",
            "patient_ethnicity is a required column.",
            "accession_number is a required column.",
            "equipment_model_name is a required column.",
            "test_performed_code is a required column.",
            "test_result is a required column.",
            "order_test_date is a required column.",
            "test_result_date is a required column.",
            "specimen_type is a required column.",
            "ordering_provider_id is a required column.",
            "ordering_provider_last_name is a required column.",
            "ordering_provider_first_name is a required column.",
            "ordering_provider_street is a required column.",
            "ordering_provider_city is a required column.",
            "ordering_provider_state is a required column.",
            "ordering_provider_zip_code is a required column.",
            "ordering_provider_phone_number is a required column.",
            "testing_lab_clia is a required column.",
            "testing_lab_name is a required column.",
            "testing_lab_street is a required column.",
            "testing_lab_city is a required column.",
            "testing_lab_state is a required column.",
            "testing_lab_zip_code is a required column.");
    indices.forEach(i -> assertThat(i).isEqualTo(List.of(2)));
  }

  @Test
  void testResultsFile_invalidValues() {
    // GIVEN
    InputStream input = loadCsv("testResultUpload/test-results-upload-invalid-values.csv");
    // WHEN
    List<FeedbackMessage> errors = testResultFileValidator.validate(input);
    // THEN
    assertThat(errors).hasSize(34);

    List<String> errorMessages =
        errors.stream().map(FeedbackMessage::getMessage).collect(Collectors.toList());
    List<List<Integer>> indices =
        errors.stream().map(FeedbackMessage::getIndices).collect(Collectors.toList());
    assertThat(errorMessages)
        .contains(
            "x is not an acceptable value for the patient_state column",
            "x is not an acceptable value for the ordering_provider_state column",
            "x is not an acceptable value for the testing_lab_state column",
            "x is not an acceptable value for the ordering_facility_state column",
            "x is not an acceptable value for the patient_zip_code column",
            "x is not an acceptable value for the ordering_provider_zip_code column",
            "x is not an acceptable value for the testing_lab_zip_code column",
            "x is not an acceptable value for the ordering_facility_zip_code column",
            "x is not an acceptable value for the patient_phone_number column",
            "x is not an acceptable value for the ordering_provider_phone_number column",
            "x is not an acceptable value for the testing_lab_phone_number column",
            "x is not an acceptable value for the ordering_facility_phone_number column",
            "x is not an acceptable value for the patient_dob column",
            "x is not an acceptable value for the illness_onset_date column",
            "x is not an acceptable value for the order_test_date column",
            "x is not an acceptable value for the specimen_collection_date column",
            "x is not an acceptable value for the testing_lab_specimen_received_date column",
            "x is not an acceptable value for the test_result_date column",
            "x is not an acceptable value for the date_result_released column",
            "x is not an acceptable value for the patient_email column",
            "x is not an acceptable value for the testing_lab_clia column",
            "x is not an acceptable value for the patient_race column",
            "x is not an acceptable value for the patient_gender column",
            "x is not an acceptable value for the patient_ethnicity column",
            "x is not an acceptable value for the pregnant column",
            "x is not an acceptable value for the employed_in_healthcare column",
            "x is not an acceptable value for the symptomatic_for_disease column",
            "x is not an acceptable value for the resident_congregate_setting column",
            "x is not an acceptable value for the hospitalized column",
            "x is not an acceptable value for the icu column",
            "x is not an acceptable value for the residence_type column",
            "x is not an acceptable value for the test_result column",
            "x is not an acceptable value for the test_result_status column",
            "x is not an acceptable value for the specimen_type column");
    indices.forEach(i -> assertThat(i).isEqualTo(List.of(2)));
  }

  private InputStream loadCsv(String csvFile) {
    return FileValidatorTest.class.getClassLoader().getResourceAsStream(csvFile);
  }
}
