/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Pavel
 * @coathor simon
 */
public class Graph {
    private ArrayList<Node> nodeList;
    private ArrayList<ArrayList<Integer>> adjList;
    
    
    private final float CANVAS_OFFSET = 100;
    public int highlighted = -1;
    public int focusIdx = -1;
    private float xCenter;  // centroid of x geo coordinates
    private float yCenter;  // centroid of y geo coordinates
    private float xRange;   // max x geo value - min x geo value
    private float yRange;   // max y geo value - min y geo value
    
    private float[] fisheyePos(float xCurr, float yCurr, float xFoc, float yFoc, Dimension displaySize){
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
    
    private float gFun(float x, float d){
        return (d+1)*x / (d*x + 1);
    }
    
    private int fisheyeSize(float xFish, float yFish, float xQ, float yQ){
        float e = 1f;
        float c = 1f;
        int sGeom = Math.round(Math.min(Math.abs(xFish - xQ), Math.abs(yFish - yQ)));
        return Math.round((float) Math.pow(sGeom * c, e));
        
    
    }
    
    public void paint(Graphics gin, Dimension displaySize){
        Graphics2D g = (Graphics2D) gin;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
        
        int xPaint, yPaint;
        float scale = Math.min(displaySize.width / xRange, 
                 displaySize.height / yRange);
        //Draw edges
        for (int i = 0; i < this.adjList.size(); i++){
            for (Integer j : this.adjList.get(i)){
                g.setColor(new Color(0, 0,0, 25));
                if (this.highlighted == i || this.highlighted == j){
                    g.setColor(Color.BLACK); 
                }
                    g.drawLine(Math.round(this.nodeList.get(i).xCurr + this.nodeList.get(i).currSize/2), 
                               Math.round(this.nodeList.get(i).yCurr+ this.nodeList.get(i).currSize/2),
                               Math.round(this.nodeList.get(j).xCurr+ this.nodeList.get(j).currSize/2),
                               Math.round(this.nodeList.get(j).yCurr+ this.nodeList.get(j).currSize/2));
                
            }
        }
        // Draw nodes
        for (Node n : nodeList){
            float s = 3.0f * 10;
            g.setColor(new Color(255, 0,0));
            if (n.highlighted){
                g.setColor(Color.BLUE);
                g.drawString(n.tooltip,10,20);
            }
            /*Compute the x and y paint coordinates*/
            xPaint = Math.round((n.x - this.xCenter) * scale + displaySize.width / 2 - n.currSize/2);
            yPaint = Math.round((n.y - this.yCenter) * scale + displaySize.height / 2 - n.currSize/2 );
            n.xCurr = xPaint;
            n.yCurr = yPaint;
            
            /*compute the transformed coordinates*/
            if (focusIdx != -1){
                float [] fCoordinates = fisheyePos(n.xCurr, n.yCurr, 
                        nodeList.get(focusIdx).xCurr,
                        nodeList.get(focusIdx).yCurr,
                        displaySize);
                float vecMag = (float) Math.hypot(n.xCurr - nodeList.get(focusIdx).xCurr,
                            n.yCurr - nodeList.get(focusIdx).yCurr);
                float [] normVec = {
                    (n.xCurr - nodeList.get(focusIdx).xCurr)/vecMag, 
                    (n.yCurr - nodeList.get(focusIdx).yCurr)/vecMag 
                };
                /*TODO 18.05 - here should be + s * size_normal to the direction away from focus*/
                /*TODO 18.05 - name if size > certain threshold*/
                float [] sizePoint = {n.xCurr + s, n.yCurr  + s};
                float [] qPoint = fisheyePos(sizePoint[0], sizePoint[1], 
                        nodeList.get(focusIdx).xCurr,
                        nodeList.get(focusIdx).yCurr,
                        displaySize);
                int fSize = fisheyeSize(qPoint[0], qPoint[1],
                        fCoordinates[0],
                        fCoordinates[1]);
                g.fillOval( Math.round(fCoordinates[0]), Math.round(fCoordinates[1]), fSize, fSize);
            } else {
                g.fillOval( Math.round(xPaint), Math.round(yPaint), n.currSize, n.currSize);
            }
        }
        
    }
    
    public boolean highlightNearest(int x, int y){
        Node nearest = null;
        float minDist = Float.POSITIVE_INFINITY;
        float dst;
        for  (Node n : nodeList){
            dst = (n.xCurr - x)* (n.xCurr - x) + (n.yCurr - y)* (n.yCurr - y);
            if (dst < minDist){
                nearest = n;
                minDist = dst;
            }
        }
        if (nearest != null) {
            nearest.highlighted = true;
        }
        if (this.highlighted == nearest.idx){
            return false;
        } else {
            if (this.highlighted != -1){
                this.nodeList.get(this.highlighted).highlighted = false;
            }
            
            this.highlighted = nearest.idx;
            return true;
        }
    }
    
    public void focusTest(){

            focusIdx = this.highlighted;
       
        
    }
    
    
    public void importFromFile(String filepath) {
        try {
            File inputFile = new File(filepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("node");
            
            int nodeNumber = nList.getLength();
            nodeList = new ArrayList<>();
            adjList = new ArrayList<>();
            for (int i = 0; i < nodeNumber; i++){
                adjList.add(new ArrayList<>());
            }
            
            float x;
            float y;
            
            float xCum = 0.f;
            float yCum = 0.f;
            float xMin = Float.MAX_VALUE;
            float xMax = Float.NEGATIVE_INFINITY;
            float yMin = Float.MAX_VALUE;
            float yMax = Float.NEGATIVE_INFINITY;
            String tooltip;
            
            for (int i = 0; i < nList.getLength(); i++){
                org.w3c.dom.Node currNode = nList.item(i);
                
                if (currNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) currNode;
                    //System.out.println("Node id : " + eElement.getAttribute("id"));

                    if (Integer.parseInt(eElement.getAttribute("id")) != i){
                        throw new Exception("Wrong format...");
                    }
                    NodeList data = eElement.getElementsByTagName("data");

                    if ("x".equals(data.item(0).getAttributes().getNamedItem("key").getTextContent())){
                        //System.out.println(data.item(0).getTextContent());
                        x = Float.parseFloat(data.item(0).getTextContent());
                        if (x < xMin) xMin = x;
                        if (x > xMax) xMax = x;
                        xCum += x;
                    } else {
                        throw new Exception("Wrong format");
                    }
                    if ("tooltip".equals(data.item(1).getAttributes().getNamedItem("key").getTextContent())){
                        //System.out.println(data.item(1).getTextContent());
                        tooltip = data.item(1).getTextContent();
                    } else {
                        throw new Exception("Wrong format");
                    }
                    if ("y".equals(data.item(2).getAttributes().getNamedItem("key").getTextContent())){
                        //System.out.println(data.item(2).getTextContent());
                        y = Float.parseFloat(data.item(2).getTextContent());
                        if (y < yMin) yMin = y;
                        if (y > yMax) yMax = y;
                        yCum += y; 
                    } else {
                        throw new Exception("Wrong format");
                    }

                    this.nodeList.add(new Node(x, y, i, tooltip));
               
                }
            }   
            
            this.xCenter = xCum / nList.getLength();
            this.yCenter = yCum / nList.getLength();
            this.xRange = xMax - xMin + CANVAS_OFFSET;
            this.yRange = yMax - yMin + CANVAS_OFFSET;
             NodeList eList = doc.getElementsByTagName("edge");
             int src, tgt;
             for (int i = 0; i < eList.getLength(); i++){
                 org.w3c.dom.Node currNode = eList.item(i);
                 
                if (currNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) currNode;
                    //System.out.println(currNode.getAttributes().getNamedItem("id").getTextContent());
                    src = Integer.parseInt(currNode.getAttributes().getNamedItem("source").getTextContent());
                    tgt = Integer.parseInt(currNode.getAttributes().getNamedItem("target").getTextContent());
                    //System.out.println(currNode.getAttributes().getNamedItem("source").getTextContent());
                    //System.out.println(currNode.getAttributes().getNamedItem("target").getTextContent());     
                    this.adjList.get(src).add(tgt);
                }
             }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    
    }

    public Node[] getNodeList() {
        Node[] r = new Node[nodeList.size()];
        for (int i = 0; i < nodeList.size(); i++) {
            r[i] = nodeList.get(i);
        }
        return r;
    }
}
