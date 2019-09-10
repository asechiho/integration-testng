package testng.listener.interfaces;

import ru.qatools.properties.DefaultValue;
import ru.qatools.properties.Property;
import ru.qatools.properties.PropertyLoader;
import ru.qatools.properties.Resource;

@Resource.Classpath("integration.properties")
public interface IntegrationConfig {

    @Property("test.tracking.use")
    @DefaultValue("false")
    boolean isTestTrackingUse();

    @Property("test.tracking.system")
    @DefaultValue("xray")
    String getTestTrackingSystem();

    @Property("test.tracking.system.login")
    String getTestTrackingSystemLogin();

    @Property("test.tracking.system.password")
    String getTestTrackingSystemPassword();

    @Property("test.tracking.class")
    @DefaultValue("framework.utils.integration.resultexecutors.xray.feignapi.XrayTestExecutionImpl")
    String getClassName();

    @Property("test.status.fail")
    @DefaultValue("FAIL")
    String getStatusFail();

    @Property("test.status.pass")
    @DefaultValue("PASS")
    String getStatusPass();

    @Property("test.status.skip")
    @DefaultValue("SKIP")
    String getStatusSkip();

    static IntegrationConfig getInstance() {
        return PropertyLoader.newInstance().populate(IntegrationConfig.class);
    }
}
