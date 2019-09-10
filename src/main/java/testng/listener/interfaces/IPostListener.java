package testng.listener.interfaces;

import io.qameta.allure.Link;
import org.testng.ITestClass;
import org.testng.ITestNGMethod;
import testng.listener.annotations.TestKey;

import java.lang.annotation.Annotation;

public interface IPostListener {
    JsonAdapter getResultFromMethod(ITestNGMethod iTestClass);
    JsonAdapter getResultFromClass(ITestClass iTestClass);

    default TestKey getTestKeyForMethod(ITestNGMethod testNGMethod) {
        TestKey key = testNGMethod.getConstructorOrMethod().getMethod().getAnnotation(TestKey.class);
        if (key == null) {
            Link issue = testNGMethod.getConstructorOrMethod().getMethod().getAnnotation(Link.class);
            if (issue != null) {
                return newTestKey(issue.value());
            }
        }
        return key;
    }

    default TestKey getTestKeyForClass(ITestClass testClass) {
        TestKey key = testClass.getRealClass().getAnnotation(TestKey.class);
        if (key == null) {
            Link issue = testClass.getRealClass().getAnnotation(Link.class);
            if (issue != null) {
                return newTestKey(issue.value());
            }
        }
        return key;
    }

    default TestKey newTestKey(String key) {
        return new TestKey() {
            /**
             * Returns the annotation type of this annotation.
             *
             * @return the annotation type of this annotation
             */
            @Override
            public Class<? extends Annotation> annotationType() {
                return TestKey.class;
            }

            @Override
            public String key() {
                return key;
            }
        };
    }
}
