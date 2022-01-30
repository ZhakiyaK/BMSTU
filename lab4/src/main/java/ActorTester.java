package main.java;

public class ActorTester extends AbstractActor {
    private static final String SCRIPT_ENGINE_NAME = "nashorn";
    private static final String TEST_PASSED_STATTUS = "PASSED";
    private static final String TEST_FAILED_STATUS = "FAILED";
    private static final String TEST_CRASHED_STATUS = "CRASHED";
    private static final String EMPTY_STRING = "";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActorRouter.MessageTest.class, m -> sender().tell(runTest(m), self() ) )
                .build();
    }

    private String execJS(String jsScript)
}
