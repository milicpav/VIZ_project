package main;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OptionPanel extends JPanel {
    JButton showButton;
    JLabel infoLabel;
    JList<Node> nodeJList;
    Graph graph;
    JScrollPane listScroller;
    JTextArea selectedNodesInfo;
    JScrollPane textAreaScroller;
    GraphCanvas graphCanvas;

    public OptionPanel(Graph graph) {
        this.graph = graph;
        createUIElements();
        showButton.addActionListener(e -> {

            infoLabel.setText("funguju");
            List<Node> nodes = nodeJList.getSelectedValuesList();
            selectedNodesInfo.setText("");
            for (Node n :
                    nodes) {
                selectedNodesInfo.append(n.toString() + "\n");
//                n.highlighted = true;
            }
        });

//        nodeJList.addListSelectionListener(e -> {
//            int start = e.getFirstIndex();
//            int end = e.getLastIndex();
//            nodeJList.getSelectedValuesList()
//
//        });
    }

    public void createUIElements(){
        //show button - higlights selected nodes on map and shows info
        showButton = new JButton("Show");
        showButton.setName("showButton");
        //shows info about nodes which are hoovered over
        infoLabel = new JLabel();
        infoLabel.setName("infoLabel");
        infoLabel.setText("text");
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


        //layout
        tmpJP.setLayout(new BoxLayout(tmpJP, BoxLayout.Y_AXIS));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        listScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        showButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        textAreaScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        tmpJP.add(infoLabel);
        tmpJP.add(listScroller);
        tmpJP.add(showButton);
        tmpJP.add(textAreaScroller);
//        this.add(infoLabel);
//        this.add(listScroller);
//        this.add(showButton);
//        this.add(textAreaScroller);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(graphCanvas);
        this.add(tmpJP);


//        this.add(graphPanel);
//        this.add(optionsPanel);
    }

}
