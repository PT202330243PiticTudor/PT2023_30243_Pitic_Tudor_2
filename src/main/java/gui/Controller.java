package gui;

import business_logic.SimulationManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

public class Controller {
    private final View theView;
    public Controller(View theView){
        this.theView = theView;
        this.theView.addStartButtonListener(new StartListener());
    }

    class StartListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                int[] inputArray = new int[7];
                String[] parts;

                inputArray[0] = Integer.parseInt(theView.getClientsNumber());
                inputArray[1] = Integer.parseInt(theView.getQueuesNumber());
                inputArray[2] = Integer.parseInt(theView.getSimTimeNumber());
                parts = theView.getArrivalTimeInterval().split(",",2);
                inputArray[3] = Integer.parseInt(parts[0]);
                inputArray[4] = Integer.parseInt(parts[1]);
                parts = theView.getServiceTimeInterval().split(",", 2);
                inputArray[5] = Integer.parseInt(parts[0]);
                inputArray[6] = Integer.parseInt(parts[1]);

                PrintWriter pw = new PrintWriter("out.txt");
                pw.close();
                SimulationManager sm = new SimulationManager(inputArray, "out.txt", theView);
                Thread t = new Thread(sm);
                t.start();
            }catch (Exception x){
                x.printStackTrace();
            }

        }
    }
}
