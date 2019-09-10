# TestNG-test-tracking
Library for integration test results to test tracking system

Technology stack:
1. TestNG([docs](https://testng.org/doc/documentation-main.html))
2. Gson([docs](https://sites.google.com/site/gson/gson-user-guide))
3. Guice([docs](https://github.com/google/guice/wiki/GettingStarted))
4. Allure TestNG core([docs](https://docs.qameta.io/allure/)) - use Link annotation for marker test key
5. qatools/properties([docs](https://github.com/qatools/properties))

Build tool: gradle

## Getting started
Add the library to our project
```xml
<dependency>
    <groupId>testng-integration</groupId>
    <artifactId>listener</artifactId>
    <version>1.0</version>
</dependency>
```
Then add new implements for PostListener (It's an abstract class). Implement _getResultFromMethod_ and _getResultFromClass_ to create model with results
```java
public class XRayListener extends PostListener {
    @Override
    public JsonAdapter getResultFromMethod(ITestNGMethod iTestClass) {
        return TextExecution.fromMethodResult(iTestClass);
    }

    @Override
    public JsonAdapter getResultFromClass(ITestClass iTestClass) {
        return TextExecution.fromClassResult(iTestClass);
    }
}
```
Add test tracking system executor with implement PostResult interface. For example: EmptyResultExecutor (used if test.tracking.use = false)
The method 'configure' add our executor to guice injector;
```java
public class EmptyResultExecutor implements PostResult {

    @Override
    public void post(JsonAdapter testExecution) {
        Logger.getGlobal().info("Empty actions for posting results");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(PostResult.class).to(EmptyResultExecutor.class);
    }
}
```
Add a property file to the project with name: _integration.properties_.
```properties 
test.tracking.use=false
test.tracking.system=xray
test.tracking.system.login=
test.tracking.system.password=
test.tracking.class=testng.listener.resultexecutors.EmptyResultExecutor
test.status.fail=FAIL
test.status.pass=PASS
test.status.skip=NOT_TESTED
```
Then add a property file with name: <test.tracking.system>.properties.
 ```properties 
base.url=
base.api.path=
run.key=
 ```