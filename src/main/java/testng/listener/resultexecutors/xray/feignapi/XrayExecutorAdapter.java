package testng.listener.resultexecutors.xray.feignapi;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import okhttp3.Credentials;
import testng.listener.FeignDriver;
import testng.listener.interfaces.IntegrationConfig;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.PostResult;
import testng.listener.interfaces.TestTrackingSystemConfig;

import java.util.concurrent.atomic.AtomicReference;

public class XrayExecutorAdapter implements PostResult {

    private static final TestTrackingSystemConfig CONFIG = TestTrackingSystemConfig.getInstance();
    private static final IntegrationConfig INTEGRATION_CONFIG = IntegrationConfig.getInstance();
    private static AtomicReference<XrayTestExecution> xRayTestExecution = new AtomicReference<>();

    public static XrayTestExecution getInstance() {
        if (xRayTestExecution.get() == null) {
            xRayTestExecution.set(FeignDriver.getInstance(CONFIG.getBaseUrl(), XrayTestExecution.class));
        }
        return xRayTestExecution.get();
    }

    @Override
    public void post(JsonAdapter testExecution) {
        getInstance().updateTestExecution(
                Credentials.basic(INTEGRATION_CONFIG.getTestTrackingSystemLogin(), INTEGRATION_CONFIG.getTestTrackingSystemPassword()),
                CONFIG.getBaseApiPath(),
                testExecution.toJson());
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(PostResult.class).to(XrayExecutorAdapter.class).in(Scopes.SINGLETON);
    }
}
