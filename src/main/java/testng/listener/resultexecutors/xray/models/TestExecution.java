package testng.listener.resultexecutors.xray.models;

import com.google.gson.GsonBuilder;
import testng.listener.gsonadapters.OffsetDateTimeAdapter;
import testng.listener.interfaces.JsonAdapter;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestExecution implements JsonAdapter {

    @Nullable
    private final String testExecutionKey;

    @Nullable
    private Info info;

    @Nullable
    private List<TestInfo> tests;

    public TestExecution(@Nullable String testExecutionKey, @Nullable Info info, @Nullable List<TestInfo> tests) {
        this.testExecutionKey = testExecutionKey;
        this.info = info;
        this.tests = tests;
    }

    public TestExecution withTest(TestInfo test) {
        if (this.tests == null) {
            this.tests = new ArrayList<>();
        }
        this.tests.add(test);
        return this;
    }

    @Override
    public String toJson() {
        return new GsonBuilder().registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).create()
                .toJson(this, TestExecution.class);
    }

    @Nullable
    public String getTestExecutionKey() {
        return testExecutionKey;
    }

    @Nullable
    public Info getInfo() {
        return info;
    }

    @Nullable
    public List<TestInfo> getTests() {
        return tests;
    }
}
