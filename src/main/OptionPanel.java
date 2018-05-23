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
 * Displays graph canvas and option/information panel => GUI
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

    /**
     * creates all GUI elements and handles all listeners
     * @param graph - loaded graph from file
     */
    public OptionPanel(Graph graph) {
        this.graph = graph;
        createUIElements();

        //clears jlist of selected nodes and clears graph canvas
        clearButton.addActionListener(e -> {
            selectedNodesJList.setListData(new Node[0]);
            for (Node n :
                    graph.getNodeList()) {
                n.highlighted = false;
            }
            graph.removeFocusAll();
            graphCanvas.repaint();
        });

        //removes selected node from list of selected nodes
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

        //adds selected nodes from jlist of all nodes to jlist of selected nodes
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

        //shows help/info about GUI
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "'ADD': Adds selected airport from 'list of all airports' to the 'list of selected airports' and highlights it.\n" +
                    "'REMOVE': Removes selected airport from 'list of selected airports' and un-highlights it.\n" +
                    "'HELP?': Shows this help.\n" +
                    "'Clear All': Removes all airports from 'list of selected airports' and un-highlights them.\n", "Help", JOptionPane.QUESTION_MESSAGE);
        });

        //add selected node from graphCanvas to jlist of selected nodes
        graphCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ListModel<Node> oldNodesList = selectedNodesJList.getModel();
                List<Node> newNodes = new ArrayList<>();
                for (int i = 0; i < oldNodesList.getSize(); i++) {
                    newNodes.add(oldNodesList.getElementAt(i));
                }
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

        //handles distortion slider
        distortion.addChangeListener(e -> {
                Fisheye.setD((float) distortion.getValue() / 100);
                //System.out.println("D > " + (float) distortion.getValue() / 100);
                graph.focusChanged = true;
                graphCanvas.repaint();
        });
        //handles scale slider
        scale.addChangeListener(e -> {
                Fisheye.setS((float) scale.getValue());
                //System.out.println("S > " + (float) scale.getValue());
                graph.focusChanged = true;
                graphCanvas.repaint();
        });
        //handles param e slider
        sizeParamE.addChangeListener(e -> {
                Fisheye.setE((float) sizeParamE.getValue() / 10);
                //System.out.println("E > " + (float) sizeParamE.getValue() / 10);
                graph.focusChanged = true;
                graphCanvas.repaint();
        });
        //handles param c slider
        sizeParamC.addChangeListener(e -> {
                Fisheye.setC((float) Math.max(sizeParamC.getValue()/10.f, 0.01));
                //System.out.println("C > " + (float) sizeParamC.getValue() / 10);
                graph.focusChanged = true;
                graphCanvas.repaint();
        });
        //handles cutoff slider
        cutOff.addChangeListener(e -> {
                Fisheye.setCutoff((float) cutOff.getValue());
                //System.out.println("cutOff > " + (float) cutOff.getValue() / 100);
                graph.focusChanged = true;
                graphCanvas.repaint();
        });
        //checks api value
        api.addItemListener(e -> {
            Fisheye.setApiUsed(api.isSelected());
            //System.out.println("API > " + api.isSelected());
            graph.focusChanged = true;
            graphCanvas.repaint();
        });
    }

    /**
     * creates all UI elements
     */
    public void createUIElements() {
        paramPanel = new JPanel();//jpanel of changeable parameters
        initParamPanel(); // inits this panel
        //show button - higlights selected nodes on map and shows info
        addButton = new JButton("ADD");
        addButton.setToolTipText("Add selected node from list above.");
        removeButton = new JButton("REMOVE"); //removes selected nodes
        removeButton.setToolTipText("Remove selected node form list below.");
        //clear button
        clearButton = new JButton("Clear All");
        clearButton.setToolTipText("Clears all selected and highlighted nodes.");
        //help button
        helpButton = new JButton("HELP?");
        helpButton.setToolTipText("Shows help.");
        infoLabel = new JLabel();//heading
        infoLabel.setText("List of all airports to select from: ");
        infoLabel2 = new JLabel();//heading
        infoLabel2.setText("List of selected airports: ");
        JLabel paramLabel = new JLabel("Parameters setting");//heading
        paramLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        //lists all nodes
        nodeJList = new JList<>(graph.getNodeList());
        nodeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nodeJList.setLayoutOrientation(JList.VERTICAL);
        nodeJList.setVisibleRowCount(15);
        listScroller = new JScrollPane(nodeJList);
        //lists only selected nodes
        selectedNodesJList = new JList<>();
        selectedNodesJList.setLayoutOrientation(JList.VERTICAL);
        selectedNodesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        textAreaScroller = new JScrollPane(selectedNodesJList/*selectedNodesInfo*/);
        //graph canvas
        graphCanvas = new GraphCanvas(graph);
        //tmp jpanel for UI elements
        JPanel tmpJP = new JPanel();
        tmpJP.setBorder(BorderFactory.createLineBorder(Color.black));
        //button jp
        JPanel buttonJP = new JPanel();

        //borders
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        infoLabel2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        buttonJP.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        //layout and adding
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

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        tmpJP.setMaximumSize(new Dimension((Toolkit.getDefaultToolkit().getScreenSize().width / 3), Toolkit.getDefaultToolkit().getScreenSize().height));//TODO
        this.add(graphCanvas);
        this.add(tmpJP);
    }

    /**
     * creates param jpanel and its sliders
     */
    public void initParamPanel() {
        paramPanel.setBackground(Color.LIGHT_GRAY);
        paramPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel disLabel = new JLabel("Distortion");
        JLabel scaleLabel = new JLabel("Scale");
        JLabel sizePraramELabel = new JLabel("Size param E");
        JLabel sizePraramCLabel = new JLabel("Size param C");
        JLabel cutoffLabel = new JLabel("Cutoff");
        distortion = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (Fisheye.d * 100)); //pak musim delit 100
        scale = new JSlider(JSlider.HORIZONTAL, 0, 50, (int) Fisheye.s);
        sizeParamE = new JSlider(JSlider.HORIZONTAL, 0, 10, (int) (Fisheye.e * 10)); //pak musim delit 10
        sizeParamC = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (Fisheye.c* 10)); //pak musim delit 10
        cutOff = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (Fisheye.cutoff)); //pak musim delit 10
        api = new JCheckBox("Use API");
        api.setSelected(Fisheye.apiUsed);

        //jslider ticks
        distortion.setMajorTickSpacing(100);
        scale.setMajorTickSpacing(10);
        sizeParamE.setMajorTickSpacing(2);
        sizeParamC.setMajorTickSpacing(10);
        cutOff.setMajorTickSpacing(20);
        distortion.setMinorTickSpacing(50);
        scale.setMinorTickSpacing(5);
        sizeParamE.setMinorTickSpacing(1);
        sizeParamC.setMinorTickSpacing(5);
        cutOff.setMinorTickSpacing(25);
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
        api.setAlignmentX(Component.LEFT_ALIGNMENT);

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
}
