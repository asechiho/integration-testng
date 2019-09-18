package testng.listener.interfaces;

import com.google.inject.Module;

public interface ExecutorAdapter extends Module {
    void execute(JsonAdapter testExecution);
}
