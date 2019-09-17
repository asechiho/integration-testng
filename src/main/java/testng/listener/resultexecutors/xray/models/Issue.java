package testng.listener.resultexecutors.xray.models;

import com.google.gson.annotations.SerializedName;

public class Issue {

    private String key;

    @SerializedName("fields.summary")
    private String summary;

    public String getKey() {
        return key;
    }

    public String getSummary() {
        return summary;
    }
}
