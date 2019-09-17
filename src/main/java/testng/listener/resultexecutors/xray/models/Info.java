package testng.listener.resultexecutors.xray.models;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

public class Info {

    @Nonnull
    private String summary;

    @Nullable
    private String description;

    @Nullable
    private String version;

    @Nullable
    private String user;

    @Nullable
    private OffsetDateTime startDate;

    @Nullable
    private OffsetDateTime finishDate;

    @Nullable
    private String testPlanKey;

    @Nullable
    private String testEnvironments;

    public Info(@Nonnull String summary, @Nullable String description, @Nullable String version, @Nullable String user, @Nullable OffsetDateTime startDate,
                @Nullable OffsetDateTime finishDate, @Nullable String testPlanKey, @Nullable String testEnvironments) {
        this.summary = summary;
        this.description = description;
        this.version = version;
        this.user = user;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.testPlanKey = testPlanKey;
        this.testEnvironments = testEnvironments;
    }

    @Nonnull
    public String getSummary() {
        return summary;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    @Nullable
    public String getUser() {
        return user;
    }

    @Nullable
    public OffsetDateTime getStartDate() {
        return startDate;
    }

    @Nullable
    public OffsetDateTime getFinishDate() {
        return finishDate;
    }

    @Nullable
    public String getTestPlanKey() {
        return testPlanKey;
    }

    @Nullable
    public String getTestEnvironments() {
        return testEnvironments;
    }
}
