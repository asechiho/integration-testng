package testng.listener.listeners;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.testng.IModuleFactory;
import org.testng.ITestContext;
import org.testng.internal.ClassHelper;
import org.testng.internal.InstanceCreator;
import testng.listener.DefaultGuice;
import testng.listener.annotations.GuiceInitialization;
import testng.listener.exceptions.ClassPathException;
import testng.listener.exceptions.InjectionClassException;
import testng.listener.interfaces.IGuiceInitialization;
import testng.listener.config.IntegrationConfig;
import testng.listener.resultexecutors.defaultex.EmptyExecutorAdapter;
import testng.listener.resultexecutors.defaultex.EmptyModelAdapter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

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
            return !IntegrationConfig.getInstance().isTestTrackingUse() ? new DefaultGuice()
                    : (IGuiceInitialization) InstanceCreator.newInstance(getGuiceInitializationClass());
        } catch (MalformedURLException e) {
            throw new ClassPathException(e.getMessage());
        }
    }

    private static Class<?> getGuiceInitializationClass() throws MalformedURLException {
        URL resource = ListenerInjectorFactory.class.getClassLoader().getResource("");

        if (resource == null) {
            throw new MalformedURLException("Resource path not found");
        }

        ConfigurationBuilder builder = new ConfigurationBuilder().setUrls(new File(resource.getPath()).getParentFile().toURI().toURL());
        Set<Class<?>> classInfoSet = new Reflections(builder).getTypesAnnotatedWith(GuiceInitialization.class);
        if (classInfoSet.size() != 1) {
            throw new InjectionClassException(String.format("Found more 1 class with annotation: {%s}", IGuiceInitialization.class.getName()));
        }
        return classInfoSet.iterator().next();
    }

}
