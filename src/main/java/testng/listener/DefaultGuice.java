package testng.listener;

import testng.listener.interfaces.IGuiceInitialization;
import testng.listener.interfaces.PostResult;
import testng.listener.interfaces.TestTrackingModelAdapter;
import testng.listener.resultexecutors.defaultex.EmptyModelAdapter;
import testng.listener.resultexecutors.defaultex.EmptyExecutorAdapter;

public class DefaultGuice implements IGuiceInitialization {
    @Override
    public PostResult getExecutorAdapter() {
        return new EmptyExecutorAdapter();
    }

    @Override
    public TestTrackingModelAdapter getModelAdapter() {
        return new EmptyModelAdapter();
    }
}
