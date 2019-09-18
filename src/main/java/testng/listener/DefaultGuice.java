package testng.listener;

import testng.listener.interfaces.IGuiceInitialization;
import testng.listener.interfaces.ExecutorAdapter;
import testng.listener.interfaces.ModelAdapter;
import testng.listener.resultexecutors.defaultex.EmptyModelAdapter;
import testng.listener.resultexecutors.defaultex.EmptyExecutorAdapter;

public class DefaultGuice implements IGuiceInitialization {
    @Override
    public ExecutorAdapter getExecutorAdapter() {
        return new EmptyExecutorAdapter();
    }

    @Override
    public ModelAdapter getModelAdapter() {
        return new EmptyModelAdapter();
    }
}
