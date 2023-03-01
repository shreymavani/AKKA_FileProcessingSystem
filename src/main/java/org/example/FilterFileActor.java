package org.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class FilterFileActor extends AbstractBehavior<String> {

    private final ActorRef<String> putFileActorRef;

    public FilterFileActor(ActorContext<String> context, ActorRef<String> putFileActorRef) {
        super(context);
        this.putFileActorRef = putFileActorRef;
    }

    public static Behavior<String> create(ActorRef<String> putFileActorRef) {
        return Behaviors.setup(context -> new FilterFileActor(context, putFileActorRef));
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessage(String.class, this::onMessage)
                .build();
    }

    private Behavior<String> onMessage(String data) {
        // Filter the data as required and send it to PutFileActor
        putFileActorRef.tell(data);
        return this;
    }
}
