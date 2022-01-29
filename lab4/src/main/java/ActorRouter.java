package main.java;

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
                                router.route(new MessageTest(packageID,jsScript,funcName,test))
                            }
                        }
                )
    }

}
