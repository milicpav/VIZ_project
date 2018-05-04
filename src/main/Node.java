/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Pavel
 */
public class Node {
    final float x;
    final float y;
    final String tooltip;
    
    float xCurr;
    float yCurr;
    int currSize;
    
    
    public Node(float x, float y, String tooltip){
        this.x = x;
        this.y = y;
        xCurr = x;
        yCurr = y;
        currSize = 10;
        this.tooltip = tooltip;
    }
}
