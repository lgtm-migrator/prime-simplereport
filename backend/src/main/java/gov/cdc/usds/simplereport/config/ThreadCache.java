package gov.cdc.usds.simplereport.config;

import java.util.HashMap;
import org.springframework.cloud.context.scope.thread.ThreadLocalScopeCache;

public class ThreadCache extends HashMap<String, String> {
  private static final InheritableThreadLocal<ThreadLocalScopeCache> cache =
      new InheritableThreadLocal<ThreadLocalScopeCache>() {
        public ThreadLocalScopeCache initialValue() {
          return new ThreadLocalScopeCache();
        }
      };

  public static ThreadLocalScopeCache getCache() {
    return cache.get();
  }
}
