/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Pavel
 * @coathor simon
 */
public class Node {
    final int idx;
    final float x;
    final float y;
    final String tooltip;
    String shortcut= "";
    int api;
    
    float xDisp, yDisp; // coordinates on display canvas without fisheye transformation
    float xCurr, yCurr; // coordinates on display canvas after fisheye transformation
    int currSize = 5;   // current size of the node
    
    boolean highlighted = false;
    
    
    public Node(float x, float y, int idx, String tooltip){
        this.x = x;
        this.y = y;
        xCurr = x;
        yCurr = y;
        xDisp= x;
        yDisp = y;
        this.tooltip = tooltip;
        this.shortcut = tooltip.substring(0, 3);
        this.idx = idx;
    }

    @Override
    public String toString() {
        return tooltip;
    }
}
