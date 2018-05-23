/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Dimension;

/**
 *
 * @author Pavel
 */
public class Fisheye {
    static float d = 0.75f;         //distorsion
    static float s = 4.0f ;         // scale
    static float e = 0.5f;          // size-affecting par
    static float c = 1.5f;          // size-affecting par
    static boolean apiUsed = false; // apriori importance used for size
    static float cutoff = 0.f;      // cutoff not important nodes
    
    /**
     Compute the position on the canvas in fisheye Coordinates based on position 
     of the node, focus node and screen size
     */
    public static float[] fisheyePos(float xCurr, float yCurr, float xFoc, float yFoc, Dimension displaySize){
        float dNormX, dNormY, dMaxX, dMaxY;
        
        float[] res = {-1.0f, -1.0f};
            dMaxX = (xFoc < xCurr ) ?  displaySize.width - xFoc : xFoc;
            dNormX = Math.abs(xCurr - xFoc);
            dMaxY = (yFoc < yCurr) ? displaySize.height - yCurr : yFoc;
            dNormY = Math.abs(yCurr - yFoc);
            
        res[0] = (xCurr > xFoc) ? gFun(dNormX / dMaxX, d) * dMaxX + xFoc : 
                -gFun(dNormX / dMaxX, d) * dMaxX + xFoc;
        res[1] = (yCurr > yFoc) ? gFun(dNormY / dMaxY, d) * dMaxY + yFoc : 
                -gFun(dNormY / dMaxY, d) * dMaxY + yFoc;;
        return res;
    }
    
    private static float gFun(float x, float d){
        return (d+1)*x / (d*x + 1);
    }
    
    /**
     * Compute the node size in fisheye transformation
     */
    public static  int fisheyeSize(float xFish, float yFish, float xQ, float yQ, int api){
        int sGeom = Math.round(Math.min(Math.abs(xFish - xQ), Math.abs(yFish - yQ)));
        if (apiUsed){
            return Math.round((float) ( sGeom *  Math.pow( api * c, e)));
        } else {
            return Math.round((float) ( sGeom * Math.pow( c, e)));
        }
    }
    
    public static void setD(float dNew){
        d = dNew;
    }
    public static void setC(float cNew){
        c = cNew;
    }
    public static void setS(float sNew){
       s = sNew;
    }
    public static void setE(float eNew){
        e = eNew;
    }
    public static void setCutoff(float cut){
        cutoff = cut;
    }
    public static void setApiUsed(boolean apiUsedNew){
        apiUsed = apiUsedNew;
    }
    
    
}
