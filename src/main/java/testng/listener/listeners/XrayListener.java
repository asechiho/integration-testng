package testng.listener.listeners;

import org.testng.ITestClass;
import org.testng.ITestNGMethod;
import testng.listener.annotations.TestKey;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.resultexecutors.xray.models.TestExecution;
import testng.listener.resultexecutors.xray.models.TestInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class XrayListener extends PostListener {

    @Override
    public synchronized JsonAdapter getResultFromMethod(ITestNGMethod testNGMethod) {
        TestKey key = getTestKeyForMethod(testNGMethod);
        if (key == null) {
            return null;
        }
        String status = getTestStatus(testNGMethod);
        return getModel(status,
                new TestExecution(getTestTrackingSystemConfig().getRunKey(), null, new ArrayList<>())
                        .withTest(new TestInfo(key.key(), null, null, "", status))
        );
    }

    @Override
    public synchronized JsonAdapter getResultFromClass(ITestClass iTestClass) {
        if (Arrays.stream(iTestClass.getTestMethods())
                .anyMatch(method -> !isTestPush(method))) {
            TestKey key = getTestKeyForClass(iTestClass);
            if (key == null) {
                return null;
            }
            String status = getClassStatus(iTestClass);
            return getModel(status,
                    new TestExecution(getTestTrackingSystemConfig().getRunKey(), null, new ArrayList<>())
                            .withTest(new TestInfo(key.key(), null, null, "", status))
            );
        }
        return null;
    }

    private synchronized JsonAdapter getModel(String status, JsonAdapter adapter) {
        return status.equalsIgnoreCase(getIntegrationConfig().getStatusPass()) ? adapter : null;
    }
}
