package gov.cdc.usds.simplereport.validators;

import static gov.cdc.usds.simplereport.api.Translators.CANADIAN_STATE_CODES;
import static gov.cdc.usds.simplereport.api.Translators.STATE_CODES;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import gov.cdc.usds.simplereport.service.model.reportstream.FeedbackMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

public class CsvValidatorUtils {

  private static final String ZIP_CODE_REGEX = "^[0-9]{5}(?:-[0-9]{4})?$";

  /// 000-000-0000
  private static final String PHONE_NUMBER_REGEX = "^[1-9]\\d{2}-\\d{3}-\\d{4}$";

  // MM/DD/YYYY OR M/D/YYYY
  private static final String DATE_REGEX = "^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$";

  // MM/DD/YYYY HH:mm, MM/DD/YYYY H:mm, M/D/YYYY HH:mm OR M/D/YYYY H:mm
  private static final String DATE_TIME_REGEX =
      "^\\d{1,2}\\/\\d{1,2}\\/\\d{4}( ([0-1]?[0-9]|2[0-3]):[0-5][0-9])?$";
  private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
  private static final String CLIA_REGEX = "^[A-Za-z0-9]{2}[Dd][A-Za-z0-9]{7}$";
  private static final String ALPHABET_REGEX = "^[a-zA-Z]+$";
  private static final Set<String> VALID_STATE_CODES =
      Stream.concat(
              STATE_CODES.stream().map(String::toLowerCase),
              CANADIAN_STATE_CODES.stream().map(String::toLowerCase))
          .collect(Collectors.toSet());
  private static final String UNKNOWN_LITERAL = "unknown";

  private static final Set<String> GENDER_VALUES =
      Set.of(
          "m", "male",
          "f", "female",
          "o", "other",
          "u", UNKNOWN_LITERAL,
          "a", "ambiguous",
          "n", "not applicable");
  private static final Set<String> ETHNICITY_VALUES =
      Set.of(
          "2135-2", "hispanic or latino",
          "2186-5", "not hispanic or latino",
          "unk", UNKNOWN_LITERAL);
  private static final Set<String> RACE_VALUES =
      Set.of(
          "1002-5", "american indian or alaska native",
          "2028-9", "asian",
          "2054-5", "black or african american",
          "2076-8", "native hawaiian or other pacific islander",
          "2106-3", "white",
          "2131-1", "other",
          "asku", "ask but unknown",
          "unk", UNKNOWN_LITERAL);
  private static final Set<String> YES_NO_VALUES =
      Set.of(
          "y", "yes",
          "n", "no",
          "u", "unk");
  private static final Set<String> TEST_RESULT_VALUES =
      Set.of("positive", "negative", "not detected", "detected", "invalid result");
  private static final Set<String> SPECIMEN_TYPE_VALUES =
      Set.of(
          "nasal swab",
          "nasopharyngeal swab",
          "anterior nares swab",
          "throat swab",
          "oropharyngeal swab",
          "whole blood",
          "plasma",
          "serum");
  private static final Set<String> RESIDENCE_VALUES =
      Set.of(
          "22232009", "hospital",
          "2081004", "hospital ship",
          "32074000", "long term care hospital",
          "224929004", "secure hospital",
          "42665001", "nursing home",
          "30629002", "retirement home",
          "74056004", "orphanage",
          "722173008", "prison-based care site",
          "20078004", "substance abuse treatment center",
          "257573002", "boarding house",
          "224683003", "military accommodation",
          "284546000", "hospice",
          "257628001", "hostel",
          "310207003", "sheltered housing",
          "57656006", "penal institution",
          "285113009", "religious institutional residence",
          "285141008", "work (environment)",
          "32911000", "homeless");
  private static final Set<String> PATIENT_ROLE_VALUES =
      Set.of("staff", "resident", "student", "visitor", UNKNOWN_LITERAL);
  private static final Set<String> PHONE_NUMBER_TYPE_VALUES = Set.of("mobile", "landline");
  private static final Set<String> TEST_RESULT_STATUS_VALUES = Set.of("f", "c");
  private static final String ITEM_SCOPE = "item";
  private static final String REPORT_SCOPE = "report";

  private CsvValidatorUtils() {
    throw new IllegalStateException("CsvValidatorUtils is a utility class");
  }

  public static List<FeedbackMessage> validateTestResult(ValueOrError input) {
    return validateSpecificValueOrSNOMED(input, TEST_RESULT_VALUES);
  }

  public static List<FeedbackMessage> validateSpecimenType(ValueOrError input) {
    return validateSpecificValueOrSNOMED(input, SPECIMEN_TYPE_VALUES);
  }

  public static List<FeedbackMessage> validateResidence(ValueOrError input) {
    return validateInSet(input, RESIDENCE_VALUES);
  }

  public static List<FeedbackMessage> validateYesNoAnswer(ValueOrError input) {
    return validateInSet(input, YES_NO_VALUES);
  }

