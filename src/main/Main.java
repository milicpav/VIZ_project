/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 *
 * @author Pavel
 */
public class Main {
    
    public static void main(String[] args) {
        Graph g = new Graph();
        g.importFromFile("airlines.graphml");
        System.out.println("x");
        
        
    }
    
}
