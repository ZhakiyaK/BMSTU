package main.java;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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

    private String execJS(String jscript, String functionName, Object[] params) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(SCRIPT_ENGINE_NAME);
        engine.eval(jscript);
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction(functionName,params).toString();
    }

    private MessageTestResult runTest(ActorRouter.MessageTest message) {
        String received;
        String status;
        String expected = message.getTests().getExpectedResult();
        try {
            received = execJS(
                    message.getJsScript(),
                    message.getFuncName(),
                    message.getTests().getParams()
            );
            
        }
    }
}
