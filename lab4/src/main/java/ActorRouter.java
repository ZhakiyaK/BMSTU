package main.java;

import ActorKeeper.ActorKeeper;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.List;

public class ActorRouter extends AbstractActor {
    private static final int TESTERS_AMOUNT = 5;

    private final ActorRef keeper;
    private final Router router;

    {
        keeper = getContext().actorOf(Props.create(ActorKeeper.class));
        List<Routee> routees = new ArrayList<>();
        for (int i=0; i < TESTERS_AMOUNT; i++) {
            ActorRef r = getContext().actorOf(Props.create(ActorTester.class));
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }

        router = new Router(new RoundRobinRountingLogic(), routees);

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        JSTestApp.MessageTestPackage.class,
                        message -> {
                            String packageID = message.getPackageID();
                            String jsScript = message.getJSScript();
                            String funcName = message.getFuncName();

                            for(TestBody test: message.getTests()) {
                                router.route(new MessageTest(packageID,jsScript,funcName,test), keeper);
                            }
                        }
                )
                .match(
                        JSTestApp.MessageGetTestPackageResult.class,
                        message -> keeper.tell(message, sender())
                )
                .build();
    }

    static class MessageTest {
        private final String packageID;
        private final String jsScript;
        private final String funcName;
        private final List<TestBody> test;

        public MessageTest(@JsonProperty("packageId") String packageID,
                           @JsonProperty("jsScript") String jsScript,
                           @JsonProperty("funcName") String funcName,
                           @JsonProperty("test") List<TestBody> test) {
            this.packageID = packageID;
            this.jsScript = jsScript;
            this.funcName = funcName;
            this.test = test;
        }


        protected String getPackageID() {
            return PackageID;
        }

        protected String getJsScript() {
            return JsScript;
        }

        protected String getFuncName() {
            return funcName;
        }

        protected List<TestBody> get() {
            return test;
        }

    }

}
