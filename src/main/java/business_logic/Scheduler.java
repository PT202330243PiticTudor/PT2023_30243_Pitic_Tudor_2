package business_logic;

import business_logic.comparators.WaitingTimeComparator;
import lombok.Getter;
import model.Client;
import model.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Scheduler {
    private List<Server> servers;
    private int maxNrServers;

    public Scheduler(int maxNrServers, int numberOfClients) {
        this.maxNrServers = maxNrServers;
        this.servers = new ArrayList<Server>();

        // here we create each server and run each server
        for(int i = 1; i <= this.maxNrServers; i++){
            Server aux = new Server(i, numberOfClients);
            this.servers.add(aux);
            aux.setRunningThreads(true);
            Thread thread = new Thread(aux);
            thread.start();
        }
    }

    // we use shortest time strategy
    public void assignClient(Client client){
        Collections.sort(servers, new WaitingTimeComparator());
        this.servers.get(0).addClient(client);
    }

    public void setRunningThreads(boolean runningThreads){
        for (Server s : this.servers){
            s.setRunningThreads(runningThreads);
        }
    }
}
