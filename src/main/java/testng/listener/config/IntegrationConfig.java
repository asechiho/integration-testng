package testng.listener.config;

import ru.qatools.properties.DefaultValue;
import ru.qatools.properties.Property;
import ru.qatools.properties.PropertyLoader;
import ru.qatools.properties.Resource;

@Resource.Classpath("integration.properties")
public interface IntegrationConfig {

    @Property("test.tracking.use")
    @DefaultValue("false")
    boolean isTestTrackingUse();

    @Property("test.tracking.system.login")
    String getTestTrackingSystemLogin();

    @Property("test.tracking.system.password")
    String getTestTrackingSystemPassword();

    @Property("test.throwable.prefix")
    @DefaultValue("")
    String getThrowablePrefixes();

    @Property("test.status.fail")
    @DefaultValue("FAIL")
    String getStatusFail();

    @Property("test.status.pass")
    @DefaultValue("PASS")
    String getStatusPass();

    @Property("test.status.skip")
    @DefaultValue("SKIP")
    String getStatusSkip();

    @Property("test.tracking.system.base.url")
    String getBaseUrl();

    @Property("test.tracking.system.base.api.path")
    String getBaseApiPath();

    @Property("test.tracking.system.run.key")
    String getRunKey();

    static IntegrationConfig getInstance() {
        return PropertyLoader.newInstance().populate(IntegrationConfig.class);
    }
}
