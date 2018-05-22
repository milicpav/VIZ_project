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
    static float d = 0.75f;
    static float s = 4.0f ;
    
    public static float[] fisheyePos(float xCurr, float yCurr, float xFoc, float yFoc, Dimension displaySize){
        float d = 0.75f;
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
    
    public static  int fisheyeSize(float xFish, float yFish, float xQ, float yQ){
        float e = 1f;
        float c = 1f;
        int sGeom = Math.round(Math.min(Math.abs(xFish - xQ), Math.abs(yFish - yQ)));
        return Math.round((float) Math.pow(sGeom * c, e));
    }
}