  public static List<FeedbackMessage> validateEthnicity(ValueOrError input) {
    return validateInSet(input, ETHNICITY_VALUES);
  }

  public static List<FeedbackMessage> validateRace(ValueOrError input) {
    return validateInSet(input, RACE_VALUES);
  }

  public static List<FeedbackMessage> validateBiologicalSex(ValueOrError input) {
    return validateInSet(input, GENDER_VALUES);
  }

  public static List<FeedbackMessage> validateState(ValueOrError input) {
    return validateInSet(input, VALID_STATE_CODES);
  }

  public static List<FeedbackMessage> validateTestResultStatus(ValueOrError input) {
    return validateInSet(input, TEST_RESULT_STATUS_VALUES);
  }

  public static List<FeedbackMessage> validateZipCode(ValueOrError input) {
    return validateRegex(input, ZIP_CODE_REGEX);
  }

  public static List<FeedbackMessage> validatePhoneNumber(ValueOrError input) {
    return validateRegex(input, PHONE_NUMBER_REGEX);
  }

  public static List<FeedbackMessage> validatePhoneNumberType(ValueOrError input) {
    return validateInSet(input, PHONE_NUMBER_TYPE_VALUES);
  }

  public static List<FeedbackMessage> validateRole(ValueOrError input) {
    return validateInSet(input, PATIENT_ROLE_VALUES);
  }

  public static List<FeedbackMessage> validateClia(ValueOrError input) {
    return validateRegex(input, CLIA_REGEX);
  }

  public static List<FeedbackMessage> validateDate(ValueOrError input) {
    return validateRegex(input, DATE_REGEX);
  }

  public static List<FeedbackMessage> validateDateTime(ValueOrError input) {
    return validateRegex(input, DATE_TIME_REGEX);
  }

  public static List<FeedbackMessage> validateEmail(ValueOrError input) {
    return validateRegex(input, EMAIL_REGEX);
  }

  public static Map<String, String> getNextRow(MappingIterator<Map<String, String>> valueIterator)
      throws IllegalArgumentException {
    try {
      return valueIterator.next();
    } catch (RuntimeJsonMappingException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  public static ValueOrError getValue(Map<String, String> row, String name, boolean isRequired) {
    String value = row.get(name);
    if (isRequired && (value == null || value.trim().isEmpty())) {
      return new ValueOrError(new FeedbackMessage(REPORT_SCOPE, name + " is a required column."));
    }
    return new ValueOrError(value, name);
  }

  public static MappingIterator<Map<String, String>> getIteratorForCsv(InputStream csvStream)
      throws IllegalArgumentException {
    try {
      BufferedReader csvStreamBuffered =
          new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8));

      return new CsvMapper()
          .enable(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS)
          .readerFor(Map.class)
          .with(CsvSchema.builder().setUseHeader(true).build())
          .readValues(csvStreamBuffered);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  private static List<FeedbackMessage> validateSpecificValueOrSNOMED(
      ValueOrError input, Set<String> acceptableValues) {
    List<FeedbackMessage> errors = new ArrayList<>();
    String value = parseString(input.getValue());
    if (value == null) {
      return errors;
    }
    boolean nonSNOMEDValue = value.matches(ALPHABET_REGEX);
    if (nonSNOMEDValue) {
      return validateInSet(input, acceptableValues);
    }
    return errors;
  }

  private static List<FeedbackMessage> validateRegex(ValueOrError input, String regex) {
    List<FeedbackMessage> errors = new ArrayList<>();
    String value = parseString(input.getValue());
    if (value == null) {
      return errors;
    }
    if (!value.matches(regex)) {
      errors.add(
          new FeedbackMessage(
              ITEM_SCOPE,
              input.getValue() + " is not a valid value for column " + input.getHeader()));
    }
    return errors;
  }

  private static List<FeedbackMessage> validateInSet(
      ValueOrError input, Set<String> acceptableValues) {
    List<FeedbackMessage> errors = new ArrayList<>();
    String value = parseString(input.getValue());
    if (value == null) {
      return errors;
    }
    if (!acceptableValues.contains(value.toLowerCase())) {
      errors.add(
          new FeedbackMessage(
              ITEM_SCOPE,
              input.getValue() + " is not an acceptable value for column " + input.getHeader()));
    }
    return errors;
  }

  private static String parseString(String value) {
    if (value == null || "".equals(value)) {
      return null;
    }
    return value.trim();
  }

  @Getter
  public static class ValueOrError {
    public final List<FeedbackMessage> error;
    public final String value;
    public final String header;

    public ValueOrError(String value, String header) {
      this.value = value;
      this.error = Collections.emptyList();
      this.header = header;
    }

    public ValueOrError(FeedbackMessage error) {
      this.value = null;
      this.header = null;
      this.error = List.of(error);
    }

    public List<FeedbackMessage> getPossibleError() {
      return this.error;
    }
  }
}