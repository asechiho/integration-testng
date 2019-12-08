package testng.listener.interfaces;

import com.google.inject.Module;
import testng.listener.models.TestResults;

public interface ModelAdapter extends Module {
    JsonAdapter getResult(TestResults results);
}
