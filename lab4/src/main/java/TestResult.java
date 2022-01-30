package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestResult {
    private final String status;
    private final String testName;
    private final String expectedResult;
    private final String receivedREsult;

    @JsonCreator
    public TestResult(@JsonProperty("status") String status,
                      @JsonProperty("testName") String testName,
                      @JsonProperty("expectedResult") String expectedResult,
                      @JsonProperty("receivedResult") String receivedREsult) {
        this.status = status;
        this.testName = testName;
        this.expectedResult = expectedResult;
        this.receivedREsult = receivedREsult;
    }

    public String getStatus() {
        return status;
    }

    public String getTestName() {
        return testName;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getReceivedREsult() {
        return receivedREsult;
    }

 @Override
 public String toString() {
        return "Status: " + status + "\n"
                + "Test name: " + testName + "\n"
                + "Expected: " + expectedResult + "\n"
                + "Received: " + receivedREsult + "\n";
 }

}
