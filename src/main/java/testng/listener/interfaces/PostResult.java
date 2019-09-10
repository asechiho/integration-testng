package testng.listener.interfaces;

import com.google.inject.Module;

public interface PostResult extends Module {
    void post(JsonAdapter testExecution);
}
