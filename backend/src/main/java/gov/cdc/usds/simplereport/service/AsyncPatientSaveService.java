package gov.cdc.usds.simplereport.service;

import gov.cdc.usds.simplereport.db.model.Person;
import gov.cdc.usds.simplereport.db.model.PhoneNumber;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class AsyncPatientSaveService {

  @Autowired PersonService _personService;

  @Async
  @Transactional
  public void savePatients(List<Person> people, List<PhoneNumber> phoneNumbers) {
    System.out.println("BOOYAH in the async method");
    System.out.println(
        "security context: "
            + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    _personService.addPatientsAndPhoneNumbers(people, phoneNumbers);
    System.out.println("processed all the patients!");
  }
}
