package testng.listener.resultexecutors.xray;

import com.google.inject.Binder;
import testng.listener.models.TestResults;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.ModelAdapter;
import testng.listener.resultexecutors.xray.models.TestExecution;
import testng.listener.resultexecutors.xray.models.TestInfo;

import java.util.ArrayList;

import static testng.listener.listeners.PostListener.getIntegrationConfig;

public class XRayModelAdapter implements ModelAdapter {

    /**
     * Contributes bindings and other configurations for this module to {@code binder}.
     *
     * @param binder: binder for guice
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(ModelAdapter.class).to(XRayModelAdapter.class);
    }

    @Override
    public JsonAdapter getResult(TestResults results) {
        return new TestExecution(getIntegrationConfig().getRunKey(), null, new ArrayList<>())
                .withTest(new TestInfo(results.getTestKey().key(),null, null, results.getAllMessage("\\n"),
                        results.getStatus().getStatus()));
    }
}
