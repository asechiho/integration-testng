package testng.listener.listeners;

import java.util.List;
import java.util.stream.Collectors;

public class Status {

    private String statusValue;
    private List<Throwable> errorList;

    public Status(String statusValue, List<Throwable> errorList) {
        this.statusValue = statusValue;
        this.errorList = errorList;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public List<Throwable> getErrorList() {
        return errorList;
    }

    public String getAllMessage(String delimiter) {
        return (errorList == null || errorList.isEmpty()) ? "" : this.getErrorList().stream().map(Throwable::getMessage).collect(Collectors.joining(delimiter));
    }
}
