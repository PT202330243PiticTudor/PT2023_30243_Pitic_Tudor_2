package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client implements Comparable<Client> {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private int waitingTime;

    public Client(int id, int arrivalTime, int serviceTime){
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.waitingTime = 0;
    }

    public int compareArrivalTime(Client client){
        return (this.arrivalTime - client.getArrivalTime());
    }

    // we use the arrivalTime strategy
    @Override
    public int compareTo(Client o) {
        return (this.arrivalTime - o.getArrivalTime());
    }
}
