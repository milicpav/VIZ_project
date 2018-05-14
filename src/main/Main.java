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
 */
public class Main {
    //TODO: make a simple frame for visualization based on this and coordinates of the nodes  https://docs.oracle.com/javase/tutorial/uiswing/painting/refining.html
	
	
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
//        JFrame f = new JFrame("Swing Paint Demo");
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.add(new GraphCanvas(g));
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        f.setSize(screenSize);
//
//        //f.setSize(250,250);
//        f.setVisible(true);
//
//
//        //
//        JFrame f2 = new JFrame("form demo");
//        f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f2.add(new OptionPanel(g));
//        f2.setSize(350, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
////        f2.setSize(Toolkit.getDefaultToolkit().getScreenSize());
//        f2.setVisible(true);
//        f2.setAlwaysOnTop(true);
        //
        OptionPanel op = new OptionPanel(g);
        GraphCanvas gc = new GraphCanvas(g);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(gc);
        jp.add(op);


        JFrame f3 = new JFrame("form demo");
        f3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f3.add(jp);
//        f2.setSize(350, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        f3.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        f3.setVisible(true);
//        f2.setAlwaysOnTop(true);
    } 
    
}
