package org.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetFileActor extends AbstractBehavior<String> {

    private final ActorRef<String> filterFileActorRef;

    public GetFileActor(ActorContext<String> context, ActorRef<String> filterFileActorRef) {
        super(context);
        this.filterFileActorRef = filterFileActorRef;
    }

    public static Behavior<String> create(ActorRef<String> filterFileActorRef) {
        return Behaviors.setup(context -> new GetFileActor(context, filterFileActorRef));
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessage(String.class, this::onMessage)
                .build();
    }

    private Behavior<String> onMessage(String directoryPath) throws Exception {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            Path path = Paths.get(file.getAbsolutePath());
            String data = Files.readString(path);
            filterFileActorRef.tell(data);
        }
        return this;
    }
}
