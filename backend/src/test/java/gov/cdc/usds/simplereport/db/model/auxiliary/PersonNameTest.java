package gov.cdc.usds.simplereport.db.model.auxiliary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import java.util.List;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;

class PersonNameTest {
  @Test
  void allFields_toFHIR() {
    var personName = new PersonName("first", "middle", "last", "jr");

    var actual = personName.toFHIR();

    assertThat(actual).returns(personName.getLastName(), from(HumanName::getFamily));
    assertStringTypeListEqualsStringList(
        List.of(personName.getFirstName(), personName.getMiddleName()), actual.getGiven());
    assertStringTypeListEqualsStringList(List.of(personName.getSuffix()), actual.getSuffix());
  }

  @Test
  void noMiddleName_toFHIR() {
    var personName = new PersonName("first", null, "last", "jr");

    var actual = personName.toFHIR();

    assertStringTypeListEqualsStringList(List.of(personName.getFirstName()), actual.getGiven());
  }

  @Test
  void emptyPersonName_toFHIR() {
    var personName = new PersonName(null, null, null, null);

    var actual = personName.toFHIR();

    assertThat(actual.getGiven()).hasSize(0);
    assertThat(actual.getSuffix()).hasSize(0);
    assertThat(actual.getFamily()).isNull();
  }
  // note: getGiven and getSuffix return array lists of StringType which are difficult to compare
  public static void assertStringTypeListEqualsStringList(
      List<String> actual, List<StringType> expected) {
    assertThat(actual.size()).isEqualTo(expected.size());
    expected.forEach(st -> assertThat(actual).contains(st.getValue()));
  }
}