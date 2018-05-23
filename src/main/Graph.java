/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
    private ArrayList<Node> nodeList;               // list of graph nodes
    private ArrayList<ArrayList<Integer>> adjList;  // graph adjecency list
    
    private final float CANVAS_OFFSET = 100;            // constant
    private final float SIZE_NORMAL = 6;                // node original size
    public int highlighted = -1;                        // index of highlighted node
    public HashSet<Integer> focusSet = new HashSet<>(); // set of focused nodes indices
    public boolean focusChanged = false;               // internal bool value for triggering coordinate computation
    
    private float xCenter;  // centroid of x geo coordinates
    private float yCenter;  // centroid of y geo coordinates
    private float xRange;   // max x geo value - min x geo value
    private float yRange;   // max y geo value - min y geo value
    
    /** Paints the graph on canvas*/
    public void paint(Graphics gin, Dimension displaySize){
        Graphics2D g = (Graphics2D) gin;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int xPaint, yPaint;
        float scale = Math.min(displaySize.width / xRange, 
                 displaySize.height / yRange);
        
        // Draw nodes
        for (Node n : nodeList){
            
            /*Compute the x and y Disp coordinates*/
            xPaint = Math.round((n.x - this.xCenter) * scale + displaySize.width / 2 - n.currSize/2);
            yPaint = Math.round((n.y - this.yCenter) * scale + displaySize.height / 2 - n.currSize/2 );
            n.xDisp = xPaint;
            n.yDisp = yPaint;
            
            /*compute the transformed coordinates as average of transformations for given focuses*/
            if (!focusSet.isEmpty() && focusChanged){
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
                n.currSize = Math.round(sumSize / focusSet.size());
            } else if (focusSet.isEmpty()){
                n.xCurr = n.xDisp;
                n.yCurr = n.yDisp;
                n.currSize = (int) SIZE_NORMAL;
            }
        }
        focusChanged = false;
        
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
        /*Draw regular nodes*/
        for (Node n : nodeList){
            g.setColor(new Color(53,151,223));
            if (focusSet.contains(n.idx) || n.highlighted){
                continue;
            }
            if (n.currSize > Fisheye.cutoff){
                g.fillOval( Math.round(n.xCurr), Math.round(n.yCurr), n.currSize, n.currSize);
                drawShortcut(n, g);
            }
        }
        /*Draw focused nodes*/
        for (Integer nFoc : this.focusSet){
            Node n = nodeList.get(nFoc);
            if (n.currSize > Fisheye.cutoff){
                g.setColor(new Color(66,164,86));
                g.fillOval( Math.round(n.xCurr), Math.round(n.yCurr), n.currSize, n.currSize);
                drawShortcut(n, g);
            }
        }
        /*Draw highlighted node if any */
        if (this.highlighted != -1){
            Node n = nodeList.get(this.highlighted);
            g.setColor(new Color(225,112,81));
            g.setFont(new Font("monospaced", Font.BOLD, 20)); // monospace not necessary
            g.drawString(n.tooltip, 10,20);
            drawShortcut(n, g);
        }
    }
    
    private void drawShortcut(Node n, Graphics2D g){
        if (n.currSize > Fisheye.cutoff){
                g.fillOval( Math.round(n.xCurr), Math.round(n.yCurr), n.currSize, n.currSize);
                if (n.currSize > 25){
                    g.setColor(Color.black);
                    g.setFont(new Font("monospaced", Font.PLAIN, 12)); // monospace not necessary
                    FontMetrics fontMetrics = g.getFontMetrics();
                    g.drawString(n.shortcut, n.xCurr + n.currSize/2 - fontMetrics.stringWidth(n.shortcut)/2, 
                            n.yCurr + n.currSize/2 + fontMetrics.getHeight()/4);
                }
            }
    }
    /** Transform node according to given focusIdx index*/
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
    
    /** sets this.highlighted according to closes node to x and y coordinates 
      returns false if nearest node is already highlighted*/
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
    
    /** Handles clicking mouse near a node */
    public void handleFocusClick(){
        if (this.focusSet.contains(this.highlighted)){
            this.focusSet.remove(this.highlighted);
        } else {
            this.focusSet.add(this.highlighted);
        }
            focusChanged = true;
    }
    
    /**Adds focus to focusSet*/
    public void addFocus(int nodeIdx){
        this.focusSet.add(nodeIdx);
        focusChanged = true;
    }
    
    /** Removes focus from focusSet*/
    public void removeFocus(int nodeIdx){
        this.focusSet.remove(nodeIdx);
        focusChanged = true;
    }
    /** Removes all focused nodes*/
    public void removeFocusAll(){
        this.focusSet.clear();
        focusChanged = true;
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

                    if (Integer.parseInt(eElement.getAttribute("id")) != i){
                        throw new Exception("Wrong format...");
                    }
                    NodeList data = eElement.getElementsByTagName("data");

                    if ("x".equals(data.item(0).getAttributes().getNamedItem("key").getTextContent())){
                        x = Float.parseFloat(data.item(0).getTextContent());
                        if (x < xMin) xMin = x;
                        if (x > xMax) xMax = x;
                        xCum += x;
                    } else {
                        throw new Exception("Wrong format");
                    }
                    if ("tooltip".equals(data.item(1).getAttributes().getNamedItem("key").getTextContent())){
                        tooltip = data.item(1).getTextContent();
                    } else {
                        throw new Exception("Wrong format");
                    }
                    if ("y".equals(data.item(2).getAttributes().getNamedItem("key").getTextContent())){
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
                    src = Integer.parseInt(currNode.getAttributes().getNamedItem("source").getTextContent());
                    tgt = Integer.parseInt(currNode.getAttributes().getNamedItem("target").getTextContent());
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
