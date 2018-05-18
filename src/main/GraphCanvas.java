/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.Graphics; 
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Pavel
 */
public class GraphCanvas extends JPanel {
    
    RedSquare redSquare = new RedSquare();
    
    Graph graph;
    
    public GraphCanvas(Graph graph) {        
        this.graph = graph;
        /*addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                moveSquare(e.getX(),e.getY());
            }
        });
*/
        addMouseMotionListener(new MouseAdapter(){
            public void mouseMoved(MouseEvent e){
                highlightNearest(e.getX(), e.getY());

            }
        });
        
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                System.out.println("fuckk");
                focusTest();
            }
        });
}   
    public void focusTest(){   
        this.graph.focusTest();
        this.repaint();
        
    }
    public void highlightNearest(int x, int y){   
        if (this.graph.highlightNearest(x, y)){
            this.repaint();
        }
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);       
        graph.paint(g, this.getSize());
        //redSquare.paintSquare(g);
    }  

    
}
