package testng.listener.listeners;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.testng.IModuleFactory;
import org.testng.ITestContext;
import org.testng.internal.ClassHelper;
import testng.listener.interfaces.IntegrationConfig;
import testng.listener.resultexecutors.EmptyResultExecutor;

public class ListenerInjectorFactory implements IModuleFactory {

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
    public com.google.inject.Module createModule(ITestContext context, Class<?> testClass) {
        return getModule();
    }

    synchronized private static Module getModule() {
        if (inject == null) {
            inject = Modules.override(new EmptyResultExecutor()).with(initModule());
        }
        return inject;
    }

    /**
     * @return instance of root module, defined by sysproperty "test.rootModule"
     */
    private static Module initModule() {
        try {
            if (!IntegrationConfig.getInstance().isTestTrackingUse()) {
                return new EmptyResultExecutor();
            }
            Class<?> moduleClass = Class.forName(IntegrationConfig.getInstance().getClassName());
            return (Module) ClassHelper.newInstance(moduleClass);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to initialize Guice module", e);
        }
    }
}
