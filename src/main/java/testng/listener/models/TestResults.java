package testng.listener.models;

import org.testng.ITestResult;
import testng.listener.annotations.TestKey;
import testng.listener.config.IntegrationConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public final class TestResults {

    private final TestKey testKey;
    private final Set<ITestResult> testResultSet;
    private final Status status;
    private static final List<String> THROWABLE_PREFIXES;

    public TestResults(TestKey testKey, Set<ITestResult> testResultSet) {
        this.testKey = testKey;
        this.testResultSet = testResultSet;
        this.status = getStatus(testResultSet);
    }

    public TestKey getTestKey() {
        return testKey;
    }

    public Set<ITestResult> getTestResultSet() {
        return testResultSet;
    }

    public Status getStatus() {
        return status;
    }

    public String getAllMessage(String delimiter) {
        Set<Throwable> throwables = getTestResultSet().parallelStream()
                .filter(result -> result.getStatus() != ITestResult.SUCCESS)
                .map(ITestResult::getThrowable)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return throwables.isEmpty() ? "" : throwables.stream()
                .map(Throwable::getMessage)
                .map(this::removePrefixes)
                .collect(Collectors.joining(delimiter));
    }

    private Status getStatus(Set<ITestResult> results) {
        int passCount = results.parallelStream()
                .mapToInt(mapToIntByTestStatus(ITestResult.SUCCESS))
                .sum();
        int failCount = results.parallelStream()
                .mapToInt(mapToIntByTestStatus(ITestResult.FAILURE))
                .sum();
        int skippCount = results.parallelStream()
                .mapToInt(mapToIntByTestStatus(ITestResult.SKIP))
                .sum();
        Status result = passCount > 0 ? Status.PASS : null;
        result = (failCount > skippCount && failCount > 0) ? Status.FAIL : result;
        result = (skippCount > failCount && skippCount > 0) ? Status.SKIPP : result;
        return result;
    }

    private ToIntFunction<ITestResult> mapToIntByTestStatus(int status) {
        return result -> result.getStatus() == status ? 1 : 0;
    }

    private String removePrefixes(String message) {
        return THROWABLE_PREFIXES
                .parallelStream()
                .map(prefix -> message.replace(prefix, ""))
                .collect(Collectors.joining());
    }

    static {
        THROWABLE_PREFIXES = Arrays.stream(IntegrationConfig.getInstance().getThrowablePrefixes().split(",")).collect(Collectors.toList());
    }
}
