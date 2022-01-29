package main.java;

import java.beans.PropertyChangeSupport;
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
            ActorRef r = getContext().actorOf(PropertyChangeSupport)
        }

    }
}
