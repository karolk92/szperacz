package pl.ec.kafka.szperacz;

import io.micronaut.scheduling.annotation.Scheduled;


public class Szperacz {

    @Scheduled(fixedDelay = "1s")
    public void doTheStuff() {
        System.out.println("dasfdasfd");
    }
}
