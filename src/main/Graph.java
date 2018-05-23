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
import java.util.HashSet;
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
    private float SIZE_NORMAL = 4;
    public int highlighted = -1;
    public int focusIdx = -1;
    public HashSet<Integer> focusSet = new HashSet<>();
    
    private float xCenter;  // centroid of x geo coordinates
    private float yCenter;  // centroid of y geo coordinates
    private float xRange;   // max x geo value - min x geo value
    private float yRange;   // max y geo value - min y geo value
    
    
    public void paint(Graphics gin, Dimension displaySize){
        Graphics2D g = (Graphics2D) gin;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
        
        int xPaint, yPaint;
        float scale = Math.min(displaySize.width / xRange, 
                 displaySize.height / yRange);
        
        // Draw nodes
        for (Node n : nodeList){
            
            /*Compute the x and y Disp coordinates*/
            xPaint = Math.round((n.x - this.xCenter) * scale + displaySize.width / 2 - SIZE_NORMAL/2);
            yPaint = Math.round((n.y - this.yCenter) * scale + displaySize.height / 2 - SIZE_NORMAL/2 );
            n.xDisp = xPaint;
            n.yDisp = yPaint;
            
            /*compute the transformed coordinates*/
            if (!focusSet.isEmpty()){
                float sumX = 0.f; 
                float sumY = 0.f;
                float sumSize = 0.f;
                
                for (Integer focIdx : focusSet){
                    float [] fTrans = fisheyeTransform(n, focIdx, displaySize);
                    int fSize = Math.round(fTrans[2]);
                    sumX += fTrans[0];
                    sumY += fTrans[1];
                    sumSize += fSize;
                }
                n.xCurr = sumX / focusSet.size();
                n.yCurr = sumY / focusSet.size();
                n.currSize = (int) (sumSize / focusSet.size());
                
            } else {
                n.xCurr = n.xDisp;
                n.yCurr = n.yDisp;
                n.currSize = (int) SIZE_NORMAL;
            }
        }
        
        //Draw edges
        for (int i = 0; i < this.adjList.size(); i++){
            for (Integer j : this.adjList.get(i)){
                g.setColor(new Color(0, 0,0, 25));
                if (this.focusSet.contains(j) && this.focusSet.contains(i)){
                    g.setColor(new Color(66,164,86));
                }
                if (this.highlighted == i || this.highlighted == j){
                    g.setColor(Color.BLACK); 
                }
                
                if (nodeList.get(i).currSize > Fisheye.cutoff && 
                        nodeList.get(j).currSize > Fisheye.cutoff){
                    g.drawLine(Math.round(this.nodeList.get(i).xCurr + this.nodeList.get(i).currSize/2), 
                               Math.round(this.nodeList.get(i).yCurr+ this.nodeList.get(i).currSize/2),
                               Math.round(this.nodeList.get(j).xCurr+ this.nodeList.get(j).currSize/2),
                               Math.round(this.nodeList.get(j).yCurr+ this.nodeList.get(j).currSize/2));
                }
                            
                
            }
        }
        for (Node n : nodeList){
            g.setColor(new Color(53,151,223));
            if (focusSet.contains(n.idx)){
                g.setColor(new Color(66,164,86));
            }
            if (n.highlighted){
                g.setColor(new Color(225,112,81));
                g.drawString(n.tooltip,10,20);
            }
            
            if (n.currSize > Fisheye.cutoff){
                if (focusIdx != -1){
                    g.fillOval( Math.round(n.xCurr), Math.round(n.yCurr), n.currSize, n.currSize);
                } else {
                    g.fillOval( Math.round(n.xDisp), Math.round(n.yDisp), n.currSize, n.currSize);
                }
            }
        }
        
        
    }
    
    private float[] fisheyeTransform(Node n,  int focusIdx, Dimension displaySize){
        float [] fCoordinates = Fisheye.fisheyePos(n.xDisp, n.yDisp, 
                        nodeList.get(focusIdx).xDisp,
                        nodeList.get(focusIdx).yDisp,
                        displaySize);
                float vecMag = (float) Math.hypot(n.xDisp - nodeList.get(focusIdx).xDisp,
                            n.yDisp - nodeList.get(focusIdx).yDisp);
                float [] normVec = {
                    (n.xDisp - nodeList.get(focusIdx).xDisp)/vecMag, 
                    (n.yDisp - nodeList.get(focusIdx).yDisp)/vecMag 
                };

                float [] sizePoint = {n.xDisp, n.yDisp};
                if (n.xDisp > nodeList.get(focusIdx).xDisp){
                    sizePoint[0] = sizePoint[0] + Fisheye.s * SIZE_NORMAL;
                } else {
                    sizePoint[0] = sizePoint[0] - Fisheye.s * SIZE_NORMAL;
                }
                if (n.yDisp > nodeList.get(focusIdx).yDisp){
                    sizePoint[1] = sizePoint[1] + Fisheye.s * SIZE_NORMAL;
                } else {
                    sizePoint[1] = sizePoint[1] - Fisheye.s * SIZE_NORMAL;
                }
                
                float [] qPoint = Fisheye.fisheyePos(sizePoint[0], 
                        sizePoint[1], 
                        nodeList.get(focusIdx).xDisp,
                        nodeList.get(focusIdx).yDisp,
                        displaySize);
                int fSize = Fisheye.fisheyeSize(qPoint[0], 
                        qPoint[1],
                        fCoordinates[0],
                        fCoordinates[1],
                        n.api);
                float res [] = {fCoordinates[0], fCoordinates[1], fSize};
                return res;
    }
    
    public boolean highlightNearest(int x, int y){
        Node nearest = null;
        float minDist = Float.POSITIVE_INFINITY;
        float dst;
        for  (Node n : nodeList){
            if (n.currSize > Fisheye.cutoff){
                dst = (n.xCurr - x)* (n.xCurr - x) + (n.yCurr - y)* (n.yCurr - y);
                if (dst < minDist){
                    nearest = n;
                    minDist = dst;
                }
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
        if (this.focusSet.contains(this.highlighted)){
            this.focusSet.remove(this.highlighted);
        } else {
            this.focusSet.add(this.highlighted);
        }
            focusIdx = this.highlighted;
    }
    
    public void addFocus(int nodeIdx){
        this.focusSet.add(focusIdx);
    }
    
    public void removeFocus(int nodeIdx){
        this.focusSet.remove(focusIdx);
    }
    
    public void removeFocusAll(){
        this.focusSet.clear();
    }
    
    /** 
     Imports the graph file from specified filepath
     */
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
            int [] apis = new int[nList.getLength()];
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
                    apis[src] = apis[src] + 1;
                    apis[tgt] = apis[tgt] + 1;
                }
             }
             for(int i = 0; i < apis.length; i++){
                 nodeList.get(i).api = apis[i];
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
