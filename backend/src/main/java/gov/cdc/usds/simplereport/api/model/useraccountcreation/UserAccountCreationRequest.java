package gov.cdc.usds.simplereport.api.model.useraccountcreation;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAccountCreationRequest {
  @Size(max = RequestConstants.LARGE_REQUEST_STRING_LIMIT)
  private String activationToken;

  @Size(max = RequestConstants.STANDARD_REQUEST_STRING_LIMIT)
  private String password;
}