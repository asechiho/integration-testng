package testng.listener.resultexecutors.xray.feignapi;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import okhttp3.Credentials;
import testng.listener.FeignDriver;
import testng.listener.config.IntegrationConfig;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.ExecutorAdapter;

import java.util.concurrent.atomic.AtomicReference;

public class XRayExecutorAdapter implements ExecutorAdapter {

    private static final IntegrationConfig INTEGRATION_CONFIG = IntegrationConfig.getInstance();
    private static AtomicReference<XrayTestExecution> xRayTestExecution = new AtomicReference<>();

    public static XrayTestExecution getInstance() {
        if (xRayTestExecution.get() == null) {
            xRayTestExecution.set(FeignDriver.getInstance(INTEGRATION_CONFIG.getBaseUrl(), XrayTestExecution.class));
        }
        return xRayTestExecution.get();
    }

    @Override
    public void execute(JsonAdapter testExecution) {
        getInstance().updateTestExecution(
                Credentials.basic(INTEGRATION_CONFIG.getTestTrackingSystemLogin(), INTEGRATION_CONFIG.getTestTrackingSystemPassword()),
                INTEGRATION_CONFIG.getBaseApiPath(),
                testExecution.toJson());
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(ExecutorAdapter.class).to(XRayExecutorAdapter.class).in(Scopes.SINGLETON);
    }
}
