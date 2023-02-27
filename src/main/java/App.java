import gui.Controller;
import gui.View;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            try {
                View theView = new View();
                theView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                new Controller(theView);
                theView.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
