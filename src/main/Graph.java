/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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
 */
public class Graph {
    private ArrayList<Node> nodeList;
    private ArrayList<ArrayList<Integer>> adjList;
    
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
            
            double x;
            double y;
            String tooltip;
            
            for (int i = 0; i < nList.getLength(); i++){
                org.w3c.dom.Node currNode = nList.item(i);
                
                 if (currNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element eElement = (Element) currNode;
                System.out.println("Node id : " 
                  + eElement.getAttribute("id"));
               
                if (Integer.parseInt(eElement.getAttribute("id")) != i){
                    throw new Exception("Wrong format...");
                }
                NodeList data = eElement.getElementsByTagName("data");
               
                if ("x".equals(data.item(0).getAttributes().getNamedItem("key").getTextContent())){
                    //System.out.println(data.item(0).getTextContent());
                    x = Double.parseDouble(data.item(0).getTextContent());
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
                    y = Double.parseDouble(data.item(2).getTextContent());
                } else {
                    throw new Exception("Wrong format");
                }
                   
                this.nodeList.add(new Node(x, y, tooltip));
               
            }
            }   
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
    
}
