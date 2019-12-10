package testng.listener.resultexecutors.defaultex;

import com.google.inject.Binder;
import testng.listener.interfaces.ExecutorAdapter;
import testng.listener.interfaces.JsonAdapter;

import java.util.logging.Logger;

public class EmptyExecutorAdapter implements ExecutorAdapter {

    @Override
    public void execute(JsonAdapter testExecution) {
        Logger.getGlobal().info("Empty actions for posting results");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(ExecutorAdapter.class).to(EmptyExecutorAdapter.class);
    }
}
