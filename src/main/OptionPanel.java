package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class OptionPanel extends JPanel {
    JPanel paramPanel;
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
    JSlider distortion; //pak musim delit 10
    JSlider scale;
    JSlider sizeParamE; //pak musim delit 10
    JSlider sizeParamC; //pak musim delit 10
    JSlider cutOff; //pak musim delit 10
    JCheckBox api;

    public OptionPanel(Graph graph) {
        this.graph = graph;
        createUIElements();

        //clears jlist
        clearButton.addActionListener(e -> {
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
                        graph.removeFocus(item.idx);
                    }
                }
                graphCanvas.repaint();
            }
        });

        //adds selected nodes to jlist
        addButton.addActionListener(e -> {

            int oldNodes = selectedNodesJList.getModel().getSize();
            Node newNode = nodeJList.getSelectedValue();
            if (newNode != null) {
                Node[] tmp = new Node[1 + oldNodes];
                int index = 0;
                for (int i = 0; i < oldNodes; i++, index++) {
                    tmp[i] = selectedNodesJList.getModel().getElementAt(i);
                }

                if (!graph.focusSet.contains(newNode.idx)) {
                    tmp[tmp.length - 1] = newNode;
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
                tmp = new Node[newNodes.size()];
                for (int i = 0; i < newNodes.size(); i++) {
                    tmp[i] = newNodes.get(i);
                }


                selectedNodesJList.setListData(tmp);
            }
        });

        distortion.addChangeListener(e -> {
//            if (!distortion.getValueIsAdjusting()) {
                Fisheye.setD((float) distortion.getValue() / 100);
                System.out.println("D > " + (float) distortion.getValue() / 100);
                graph.focusChanged = true;
                graphCanvas.repaint();
//            }
        });
        scale.addChangeListener(e -> {
//            if (!distortion.getValueIsAdjusting()) {
                Fisheye.setS((float) scale.getValue());
                System.out.println("S > " + (float) scale.getValue());
                graph.focusChanged = true;
                graphCanvas.repaint();
//            }

        });
        sizeParamE.addChangeListener(e -> {
//            if (!distortion.getValueIsAdjusting()) {
                Fisheye.setE((float) sizeParamE.getValue() / 10);
                System.out.println("E > " + (float) sizeParamE.getValue() / 10);
                graph.focusChanged = true;
                graphCanvas.repaint();
//            }
        });
        sizeParamC.addChangeListener(e -> {
//            if (!distortion.getValueIsAdjusting()) {
                Fisheye.setC((float) sizeParamC.getValue() / 10);
                System.out.println("C > " + (float) sizeParamC.getValue() / 10);
                graph.focusChanged = true;
                graphCanvas.repaint();
//            }
        });
        cutOff.addChangeListener(e -> {
//            if (!distortion.getValueIsAdjusting()) {
                Fisheye.setCutoff((float) cutOff.getValue() / 10);
                System.out.println("cutOff > " + (float) cutOff.getValue() / 10);
                graph.focusChanged = true;
                graphCanvas.repaint();
//            }
        });
        api.addItemListener(e -> {
            Fisheye.setApiUsed(api.isSelected());
            graph.focusChanged = true;
            graphCanvas.repaint();
        });
    }

    public void createUIElements() {
        paramPanel = new JPanel();
        initParamPanel();
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
        JLabel paramLabel = new JLabel("Parameters setting");
        paramLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
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

        //borders
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        infoLabel2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
//        listScroller.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
//        textAreaScroller.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
//        clearButton.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        buttonJP.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

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
        paramPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paramLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tmpJP.add(paramLabel);
        tmpJP.add(paramPanel);
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

    public void initParamPanel() {
        paramPanel.setBackground(Color.LIGHT_GRAY);
        paramPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel disLabel = new JLabel("Distortion");
        JLabel scaleLabel = new JLabel("Scale");
        JLabel sizePraramELabel = new JLabel("Size param 1");
        JLabel sizePraramCLabel = new JLabel("Size param 2");
        JLabel cutoffLabel = new JLabel("Cutoff");
        distortion = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (Fisheye.d * 100)); //pak musim delit 100
        scale = new JSlider(JSlider.HORIZONTAL, 0, 50, (int) Fisheye.s);
        sizeParamE = new JSlider(JSlider.HORIZONTAL, 0, 10, (int) (Fisheye.e * 10)); //pak musim delit 10
        sizeParamC = new JSlider(JSlider.HORIZONTAL, 0, 20, (int) (Fisheye.c * 10)); //pak musim delit 10
        cutOff = new JSlider(JSlider.HORIZONTAL, 0, 10, (int) (Fisheye.cutoff * 10)); //pak musim delit 10
        api = new JCheckBox("Use API");
        api.setSelected(Fisheye.apiUsed);

        //jslider ticks
        distortion.setMajorTickSpacing(20);
        scale.setMajorTickSpacing(10);
        sizeParamE.setMajorTickSpacing(2);
        sizeParamC.setMajorTickSpacing(2);
        cutOff.setMajorTickSpacing(2);
        distortion.setMinorTickSpacing(5);
        scale.setMinorTickSpacing(5);
        sizeParamE.setMinorTickSpacing(1);
        sizeParamC.setMinorTickSpacing(1);
        cutOff.setMinorTickSpacing(1);
        distortion.setPaintTicks(true);
        scale.setPaintTicks(true);
        sizeParamE.setPaintTicks(true);
        sizeParamC.setPaintTicks(true);
        cutOff.setPaintTicks(true);
        distortion.setPaintLabels(true);
        scale.setPaintLabels(true);
        sizeParamE.setPaintLabels(true);
        sizeParamC.setPaintLabels(true);
        cutOff.setPaintLabels(true);


        //layouting and adding
        paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
        distortion.setAlignmentX(Component.CENTER_ALIGNMENT);
        scale.setAlignmentX(Component.CENTER_ALIGNMENT);
        sizeParamE.setAlignmentX(Component.CENTER_ALIGNMENT);
        sizeParamC.setAlignmentX(Component.CENTER_ALIGNMENT);
        cutOff.setAlignmentX(Component.CENTER_ALIGNMENT);
        api.setAlignmentX(Component.CENTER_ALIGNMENT);

        paramPanel.add(disLabel);
        paramPanel.add(distortion);
        paramPanel.add(scaleLabel);
        paramPanel.add(scale);
        paramPanel.add(sizePraramELabel);
        paramPanel.add(sizeParamE);
        paramPanel.add(sizePraramCLabel);
        paramPanel.add(sizeParamC);
        paramPanel.add(cutoffLabel);
        paramPanel.add(cutOff);
        paramPanel.add(api);

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
