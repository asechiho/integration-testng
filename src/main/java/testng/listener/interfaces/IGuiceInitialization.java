package testng.listener.interfaces;

public interface IGuiceInitialization {
    PostResult getExecutorAdapter();
    TestTrackingModelAdapter getModelAdapter();
}
