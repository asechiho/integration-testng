package testng.listener.listeners;

import com.google.common.reflect.ClassPath;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.testng.IModuleFactory;
import org.testng.ITestContext;
import org.testng.internal.ClassHelper;
import testng.listener.DefaultGuice;
import testng.listener.annotations.GuiceInitialization;
import testng.listener.exceptions.InjectionClassException;
import testng.listener.interfaces.IGuiceInitialization;
import testng.listener.interfaces.IntegrationConfig;
import testng.listener.resultexecutors.defaultex.EmptyModelAdapter;
import testng.listener.resultexecutors.defaultex.EmptyExecutorAdapter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

class ListenerInjectorFactory implements IModuleFactory {

    private static Module inject;

    static Injector getInjector() {
        return Guice.createInjector(getModule());
    }

    /**
     * @param context   The current test context
     * @param testClass The test class
     * @return The Guice module that should be used to get an instance of this
     * test class.
     */
    @Override
    public Module createModule(ITestContext context, Class<?> testClass) {
        return getModule();
    }

    synchronized private static Module getModule() {
        if (inject == null) {
            inject = Modules.override(new EmptyExecutorAdapter(), new EmptyModelAdapter()).with(initExecutorAdapterModule(), initModelAdapterModule());
        }
        return inject;
    }

    /**
     * @return instance of root module, defined by sysproperty "test.tracking.class"
     */
    private static Module initExecutorAdapterModule() {
        return iniGuiceInitialization().getExecutorAdapter();
    }

    /**
     * @return instance of root module, defined by sysproperty "test.tracking.model.adapter.class"
     */
    private static Module initModelAdapterModule() {
        return iniGuiceInitialization().getModelAdapter();
    }

    /**
     * @return instance of a module
     */
    private static IGuiceInitialization iniGuiceInitialization() {
        try {
            if (!IntegrationConfig.getInstance().isTestTrackingUse()) {
                return new DefaultGuice();
            }
            return (IGuiceInitialization) ClassHelper.newInstance(getGuiceInitializationClass().load());
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize Guice module", e);
        }
    }

    @SuppressWarnings("all")
    private static ClassPath.ClassInfo getGuiceInitializationClass() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassPath classPath = ClassPath.from(classLoader);
        Set<ClassPath.ClassInfo> classes = classPath.getAllClasses();

        Set<ClassPath.ClassInfo> classesWithAnnotation = classes.stream()
                .filter(classInfo -> classInfo.load().isAnnotationPresent(GuiceInitialization.class))
                .collect(Collectors.toSet());
        if (classesWithAnnotation.size() != 1) {
            throw new InjectionClassException(String.format("Found more 1 class with annotation: {%s}", IGuiceInitialization.class.getName()));
        }
        return classesWithAnnotation.iterator().next();
    }
}
