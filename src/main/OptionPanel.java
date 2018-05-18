package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class OptionPanel extends JPanel {
//    JButton showButton;
    JButton addButton;
    JButton removeButton;
    JButton clearButton;
    JLabel infoLabel;
    JList<Node> nodeJList, selectedNodesJList;
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

        //clears textArea/jlist
        clearButton.addActionListener(e -> {
//            selectedNodesInfo.setText("");
            selectedNodesJList.setListData(new Node[0]);
            for (Node n :
                    graph.getNodeList()) {
                n.highlighted = false;
            }
            graphCanvas.repaint();
        });

        //removes selected
        removeButton.addActionListener(e -> {

//            List<Node> toRemove = selectedNodesJList.getSelectedValuesList();
            Node nodeToRemove = selectedNodesJList.getSelectedValue();
            int oldNodes = selectedNodesJList.getModel().getSize();
            Node[] tmp = new Node[oldNodes - 1];
            Node[] tmp2 = new Node[oldNodes];
            int index = 0;
            for (int i = 0; i < oldNodes; i++, index++) {
                tmp2[i] = selectedNodesJList.getModel().getElementAt(i);
            }

            List<Node> result = new LinkedList<Node>();
            for(Node item : tmp2) {
                if (item.idx != nodeToRemove.idx) {
                    result.add(item);
                }
            }
            result.toArray(tmp);
            selectedNodesJList.setListData(tmp);

            for(Node item : graph.getNodeList()) {
                if (item.idx == nodeToRemove.idx) {
                    item.highlighted = false;
                }
            }
            graphCanvas.repaint();
        });

        //adds selected nodes to textarea/jlist
        addButton.addActionListener(e -> {

            //funguje, ale neni to "pekne" + je to pomale
            int oldNodes = selectedNodesJList.getModel().getSize();
            List<Node> newNodes = nodeJList.getSelectedValuesList();
            Node[] tmp = new Node[newNodes.size() + oldNodes];
//            selectedNodesInfo.setText("");
            int index = 0;
            for (int i = 0; i < oldNodes; i++, index++) {
                tmp[i] = selectedNodesJList.getModel().getElementAt(i);
            }

            for (int i = index, j = 0; j < newNodes.size(); i++, j++) {
                if (!newNodes.get(j).highlighted){
//                    selectedNodesInfo.append(n.toString() + "\n");
                    tmp[i] = newNodes.get(j);
                    newNodes.get(j).highlighted = true;
                }
            }

            selectedNodesJList.setListData(tmp);
            graphCanvas.repaint();
        });

        //add selected node from graphCanvas
        graphCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                selectedNodesInfo.setText("");

                int oldNodes = selectedNodesJList.getModel().getSize();
                Node[] tmp = new Node[1 + oldNodes];
                int index = 0;
                for (int i = 0; i < oldNodes; i++, index++) {
                    tmp[i] = selectedNodesJList.getModel().getElementAt(i);
                }
                for (Node n :
                        graph.getNodeList()) {
                    if (n.highlighted && !contains(tmp,n)){
//                        selectedNodesInfo.append(n.toString() + "\n");
                        tmp[tmp.length-1] = n;
                        break;
                    }
                }
                selectedNodesJList.setListData(tmp);
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
//        showButton = new JButton("Select");
//        showButton.setName("showButton");
        addButton = new JButton("ADD");
        addButton.setToolTipText("Add selected nodes");
        removeButton = new JButton("REMOVE");
        removeButton.setToolTipText("Remove selected nodes");
        //clear button
        clearButton = new JButton("Clear All");
        //shows info about nodes which are hoovered over
        infoLabel = new JLabel();
        infoLabel.setText("List of all airports to select from");
        //lists all nodes
        nodeJList = new JList<>(graph.getNodeList());
        nodeJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        nodeJList.setLayoutOrientation(JList.VERTICAL);
        nodeJList.setVisibleRowCount(15);
        listScroller = new JScrollPane(nodeJList);
        selectedNodesJList = new JList<>();
        selectedNodesJList.setLayoutOrientation(JList.VERTICAL);
        selectedNodesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        /*selectedNodesJList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
                JLabel label = (JLabel)super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
                label.setForeground( Color.RED );
                return label;
            }
        });*/
//        selectedNodesJList.setSelectionInterval(-1,-1);
//        selectedNodesJList.setEnabled(false);
        textAreaScroller = new JScrollPane(selectedNodesJList/*selectedNodesInfo*/);
        //graph canvas
        graphCanvas = new GraphCanvas(graph);
        //tmp jpanel
        JPanel tmpJP = new JPanel();
        tmpJP.setBorder(BorderFactory.createLineBorder(Color.black));
        //button jp
        JPanel buttonJP = new JPanel();


        //layout
        buttonJP.setLayout(new BoxLayout(buttonJP, BoxLayout.X_AXIS));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonJP.add(addButton);
        buttonJP.add(removeButton);
        tmpJP.setLayout(new BoxLayout(tmpJP, BoxLayout.Y_AXIS));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        listScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        textAreaScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        tmpJP.add(infoLabel);
        tmpJP.add(listScroller);
        tmpJP.add(buttonJP);
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

    boolean contains(Node[] nodes, Node node){
        for (int i = 0; i < nodes.length - 1; i++) {
            if (nodes[i].idx == node.idx) return true;
        }
        return false;
    }

}
