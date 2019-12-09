package testng.listener.listeners;

import com.google.inject.Inject;
import org.testng.*;
import org.testng.annotations.Guice;
import org.testng.collections.Maps;
import org.testng.collections.Sets;
import testng.listener.annotations.TestKey;
import testng.listener.config.IntegrationConfig;
import testng.listener.interfaces.ExecutorAdapter;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.ModelAdapter;
import testng.listener.models.TestResults;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Guice(moduleFactory = ListenerInjectorFactory.class)
public class PostListener implements ITestNGListener, ITestNGListenerFactory, ITestListener, IClassListener {

    private static final IntegrationConfig INTEGRATION_CONFIG = IntegrationConfig.getInstance();
    private static final Map<TestKey, Set<ITestResult>> TEST_RESULTS = Maps.newLinkedHashMap();

    @Inject
    private ExecutorAdapter executorAdapter;
    @Inject
    private ModelAdapter modelAdapter;

    public PostListener() {
        ListenerInjectorFactory.getInjector().injectMembers(this);
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        TestKey testKey = getAnnotation(testClass);
        if (!isKeyEmpty(testKey)) {
            synchronized (TEST_RESULTS) {
                TEST_RESULTS.put(testKey, new HashSet<>());
            }
        }
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        postTestMethodsWithTestKey(testClass);
        TestKey testKey = getAnnotation(testClass);
        if (!isKeyEmpty(testKey)) {
            synchronized (TEST_RESULTS) {
                if (TEST_RESULTS.containsKey(testKey)) {
                    post(getResult(new TestResults(testKey, TEST_RESULTS.get(testKey))));
                }
            }
        }
    }

    private boolean isKeyEmpty(TestKey testKey) {
        return testKey == null || testKey.key().isEmpty();
    }

    private void postTestMethodsWithTestKey(ITestClass testClass) {
        Set<ITestNGMethod> pushMethods = Arrays.stream(testClass.getTestMethods())
                .filter(test -> test.getConstructorOrMethod().getMethod().isAnnotationPresent(TestKey.class))
                .filter(test -> isKeyEmpty(test.getConstructorOrMethod().getMethod().getAnnotation(TestKey.class)))
                .collect(Collectors.toSet());
        pushMethods.forEach(method -> {
            TestKey key = method.getConstructorOrMethod().getMethod().getAnnotation(TestKey.class);
            synchronized (TEST_RESULTS) {
                post(getResult(new TestResults(key, TEST_RESULTS.get(key))));
            }
        });
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        addResult(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        addResult(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        addResult(tr);
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

    private void post(JsonAdapter adapter) {
        if (adapter != null) {
            Logger.getGlobal().info("Model: " + adapter.toJson());
            this.executorAdapter.execute(adapter);
        }
    }

    public static IntegrationConfig getIntegrationConfig() {
        return INTEGRATION_CONFIG;
    }

    private JsonAdapter getResult(TestResults results) {
        return modelAdapter.getResult(results);
    }

    private Predicate<? super ITestResult> predicateToEqualITestResult(ITestResult tr) {
        return iTestResult -> Objects.deepEquals(iTestResult.getParameters(), tr.getParameters());
    }

    private void addResult(ITestResult tr) {
        TestKey testKey = getAnnotation(tr);
        if (!isKeyEmpty(testKey)) {
            addResultToSet(testKey, tr);
        }
    }

    private void addResultToSet(TestKey key, ITestResult tr) {
        synchronized (TEST_RESULTS) {
            if (TEST_RESULTS.containsKey(key)) {
                Set<ITestResult> removeResults = TEST_RESULTS.get(key).stream()
                        .filter(predicateToEqualITestResult(tr))
                        .collect(Collectors.toSet());
                TEST_RESULTS.get(key).removeAll(removeResults);
                TEST_RESULTS.get(key).add(tr);
            } else {
                TEST_RESULTS.put(key, Sets.newHashSet(tr));
            }
        }
    }

    private TestKey getAnnotation(ITestResult tr) {
        TestKey key = tr.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestKey.class);
        return key == null ? getAnnotation(tr.getTestClass()) : key;
    }

    private TestKey getAnnotation(IClass testClass) {
        return testClass.getRealClass().getAnnotation(TestKey.class);
    }
}
