package testng.listener.resultexecutors.defaultex;

import com.google.inject.Binder;
import org.testng.ITestClass;
import org.testng.ITestNGMethod;
import testng.listener.interfaces.ModelAdapter;
import testng.listener.interfaces.JsonAdapter;

public class EmptyModelAdapter implements ModelAdapter {

    @Override
    public JsonAdapter getResultFromMethod(ITestNGMethod iTestNGMethod, String status) {
        return null;
    }

    @Override
    public JsonAdapter getResultFromClass(ITestClass iTestClass, String status) {
        return null;
    }

    /**
     * Contributes bindings and other configurations for this module to {@code binder}.
     *
     * @param binder: binder for guice
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(ModelAdapter.class).to(EmptyModelAdapter.class);
    }
}
