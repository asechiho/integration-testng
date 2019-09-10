package testng.listener.listeners;

import com.google.inject.Inject;
import io.qameta.allure.Link;
import org.testng.*;
import org.testng.annotations.Guice;
import testng.listener.annotations.TestKey;
import testng.listener.interfaces.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Guice(moduleFactory = ListenerInjectorFactory.class)
public abstract class PostListener extends TestListenerAdapter implements ITestNGListenerFactory, IPostListener, IClassListener {

    private static final TestTrackingSystemConfig TEST_TRACKING_SYSTEM_CONFIG = TestTrackingSystemConfig.getInstance();
    private static final IntegrationConfig INTEGRATION_CONFIG = IntegrationConfig.getInstance();

    @Inject
    private PostResult actions;

    public PostListener() {
        ListenerInjectorFactory.getInjector().injectMembers(this);
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        for (ITestNGMethod method : testClass.getTestMethods()) {
            if (isTestPush(method)) {
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

    protected String getTestStatus(ITestNGMethod testNGMethod) {
        String status = isFailedTest(testNGMethod) ? INTEGRATION_CONFIG.getStatusFail() : INTEGRATION_CONFIG.getStatusPass();
        status = isSkippTest(testNGMethod) ? INTEGRATION_CONFIG.getStatusSkip() : status;
        return status;
    }

    protected String getClassStatus(ITestClass iTestClass) {
        List<ITestNGMethod> noAnnotatedMethods = Arrays.stream(iTestClass.getTestMethods())
                .filter(mthd -> !isTestPush(mthd))
                .collect(Collectors.toList());
        String status = noAnnotatedMethods.stream().anyMatch(this::isFailedTest) ? INTEGRATION_CONFIG.getStatusFail() : INTEGRATION_CONFIG.getStatusPass();
        status = noAnnotatedMethods.stream().anyMatch(this::isSkippTest) ? INTEGRATION_CONFIG.getStatusSkip() : status;
        return status;
    }

    protected boolean isTestPush(ITestNGMethod method) {
        return method.getConstructorOrMethod().getMethod().isAnnotationPresent(Link.class) ||
                method.getConstructorOrMethod().getMethod().isAnnotationPresent(TestKey.class);
    }

    protected boolean isFailedTest(ITestNGMethod method) {
        return getFailedTests().stream().anyMatch(getTestContainsPredicate(method));
    }

    protected boolean isSkippTest(ITestNGMethod method) {
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

    public static IntegrationConfig getIntegrationConfig() {
        return INTEGRATION_CONFIG;
    }
}
