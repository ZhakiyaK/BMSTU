
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ActorRouter extends AbstractActor {
    private static final int TESTERS_AMOUNT = 5;

    private final ActorRef keeper;
    private final Router router;

    {
        keeper = getContext().actorOf(Props.create(main.java.ActorKeeper.class));
        List<Routee> routees = new ArrayList<>();
        for (int i=0; i < TESTERS_AMOUNT; i++) {
            ActorRef r = getContext().actorOf(Props.create(main.java.ActorTester.class));
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }

        router = new Router(new RoundRobinRoutingLogic(), routees);

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(main.java.JSTestApp.MessageTestPackage.class,
                        message -> {
                            String packageID = message.getPackageID();
                            String jsScript = message.getJSScript();
                            String funcName = message.getFuncName();

                            for(main.java.TestBody test: message.getTests()) {
                                router.route(new MessageTest(packageID,jsScript,funcName,test), keeper);
                            }
                        }
                )
                .match(
                        main.java.JSTestApp.MessageGetTestPackageResult.class,
                        message -> keeper.tell(message, sender())
                )
                .build();
    }

    static class MessageTest {
        private final String packageID;
        private final String jsScript;
        private final String funcName;
        private final List<main.java.TestBody> test;

        @JsonCreator
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

    private static class TestBody {
    }
}
