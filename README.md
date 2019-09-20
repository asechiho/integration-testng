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
**First**: add the library to our project. <br>
**Second**: add test tracking system executor with implement _**ExecutorAdapter**_ interface. For example: EmptyResultExecutor (used if test.tracking.use
 = false)
The method 'configure' add our executor to guice injector;
```java
public class EmptyExecutorAdapter implements ExecutorAdapter {

    @Override
    public void post(JsonAdapter testExecution) {
        Logger.getGlobal().info("Empty actions for posting results");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(ExecutorAdapter.class).to(EmptyResultExecutor.class);
    }
}
```
**Then**: add model adapter for create test model with implement _**ModelAdapter**_ interface. For example: EmptyResultAdapter (used if test.tracking.use
 = false)
```java
public class EmptyModelAdapter implements ModelAdapter {

    @Override
    public JsonAdapter getResultFromMethod(ITestNGMethod iTestNGMethod, String status) {
        return null;
    }

    @Override
    public JsonAdapter getResultFromClass(ITestClass iTestClass, String status) {
        return null;
    }

    /**
     * Contributes bindings and other configurations for this module to {@code binder}.
     *
     * @param binder: binder for guice
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(ModelAdapter.class).to(EmptyResultAdapter.class);
    }
}
```
**Finally**: create guice initialization class. Can be only one class with the _**@GuiceInitialization**_ mark.
```java
@GuiceInitialization
public class DefaultGuice implements IGuiceInitialization {
    @Override
    public PostResult getExecutorAdapter() {
        return new EmptyExecutorAdapter();
    }

    @Override
    public TestTrackingModelAdapter getModelAdapter() {
        return new EmptyModelAdapter();
    }
}
```
**In Additional**: add a property file to the project with name: _**integration.properties**_.
```properties 
test.tracking.use=false
test.tracking.system.login=
test.tracking.system.password=
test.status.fail=FAIL
test.status.pass=PASS
test.status.skip=NOT_TESTED
test.tracking.system.base.url=
test.tracking.system.base.api.path=
test.tracking.system.run.key=
 ```