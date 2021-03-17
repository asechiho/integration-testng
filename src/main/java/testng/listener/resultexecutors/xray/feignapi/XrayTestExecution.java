package testng.listener.resultexecutors.xray.feignapi;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface XrayTestExecution{

    @RequestLine("POST {baseApiPath}/import/execution")
    @Headers("Content-Type: application/json")
    @Body("{testExecution}")
    void updateTestExecution(@Param("baseApiPath") String baseApiPath, @Param("testExecution") String testExecution);
}
