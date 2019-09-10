package testng.listener.resultexecutors;

import com.google.inject.Binder;
import testng.listener.interfaces.JsonAdapter;
import testng.listener.interfaces.PostResult;

import java.util.logging.Logger;

public class EmptyResultExecutor implements PostResult {

    @Override
    public void post(JsonAdapter testExecution) {
        Logger.getGlobal().info("Empty actions for posting results");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(PostResult.class).to(EmptyResultExecutor.class);
    }
}
