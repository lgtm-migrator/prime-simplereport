package gov.cdc.usds.simplereport.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextAwareTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {

    SecurityContext currentContext = SecurityContextHolder.getContext();
    RequestAttributes requestContext = RequestContextHolder.getRequestAttributes();

    return () -> {
      try {
        SecurityContextHolder.setContext(currentContext);
        RequestContextHolder.setRequestAttributes(requestContext);
        runnable.run();
      } finally {
        RequestContextHolder.resetRequestAttributes();
      }
    };
  }
}
