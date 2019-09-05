package testng.listener.interfaces;

import ru.qatools.properties.Property;
import ru.qatools.properties.PropertyLoader;
import ru.qatools.properties.utils.PropertiesUtils;

public interface TestTrackingSystemConfig {

    @Property("base.url")
    String getBaseUrl();

    @Property("base.api.path")
    String getBaseApiPath();

    @Property("login")
    String getLogin();

    @Property("password")
    String getPassword();

    @Property("run.key")
    String getRunKey();

    static TestTrackingSystemConfig getInstance() {
        String propertyFileName = String.format("%s.properties", IntegrationConfig.getInstance().getTestTrackingSystem());
        return PropertyLoader.newInstance()
                .withPropertyProvider((classLoader, beanClass) -> PropertiesUtils.readProperties(TestTrackingSystemConfig.class.getClassLoader().getResourceAsStream(propertyFileName)))
                .populate(TestTrackingSystemConfig.class);
    }
}
