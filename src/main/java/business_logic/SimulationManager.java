package business_logic;

import business_logic.comparators.IdComparator;
import gui.View;
import lombok.Getter;
import lombok.Setter;
import model.Client;
import model.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class SimulationManager implements Runnable{

    private int timeLimit;
    private int minProcessingTime;
    private int maxProcessingTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers;
    private int numberOfClients;

    private float avgServiceTime;

    private Scheduler scheduler;
    private List<Client> generatedClients;

    private String outputFileName;
    private View view;

    public SimulationManager(int[] inputArray, String outputFileName, View view){
        this.numberOfClients = inputArray[0];
        this.numberOfServers = inputArray[1];
        this.timeLimit = inputArray[2];
        this.minArrivalTime = inputArray[3];
        this.maxArrivalTime = inputArray[4];
        this.minProcessingTime = inputArray[5];
        this.maxProcessingTime = inputArray[6];
        this.outputFileName = outputFileName;
        this.view = view;

        generateClients();
        this.scheduler = new Scheduler(this.numberOfServers, this.numberOfClients);
        calculateAverageServiceTime((ArrayList<Client>) getGeneratedClients());
    }

    public void generateClients() {
        List<Client> clients = new ArrayList<Client>();
        Random random = new Random();
        for(int id = 0; id < this.numberOfClients; id++){
            clients.add(
                    new Client(
                            id + 1,
                            random.nextInt(this.maxArrivalTime - this.minArrivalTime) + this.minArrivalTime,
                            random.nextInt(this.maxProcessingTime - this.minProcessingTime) + this.minProcessingTime
                    )
            );
        }

        Collections.sort(clients);
        setGeneratedClients(clients);
    }

    private void outputToFile(List<Server> servers, List<Client> clients, int currentTime) throws IOException {
        FileWriter fw = new FileWriter(this.outputFileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        out.println("Time: " + currentTime);
        out.append("Waiting List: ");
        for (Client client : clients){
            out.write("(" + client.getId() + "," + client.getArrivalTime() + "," + client.getServiceTime() + ") ");
        }
        out.append('\n');

        this.scheduler.getServers().sort(new IdComparator());
        for (Server server : servers){
            out.append("Queue ").append(String.valueOf(server.getId())).append(":");
            if (server.getClients().isEmpty()) {
                out.append(" closed\n");
            }else {
                for (Client client: server.getClients()) {
                    out.append("(").append(String.valueOf(client.getId())).append(",").append(String.valueOf(client.getArrivalTime())).append(",").append(String.valueOf(client.getServiceTime())).append(") ");
                }
                out.append("\n");
            }
        }
        out.append('\n');
        out.close();
    }

    private boolean checkRunning() {
        boolean flag = false;
        for (Server server: this.scheduler.getServers()) {
            if(!server.getClients().isEmpty()) {
                flag = true;
            }
        }
        if (!this.generatedClients.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    private void calculateAverageServiceTime(ArrayList<Client> clients){
        int totalServiceTime = 0;
        for (Client client: clients) {
            totalServiceTime += client.getServiceTime();
        }
        this.setAvgServiceTime((float)totalServiceTime/(float)this.numberOfClients);
    }

    private void printAverageWaitingTime(ArrayList<Client> clients) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(this.outputFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println("Average Waiting Time: ");
            int totalWaitingTime = 0;
            for (Client client: clients) {
                totalWaitingTime += client.getWaitingTime();
            }
            out.println((float)totalWaitingTime/(float)this.numberOfClients);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printAverageServiceTime(){
        FileWriter fw = null;
        try {
            fw = new FileWriter(this.outputFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println("Average Service Time: ");
            out.println(this.avgServiceTime);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printPeakHour(int peakTime) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(this.outputFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println("Peak Hour: ");
            out.println(peakTime);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int peakHour(int maxQueue){
        int serverSize = 0;
        for(Server server : this.scheduler.getServers()){
            serverSize = server.getClients().size();
            if (serverSize > maxQueue) {
                maxQueue = serverSize;
            }
        }
        return maxQueue;
    }

    @Override
    public void run() {
        // "special military operation" clock
        int currentTime = 0;

        // queue for processed clients; used for average time calculation
        ArrayList<Client> removed = new ArrayList<>();
        boolean running = true;
        int maxServerSize = 0;
        int currentServerSize;
        int peakTime = 0;

        while(running) {
            try {
                FileReader reader = new FileReader(outputFileName);
                BufferedReader buff = new BufferedReader(reader);
                ArrayList<Client> toRemove = new ArrayList<>();

                // loop in all clients generated
                for (Client client : this.generatedClients){
                    if(client.getArrivalTime() == currentTime) {
                        this.scheduler.assignClient(client);
                        toRemove.add(client);
                        removed.add(client);
                    }
                }

                // each second it checks which clients it needs to remove from waiting list
                this.generatedClients.removeAll(toRemove);

                // update out.txt file
                outputToFile(this.scheduler.getServers(), this.generatedClients, currentTime);

                // writes to text area from out.txt file
                this.view.getLiveArea().read(buff, null);
                System.out.println("Time : " + currentTime);

                // peak hour implemented
                currentServerSize = peakHour(maxServerSize);
                if(maxServerSize < currentServerSize){
                    maxServerSize = currentServerSize;
                    peakTime = currentTime;
                }
                // next second
                currentTime++;
                Thread.sleep(1000);
                if ((currentTime == this.timeLimit) || !checkRunning()) {
                    running = false;
                    // in plus
                    this.scheduler.setRunningThreads(false);
                    printAverageWaitingTime(removed);
                    printAverageServiceTime();
                    printPeakHour(peakTime);
//                    this.view.getLiveArea().read(buff, null);
                }

            }catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
//        this.scheduler.setRunningThreads(false);
//        printAverageWaitingTime(removed);
    }
}
