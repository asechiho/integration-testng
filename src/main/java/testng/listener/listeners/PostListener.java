package testng.listener.listeners;

import com.google.inject.Inject;
import org.testng.*;
import org.testng.annotations.Guice;
import testng.listener.interfaces.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Guice(moduleFactory = ListenerInjectorFactory.class)
public class PostListener extends TestListenerAdapter implements ITestNGListenerFactory, IClassListener {

    private static final TestTrackingSystemConfig TEST_TRACKING_SYSTEM_CONFIG = TestTrackingSystemConfig.getInstance();
    private static final IntegrationConfig INTEGRATION_CONFIG = IntegrationConfig.getInstance();

    @Inject
    private PostResult actions;
    @Inject
    private TestTrackingModelAdapter listenerAdapter;

    public PostListener() {
        ListenerInjectorFactory.getInjector().injectMembers(this);
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        for (ITestNGMethod method : testClass.getTestMethods()) {
            if (listenerAdapter.isTestPush(method)) {
                post(getResultFromMethod(method));
            }
        }
        post(getResultFromClass(testClass));
    }

    @Override
    public ITestNGListener createListener(Class<? extends ITestNGListener> listenerClass) {
        try {
            ITestNGListener listener = listenerClass.getConstructor().newInstance();
            ListenerInjectorFactory.getInjector().injectMembers(listener);
            return listener;
        } catch (Exception ex) {
            Logger.getGlobal().warning("Listener was not created. " + ex.getMessage());
            return null;
        }
    }

    private String getTestStatus(ITestNGMethod testNGMethod) {
        String status = isFailedTest(testNGMethod) ? INTEGRATION_CONFIG.getStatusFail() : INTEGRATION_CONFIG.getStatusPass();
        status = isSkippTest(testNGMethod) ? INTEGRATION_CONFIG.getStatusSkip() : status;
        return status;
    }

    private String getClassStatus(ITestClass iTestClass) {
        List<ITestNGMethod> noAnnotatedMethods = Arrays.stream(iTestClass.getTestMethods())
                .filter(mthd -> !listenerAdapter.isTestPush(mthd))
                .collect(Collectors.toList());
        String status = noAnnotatedMethods.stream().anyMatch(this::isFailedTest) ? INTEGRATION_CONFIG.getStatusFail() : INTEGRATION_CONFIG.getStatusPass();
        status = noAnnotatedMethods.stream().anyMatch(this::isSkippTest) ? INTEGRATION_CONFIG.getStatusSkip() : status;
        return status;
    }

    private boolean isFailedTest(ITestNGMethod method) {
        return getFailedTests().stream().anyMatch(getTestContainsPredicate(method));
    }

    private boolean isSkippTest(ITestNGMethod method) {
        return getSkippedTests().stream().anyMatch(getTestContainsPredicate(method));
    }

    private Predicate<? super ITestResult> getTestContainsPredicate(ITestNGMethod containsMethod) {
        return (Predicate<ITestResult>) testNGMethod -> testNGMethod.getMethod().getMethodName().equalsIgnoreCase(containsMethod.getMethodName());
    }

    private synchronized void post(JsonAdapter adapter) {
        if (adapter != null) {
            this.actions.post(adapter);
        }
    }

    public static TestTrackingSystemConfig getTestTrackingSystemConfig() {
        return TEST_TRACKING_SYSTEM_CONFIG;
    }

    private JsonAdapter getResultFromMethod(ITestNGMethod iTestNGMethod) {
        return listenerAdapter.getResultFromMethod(iTestNGMethod, getTestStatus(iTestNGMethod));
    }

    private JsonAdapter getResultFromClass(ITestClass iTestClass) {
        return listenerAdapter.getResultFromClass(iTestClass, getClassStatus(iTestClass));
    }
}
