package testng.listener.resultexecutors.xray.models;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

@SuppressWarnings("CanBeFinal")
public class TestInfo {

    @Nonnull
    private String testKey;

    @Nullable
    private OffsetDateTime start;

    @Nullable
    private OffsetDateTime finish;

    @Nullable
    private String comment;

    @Nonnull
    private String status;

    public TestInfo(@Nonnull String testKey, @Nullable OffsetDateTime start, @Nullable OffsetDateTime finish, @Nullable String comment,
                    @Nonnull String status) {
        this.testKey = testKey;
        this.start = start;
        this.finish = finish;
        this.comment = comment;
        this.status = status;
    }

    @Nonnull
    public String getTestKey() {
        return testKey;
    }

    @Nullable
    public OffsetDateTime getStart() {
        return start;
    }

    @Nullable
    public OffsetDateTime getFinish() {
        return finish;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    @Nonnull
    public String getStatus() {
        return status;
    }
}
