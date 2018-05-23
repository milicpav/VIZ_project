/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.*;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.RenderingHints;
/**
 *
 * @author Pavel
 * @author kadlesim
 */
public class Main {
	
    public static void main(String[] args) {
        Graph g = new Graph();
        g.importFromFile("airlines.graphml");
        System.out.println("x");
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                createAndShowGUI(g);
            }
        });
        
    }
    
    private static void createAndShowGUI(Graph g) {
        System.out.println("Created GUI on EDT? "+
        SwingUtilities.isEventDispatchThread());

        JFrame frame = new JFrame("Fish Eye - Visualization Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new OptionPanel(g));
        frame.setSize(1300,700);
//        f2.setExtendedState(JFrame.MAXIMIZED_BOTH);//fullscreen
        frame.setVisible(true);
    }
    
}
