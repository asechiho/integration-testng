package testng.listener.listeners;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.testng.IModuleFactory;
import org.testng.ITestContext;
import org.testng.internal.ClassHelper;
import testng.listener.interfaces.IntegrationConfig;
import testng.listener.resultexecutors.defaultex.EmptyResultAdapter;
import testng.listener.resultexecutors.defaultex.EmptyResultExecutor;

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
            inject = Modules.override(new EmptyResultExecutor(), new EmptyResultAdapter()).with(initExecutorModule(), initAdapterModule());
        }
        return inject;
    }

    /**
     * @return instance of root module, defined by sysproperty "test.tracking.class"
     */
    private static Module initExecutorModule() {
        return initModule(new EmptyResultExecutor(), IntegrationConfig.getInstance().getExecutorClassName());
    }

    /**
     * @return instance of root module, defined by sysproperty "test.tracking.model.adapter.class"
     */
    private static Module initAdapterModule() {
        return initModule(new EmptyResultAdapter(), IntegrationConfig.getInstance().getAdapterClassName());
    }

    /**
     * @return instance of a module
     */
    private static Module initModule(Module module, String className) {
        try {
            if (!IntegrationConfig.getInstance().isTestTrackingUse()) {
                return module;
            }
            Class<?> moduleClass = Class.forName(className);
            return (Module) ClassHelper.newInstance(moduleClass);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to initialize Guice module", e);
        }
    }
}
