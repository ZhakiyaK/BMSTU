package main.java;

public class TestBody {
    private final String testName;
    private final Object[] params;
    private final String expectedResult;

    @JsonCreator
    public TestBody(
            @JsonProperty("testName") String testName,
            @JsonProperty("params") Object[] params,
            @JsonProperty("expectedResult") String expectedResult) {
        this.testName = testName;
        this.params = params;
        this.expectedResult = expectedResult;
    }

    protected String getTestName() {
        return testName
    }

    protected  Object[] getParams() {
        return params;
    }

    protected String getExpectedResult() {
        return expectedResult;
    }
}
