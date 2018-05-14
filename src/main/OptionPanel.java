package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OptionPanel extends JPanel {
    JButton showButton;
    JButton clearButton;
    JLabel infoLabel;
    JList<Node> nodeJList;
    Graph graph;
    JScrollPane listScroller;
    JTextArea selectedNodesInfo;
    JScrollPane textAreaScroller;
    GraphCanvas graphCanvas;

    // TODO: 14. 5. 2018 - kdyz se vybere vic nodu, tak vlevo nahore info je necitelne
    // TODO: 14. 5. 2018 - nezobrazuji se s vybranymi nody prilehle hrany, je to  dobre/spatne?
    public OptionPanel(Graph graph) {
        this.graph = graph;
        createUIElements();

        //clears text area
        clearButton.addActionListener(e -> {
            selectedNodesInfo.setText("");
            for (Node n :
                    graph.getNodeList()) {
                n.highlighted = false;
            }
        });

        //shows selected nodes
        showButton.addActionListener(e -> {

            for (Node n :
                    graph.getNodeList()) {
                n.highlighted = false;
            }
            List<Node> nodes = nodeJList.getSelectedValuesList();
            selectedNodesInfo.setText("");
            for (Node n :
                    nodes) {
                selectedNodesInfo.append(n.toString() + "\n");
                n.highlighted = true;
            }
            graphCanvas.repaint();
        });
        //show selected node from graphCanvas
        graphCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                selectedNodesInfo.setText("");
                for (Node n :
                        graph.getNodeList()) {
                    if (n.highlighted){
                        selectedNodesInfo.append(n.toString() + "\n");
                    }
                }
            }

//            @Override
//            public void mouseMoved(MouseEvent e) {
//                for (Node n :
//                        graph.getNodeList()) {
//                    if (n.highlighted){
//                        infoLabel.setText(n.toString());
//                    }
//                }
//            }
        });
    }

    public void createUIElements(){
        //show button - higlights selected nodes on map and shows info
        showButton = new JButton("Select");
        showButton.setName("showButton");
        //clear button
        clearButton = new JButton("Clear Selected");
        //shows info about nodes which are hoovered over
        infoLabel = new JLabel();
        infoLabel.setName("infoLabel");
        infoLabel.setText("Options");
        //lists all nodes
        nodeJList = new JList<>(graph.getNodeList());
        nodeJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        nodeJList.setLayoutOrientation(JList.VERTICAL);
        nodeJList.setVisibleRowCount(15);
        listScroller = new JScrollPane(nodeJList);
//        listScroller.setMaximumSize(new Dimension(this.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height / 4));
//        listScroller.setSize(new Dimension(this.getWidth()-2, 80));
        //shows info about selected nodes
        selectedNodesInfo = new JTextArea();
        selectedNodesInfo.setEditable(false);
        textAreaScroller = new JScrollPane(selectedNodesInfo);
        //graph canvas
        graphCanvas = new GraphCanvas(graph);
        //tmp jpanel
        JPanel tmpJP = new JPanel();
        tmpJP.setBorder(BorderFactory.createLineBorder(Color.black));


        //layout
        tmpJP.setLayout(new BoxLayout(tmpJP, BoxLayout.Y_AXIS));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        listScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        showButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        textAreaScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        tmpJP.add(infoLabel);
        tmpJP.add(listScroller);
        tmpJP.add(showButton);
        tmpJP.add(textAreaScroller);
        tmpJP.add(clearButton);
//        this.add(infoLabel);
//        this.add(listScroller);
//        this.add(showButton);
//        this.add(textAreaScroller);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        tmpJP.setMaximumSize(new Dimension((Toolkit.getDefaultToolkit().getScreenSize().width /3), Toolkit.getDefaultToolkit().getScreenSize().height));
        this.add(graphCanvas);
        this.add(tmpJP);


//        this.add(graphPanel);
//        this.add(optionsPanel);
    }

}
