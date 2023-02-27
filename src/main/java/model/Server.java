package model;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Server implements Runnable {
    private int id;
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private volatile boolean running = false;
    private boolean runningThreads;

    public Server(int id, int numberOfClients){
        this.waitingPeriod = new AtomicInteger(0);
        this.clients = new LinkedBlockingQueue<>(numberOfClients);
        this.id = id;
    }

    public void addClient(Client client){
        this.clients.add(client);
        this.waitingPeriod.addAndGet(client.getServiceTime());
        client.setWaitingTime(this.waitingPeriod.intValue());
    }

    @Override
    public void run() throws NullPointerException {
        while(this.runningThreads){
            if(!this.clients.isEmpty()){
                // resuming thread
                this.running = true;
            }
            while(this.running && this.runningThreads){
                try{
                    // executes service as described:
                    //      each second the server processes a client until the service time is exhausted
                    //      pops the client from the queue
                    int timeToService = this.clients.peek().getServiceTime();
                    Thread.sleep(1000);
                    if (timeToService == 1) {
                        this.clients.poll();
                    } else {
                        this.clients.peek().setServiceTime(timeToService - 1);
                    }
                    this.waitingPeriod.addAndGet(-1);
                    if (this.clients.isEmpty()) {
                        // pauses thread, exits while loop
                        this.running = false;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
