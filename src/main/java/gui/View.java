package gui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class View extends JFrame{
    private JPanel panel;
    private JTextField clientsField;
    private JTextField serversField;
    private JTextField simTimeField;
    private JTextField arrivalTimeField;
    private JTextArea liveArea;
    private JTextField serviceTimeInterval;
    private JButton startButton;
    private JTextArea infoArea;

    public View() {
        setSize(1000, 500);
        add(panel);
    }

    public void addStartButtonListener(ActionListener listenForStartButton){
        startButton.addActionListener(listenForStartButton);
    }

    public String getClientsNumber(){
        return clientsField.getText();
    }

    public String getQueuesNumber(){
        return serversField.getText();
    }

    public String getSimTimeNumber(){
        return simTimeField.getText();
    }

    public String getArrivalTimeInterval(){
        return arrivalTimeField.getText();
    }

    public String getServiceTimeInterval(){
        return serviceTimeInterval.getText();
    }

    public JTextArea getLiveArea(){
        return this.liveArea;
    }
}
