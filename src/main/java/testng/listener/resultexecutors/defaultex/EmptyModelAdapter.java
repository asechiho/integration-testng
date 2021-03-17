package testng.listener.resultexecutors.defaultex;

import com.google.inject.Binder;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.ModelAdapter;
import testng.listener.models.TestResults;

public class EmptyModelAdapter implements ModelAdapter {

    /**
     * Contributes bindings and other configurations for this module to {@code binder}.
     *
     * @param binder: binder for guice
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(ModelAdapter.class).to(EmptyModelAdapter.class);
    }

    @Override
    public JsonAdapter getResult(TestResults results) {
        return () -> "";
    }
}
