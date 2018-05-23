package main;

import org.omg.CORBA.NO_IMPLEMENT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class OptionPanel extends JPanel {
    //    JButton showButton;
    JButton addButton;
    JButton removeButton;
    JButton clearButton;
    JButton helpButton;
    JLabel infoLabel, infoLabel2;
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
            graph.removeFocusAll();
            graphCanvas.repaint();
        });

        //removes selected
        removeButton.addActionListener(e -> {

//            List<Node> toRemove = selectedNodesJList.getSelectedValuesList();
            Node nodeToRemove = selectedNodesJList.getSelectedValue();
            if (nodeToRemove != null) {
                int oldNodes = selectedNodesJList.getModel().getSize();
                Node[] tmp = new Node[oldNodes - 1];
                Node[] tmp2 = new Node[oldNodes];
                int index = 0;
                for (int i = 0; i < oldNodes; i++, index++) {
                    tmp2[i] = selectedNodesJList.getModel().getElementAt(i);
                }

                List<Node> result = new LinkedList<Node>();
                for (Node item : tmp2) {
                    if (!item.equals(null)) {
                        if (item.idx != nodeToRemove.idx) {
                            result.add(item);
                        }
                    }
                }
                result.toArray(tmp);
                selectedNodesJList.setListData(tmp);

                for (Node item : graph.getNodeList()) {
                    if (item.idx == nodeToRemove.idx) {
//                        item.highlighted = false;
                        graph.removeFocus(item.idx);
                    }
                }
                graphCanvas.repaint();
            }
        });

        //adds selected nodes to textarea/jlist
        addButton.addActionListener(e -> {

            //funguje, ale neni to "pekne" + je to pomale
            int oldNodes = selectedNodesJList.getModel().getSize();
            Node newNode = nodeJList.getSelectedValue();
            if (newNode != null) {
                Node[] tmp = new Node[1 + oldNodes];
//            selectedNodesInfo.setText("");
                int index = 0;
                for (int i = 0; i < oldNodes; i++, index++) {
                    tmp[i] = selectedNodesJList.getModel().getElementAt(i);
                }

//            for (int i = index, j = 0; j < newNodes.size(); i++, j++) {
//                if (!newNodes.get(j).highlighted) {
////                    selectedNodesInfo.append(n.toString() + "\n");
//                    tmp[i] = newNodes.get(j);
//                    newNodes.get(j).highlighted = true;
//                }
//            }
                if (!graph.focusSet.contains(newNode.idx)) {
                    tmp[tmp.length - 1] = newNode;
//                    newNode.highlighted = true;
                    graph.addFocus(newNode.idx);
                }


                selectedNodesJList.setListData(tmp);
                graphCanvas.repaint();
            }
        });

        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "'ADD': Adds selected airport from 'list of all airports' to the 'list of selected airports' and highlights it.\n" +
                    "'REMOVE': Removes selected airport from 'list of selected airports' and un-highlights it.\n" +
                    "'HELP?': Shows this help.\n" +
                    "'Clear All': Removes all airports from 'list of selected airports' and un-highlights them.\n", "Help", JOptionPane.QUESTION_MESSAGE);
        });

        //add selected node from graphCanvas
        graphCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                selectedNodesInfo.setText("");

//                int oldNodes = selectedNodesJList.getModel().getSize();
                ListModel<Node> oldNodesList = selectedNodesJList.getModel();
                List<Node> newNodes = new ArrayList<>();
                for (int i = 0; i < oldNodesList.getSize(); i++) {
                    newNodes.add(oldNodesList.getElementAt(i));
                }
                List<Node> newNodesList = new ArrayList<>();
                Node[] tmp;

                for (Node n :
                        graph.getNodeList()) {
                    if (!newNodes.contains(n) && graph.focusSet.contains(n.idx)) {
                        newNodes.add(n);
                        break;
                    } else if (newNodes.contains(n) && !graph.focusSet.contains(n.idx)) {
                        newNodes.remove(n);
                        break;
                    }
                }
//                int index = 0;
//                for (int i = 0; i < oldNodes; i++, index++) {
//                    tmp[i] = selectedNodesJList.getModel().getElementAt(i);
//                }
//                for (Node n :
//                        graph.getNodeList()) {
//                    if (graph.focusSet.contains(n.idx) && !contains(tmp, n)) {
////                        selectedNodesInfo.append(n.toString() + "\n");
//                        tmp[tmp.length - 1] = n;
//                        break;
//                    } else if (!graph.focusSet.contains(n.idx) && contains(tmp, n)){
//                        tmp = new Node[oldNodes - 1];
//                        for (int i = 0; i < oldNodes - 1; i++, index++) {
//                            tmp[i] = selectedNodesJList.getModel().getElementAt(i);
//                        }
//                    }
//                }

                tmp = new Node[newNodes.size()];
                for (int i = 0; i < newNodes.size(); i++) {
                    tmp[i] = newNodes.get(i);
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

    public void createUIElements() {
        //show button - higlights selected nodes on map and shows info
//        showButton = new JButton("Select");
//        showButton.setName("showButton");
        addButton = new JButton("ADD");
        addButton.setToolTipText("Add selected node from list above.");
        removeButton = new JButton("REMOVE");
        removeButton.setToolTipText("Remove selected node form list below.");
        //clear button
        clearButton = new JButton("Clear All");
        clearButton.setToolTipText("Clears all selected and highlighted nodes.");
        //help button
        helpButton = new JButton("HELP?");
        helpButton.setToolTipText("Shows help.");
        //shows info about nodes which are hoovered over
        infoLabel = new JLabel();
        infoLabel.setText("List of all airports to select from: ");
        infoLabel2 = new JLabel();
        infoLabel2.setText("List of selected airports: ");
        //lists all nodes
        nodeJList = new JList<>(graph.getNodeList());
        nodeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonJP.add(addButton);
        buttonJP.add(removeButton);
        buttonJP.add(helpButton);
        tmpJP.setLayout(new BoxLayout(tmpJP, BoxLayout.Y_AXIS));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        listScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        textAreaScroller.setAlignmentX(Component.CENTER_ALIGNMENT);
        tmpJP.add(infoLabel);
        tmpJP.add(listScroller);
        tmpJP.add(buttonJP);
        tmpJP.add(infoLabel2);
        tmpJP.add(textAreaScroller);
        tmpJP.add(clearButton);
//        this.add(infoLabel);
//        this.add(listScroller);
//        this.add(showButton);
//        this.add(textAreaScroller);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        tmpJP.setMaximumSize(new Dimension((Toolkit.getDefaultToolkit().getScreenSize().width / 3), Toolkit.getDefaultToolkit().getScreenSize().height));
        this.add(graphCanvas);
        this.add(tmpJP);


//        this.add(graphPanel);
//        this.add(optionsPanel);
    }

    boolean contains(Node[] nodes, Node node) {
        if (nodes.length > 1) {
            for (int i = 0; i < nodes.length - 1; i++) {
                if (nodes[i].idx == node.idx) return true;
            }
        }
        return false;
    }
}
