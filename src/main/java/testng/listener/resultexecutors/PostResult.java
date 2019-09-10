package testng.listener.resultexecutors;

import com.google.inject.Module;
import testng.listener.interfaces.JsonAdapter;

public interface PostResult extends Module {
    void post(JsonAdapter testExecution);
}
