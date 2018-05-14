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
    final int idx;
    final float x;
    final float y;
    final String tooltip;
    
    float xCurr;
    float yCurr;
    int currSize;
    
    boolean highlighted = false;
    
    
    public Node(float x, float y, int idx, String tooltip){
        this.x = x;
        this.y = y;
        xCurr = x;
        yCurr = y;
        currSize = 10;
        this.tooltip = tooltip;
        this.idx = idx;
    }

    @Override
    public String toString() {
        return tooltip;
    }
}
