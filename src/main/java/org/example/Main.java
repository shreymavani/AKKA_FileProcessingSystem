package org.example;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import org.example.filterFileActor.FilterFileActor;
import org.example.getFileActor.GetFileActor;
import org.example.putFileActor.PutFileActor;


public class Main {

    public static void main(String[] args) {
        ActorSystem<String> system = ActorSystem.create(Behaviors.empty(), "FileProcessingSystem");

        ActorRef<String> putFileActorRef = system
                .systemActorOf(PutFileActor.create("/Users/smavani/INPUT_OUTPUT_FOR_TESTING/OUTPUT/output"), "putFileActor",Props.empty());

        ActorRef<String> filterFileActorRef = system
                .systemActorOf(FilterFileActor.create(putFileActorRef), "filterFileActor",Props.empty());

        ActorRef<String> getFileActorRef = system
                .systemActorOf(GetFileActor.create(filterFileActorRef), "getFileActor",Props.empty());

        getFileActorRef.tell("/Users/smavani/INPUT_OUTPUT_FOR_TESTING/INPUT");
    }
}

