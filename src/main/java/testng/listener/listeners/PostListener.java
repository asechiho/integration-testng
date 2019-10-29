package testng.listener.listeners;

import com.google.inject.Inject;
import org.testng.*;
import org.testng.annotations.Guice;
import testng.listener.config.IntegrationConfig;
import testng.listener.interfaces.ExecutorAdapter;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.ModelAdapter;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Guice(moduleFactory = ListenerInjectorFactory.class)
public class PostListener extends TestListenerAdapter implements ITestNGListenerFactory, IClassListener {

    private static final IntegrationConfig INTEGRATION_CONFIG = IntegrationConfig.getInstance();

    @Inject
    private ExecutorAdapter actions;
    @Inject
    private ModelAdapter listenerAdapter;

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
    public void onTestSuccess(ITestResult tr) {
        updateAfterRetry(tr);
        super.onTestSuccess(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        updateAfterRetry(tr);
        super.onTestFailure(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        updateAfterRetry(tr);
        super.onTestSkipped(tr);
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

    private Status getTestStatus(ITestNGMethod testNGMethod) {
        Status status = isFailedTest(testNGMethod) ?
                new Status(INTEGRATION_CONFIG.getStatusFail(), getTestThrowable(getFailedTests(), testNGMethod)) :
                new Status(INTEGRATION_CONFIG.getStatusPass(), null);
        status = isSkippTest(testNGMethod) ?
                new Status(INTEGRATION_CONFIG.getStatusSkip(), getTestThrowable(getSkippedTests(), testNGMethod)) : status;
        return status;
    }

    private Status getClassStatus(ITestClass iTestClass) {
        List<ITestNGMethod> noAnnotatedMethods = Arrays.stream(iTestClass.getTestMethods())
                .filter(mthd -> !listenerAdapter.isTestPush(mthd))
                .collect(Collectors.toList());
        Status status = noAnnotatedMethods.stream().anyMatch(this::isFailedTest) ?
                new Status(INTEGRATION_CONFIG.getStatusFail(), getClassThrowable(getFailedTests(), noAnnotatedMethods)) :
                new Status(INTEGRATION_CONFIG.getStatusPass(), null);
        status = noAnnotatedMethods.stream().anyMatch(this::isSkippTest) ?
                new Status(INTEGRATION_CONFIG.getStatusSkip(), getClassThrowable(getSkippedTests(), noAnnotatedMethods)) : status;
        return status;
    }

    private boolean isFailedTest(ITestNGMethod method) {
        return getFailedTests().stream().anyMatch(getTestContainsPredicate(method));
    }

    private List<Throwable> getTestThrowable(Collection<ITestResult> testResults, ITestNGMethod method) {
        return testResults.stream()
                .distinct()
                .filter(getTestContainsPredicate(method))
                .map(ITestResult::getThrowable)
                .collect(Collectors.toList());
    }

    private List<Throwable> getClassThrowable(Collection<ITestResult> testResults, List<ITestNGMethod> methods) {
        List<Throwable> throwable = new ArrayList<>();
        methods.stream()
                .distinct()
                .map(method -> getTestThrowable(testResults, method))
                .forEach(throwable::addAll);
        return throwable;
    }

    private boolean isSkippTest(ITestNGMethod method) {
        return getSkippedTests().stream().anyMatch(getTestContainsPredicate(method));
    }

    private Predicate<? super ITestResult> getTestContainsPredicate(ITestNGMethod containsMethod) {
        return (Predicate<ITestResult>) testNGMethod -> testNGMethod.getMethod().getMethodName().equalsIgnoreCase(containsMethod.getMethodName());
    }

    private synchronized void post(JsonAdapter adapter) {
        if (adapter != null) {
            Logger.getGlobal().info("Model: " + adapter.toJson());
            this.actions.execute(adapter);
        }
    }

    public static IntegrationConfig getIntegrationConfig() {
        return INTEGRATION_CONFIG;
    }

    private JsonAdapter getResultFromMethod(ITestNGMethod iTestNGMethod) {
        return listenerAdapter.getResultFromMethod(iTestNGMethod, getTestStatus(iTestNGMethod));
    }

    private JsonAdapter getResultFromClass(ITestClass iTestClass) {
        return listenerAdapter.getResultFromClass(iTestClass, getClassStatus(iTestClass));
    }

    private void updateAfterRetry(ITestResult tr) {
        setSkippedTests(removeTestWithStatusByFilter(getSkippedTests(), predicateToEqualITestResult(tr)));
        setFailedTests(removeTestWithStatusByFilter(getFailedTests(), predicateToEqualITestResult(tr)));
    }

    private Predicate<? super ITestResult> predicateToEqualITestResult(ITestResult tr) {
        return iTestResult -> iTestResult.getName().equalsIgnoreCase(tr.getName()) &&
                iTestResult.getMethod().equals(tr.getMethod()) &&
                Objects.deepEquals(iTestResult.getParameters(), tr.getParameters());
    }

    private synchronized List<ITestResult> removeTestWithStatusByFilter(List<ITestResult> results, Predicate<? super ITestResult> filter) {
        List<ITestResult> res = results.stream()
                .filter(filter)
                .collect(Collectors.toList());
        results.removeAll(res);
        return results;
    }
}
