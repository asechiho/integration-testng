package testng.listener.resultexecutors.xray;

import com.google.inject.Binder;
import org.testng.ITestClass;
import org.testng.ITestNGMethod;
import testng.listener.annotations.TestKey;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.ModelAdapter;
import testng.listener.resultexecutors.xray.models.TestExecution;
import testng.listener.resultexecutors.xray.models.TestInfo;

import java.util.ArrayList;
import java.util.Arrays;

import static testng.listener.listeners.PostListener.getIntegrationConfig;

public class XRayModelAdapter implements ModelAdapter {

    @Override
    public synchronized JsonAdapter getResultFromMethod(ITestNGMethod iTestNGMethod, String status) {
        TestKey key = getTestKeyForMethod(iTestNGMethod);
        if (key == null) {
            return null;
        }
        return new TestExecution(getIntegrationConfig().getRunKey(), null, new ArrayList<>())
                        .withTest(new TestInfo(key.key(), null, null, "", status));
    }

    @Override
    public synchronized JsonAdapter getResultFromClass(ITestClass iTestClass, String status) {
        if (Arrays.stream(iTestClass.getTestMethods())
                .anyMatch(method -> !isTestPush(method))) {
            TestKey key = getTestKeyForClass(iTestClass);
            if (key == null) {
                return null;
            }
            return new TestExecution(getIntegrationConfig().getRunKey(), null, new ArrayList<>())
                            .withTest(new TestInfo(key.key(), null, null, "", status));
        }
        return null;
    }


    /**
     * Contributes bindings and other configurations for this module to {@code binder}.
     *
     * @param binder: binder for guice
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(ModelAdapter.class).to(XRayModelAdapter.class);
    }
}
