/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx).
 *
 * How to cite this work:
 *  
@inproceedings{paulovich2007pex,
author = {Fernando V. Paulovich and Maria Cristina F. Oliveira and Rosane 
Minghim},
title = {The Projection Explorer: A Flexible Tool for Projection-based 
Multidimensional Visualization},
booktitle = {SIBGRAPI '07: Proceedings of the XX Brazilian Symposium on 
Computer Graphics and Image Processing (SIBGRAPI 2007)},
year = {2007},
isbn = {0-7695-2996-8},
pages = {27--34},
doi = {http://dx.doi.org/10.1109/SIBGRAPI.2007.39},
publisher = {IEEE Computer Society},
address = {Washington, DC, USA},
}
 *  
 * PEx is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * PEx is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * This code was developed by members of Computer Graphics and Image
 * Processing Group (http://www.lcad.icmc.usp.br) at Instituto de Ciencias
 * Matematicas e de Computacao - ICMC - (http://www.icmc.usp.br) of 
 * Universidade de Sao Paulo, Sao Carlos/SP, Brazil. The initial developer 
 * of the original code is Fernando Vieira Paulovich <fpaulovich@gmail.com>.
 *
 * Contributor(s): Rosane Minghim <rminghim@icmc.usp.br>
 *
 * You should have received a copy of the GNU General Public License along 
 * with PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */

package dataanalysis;

import dataanalysis.ttest.TTestDialog;
import datamining.neighbors.KNN;
import datamining.neighbors.Pair;
import distance.DetailedDistanceMatrix;
import distance.DistanceMatrix;
import distance.dissimilarity.Euclidean;
import graph.model.Connectivity;
import graph.model.Edge;
import graph.model.GraphInstance;
import graph.model.GraphModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import labeledgraph.model.LabeledGraphModel;
import matrix.AbstractMatrix;
import matrix.AbstractVector;
import matrix.dense.DenseMatrix;
import matrix.dense.DenseVector;
import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import projection.model.ProjectionInstance;
import projection.model.ProjectionModel;
import projection.model.Scalar;
import simpletree.model.SimpleTreeModel;
import simpletree.util.TreeDistanceCalculator;
import util.filter.EPSFilter;
import visualizationbasics.model.AbstractInstance;
import visualizationbasics.util.SaveDialog;

/**
 *
 * @author  Fernando Vieira Paulovich
 */
public class NeighborhoodHit extends javax.swing.JDialog {

    /** Creates new form NeighborhoodHit */
    private NeighborhoodHit(javax.swing.JDialog parent) {
        super(parent);
        initComponents();
        this.setModal(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        saveImageButton = new javax.swing.JButton();
        ttestButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Neighborhood Hit");
        setModal(true);

        saveImageButton.setText("Save Image");
        saveImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(saveImageButton);

        ttestButton.setText("TTest");
        ttestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ttestButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(ttestButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(closeButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void saveImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImageButtonActionPerformed
        int result = SaveDialog.showSaveDialog(new EPSFilter(),this,"","image.eps");

        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = SaveDialog.getFilename();

            FileOutputStream out = null;

            try {
                // Save this document to example.eps
                out = new FileOutputStream(filename);

                // Create a new document with bounding box 0 <= x <= 100 and 0 <= y <= 100.
                EpsGraphics g = new EpsGraphics(filename, out, 0, 0,
                        panel.getWidth() + 1, panel.getHeight() + 1, ColorMode.COLOR_RGB);

                freechart.draw(g, new Rectangle2D.Double(0, 0, panel.getWidth() + 1,
                        panel.getHeight() + 1));

                // Flush and close the document (don't forget to do this!)
                g.flush();
                g.close();

            } catch (IOException ex) {
                Logger.getLogger(NeighborhoodHit.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Problems saving the file", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(NeighborhoodHit.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_saveImageButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void ttestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ttestButtonActionPerformed
        TTestDialog.getInstance(this).display("Neighborhood Hit t-test",values);
}//GEN-LAST:event_ttestButtonActionPerformed

    public static NeighborhoodHit getInstance(javax.swing.JDialog parent) {
        return new NeighborhoodHit(parent);
    }

    public TreeMap<String,double[]> display(final ArrayList<Serie> series, final ArrayList<DistanceMatrix> dmats, final int maxneigh,
                        final boolean useVisEuclideanDistance, final boolean useWeight,
                        final boolean useEuclideanAsWeights, AbstractMatrix originalPoints) {

        final MessageDialog md = MessageDialog.show(this, "Calculating neighborhood hit...");

//        Thread t = new Thread() {

//            @Override
//            public void run() {
                NeighborhoodHit.this.freechart = NeighborhoodHit.this.createChart(NeighborhoodHit.this.createAllSeries(series,dmats,maxneigh,useVisEuclideanDistance,useWeight,useEuclideanAsWeights,originalPoints));
                //Uncomment and Modify this to specify a fixed range for y values.
//                XYPlot plot = NeighborhoodHit.this.freechart.getXYPlot();
//                NumberAxis axis = (NumberAxis)plot.getRangeAxis();
//                axis.setRange(-0.1,1.0); //Modify these values.
//                axis.setTickUnit(new NumberTickUnit(0.1));
                //
                NeighborhoodHit.this.panel = new ChartPanel(freechart);
                NeighborhoodHit.this.getContentPane().add(panel, BorderLayout.CENTER);

                NeighborhoodHit.this.setPreferredSize(new Dimension(650, 375));
                NeighborhoodHit.this.setSize(new Dimension(650, 375));

                NeighborhoodHit.this.setLocationRelativeTo(NeighborhoodHit.this.getParent());

                md.close();

                NeighborhoodHit.this.setVisible(true);
                return values;
//            }

//        };

 //       t.start();
    }

    private JFreeChart createChart(XYDataset xydataset) {
        //JFreeChart chart = ChartFactory.createXYLineChart("Neighborhood Hit","Number Neighbors", "Precision", xydataset, PlotOrientation.VERTICAL, true, true, false);
        JFreeChart chart = ChartFactory.createXYLineChart("","Number of Neighbors", "Precision", xydataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.WHITE);

        XYPlot xyplot = (XYPlot) chart.getPlot();
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        
        xyplot.setDomainGridlinePaint(Color.BLACK);
        xyplot.setRangeGridlinePaint(Color.BLACK);

        xyplot.setOutlinePaint(Color.BLACK);
        xyplot.setOutlineStroke(new BasicStroke(1.0f));
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setRangeCrosshairVisible(true);

        xyplot.setDrawingSupplier(new DefaultDrawingSupplier(
                new Paint[]{Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA,
                    Color.CYAN, Color.ORANGE, Color.BLACK, Color.DARK_GRAY, Color.GRAY,
                    Color.LIGHT_GRAY, Color.YELLOW
                }, DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));

        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setBaseShapesVisible(true);
        xylineandshaperenderer.setBaseShapesFilled(true);
        xylineandshaperenderer.setDrawOutlines(true);

        return chart;
    }

    private XYDataset createAllSeries(ArrayList<Serie> series, ArrayList<DistanceMatrix> dmats, int maxneigh,
                                      boolean useVisEuclideanDistance, boolean useWeight,
                                      boolean useEuclideanAsWeights, AbstractMatrix originalPoints) {
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();

        //Creating series for models...
        for (int i = 0; i < series.size(); i++) {
            if (values == null) values = new TreeMap<String,double[]>();
            double[] serieValues = this.neighborhoodHit(series.get(i).model,maxneigh,useVisEuclideanDistance,useWeight,useEuclideanAsWeights,originalPoints);
            //XYSeries xyseries = this.createSerie(series.get(i).name, serieValues);
            //xyseriescollection.addSeries(xyseries);
            if (values.containsKey(series.get(i).name)) {
                int idx = 0;
                String newName = series.get(i).name;
                do {
                    idx++;
                    newName += " "+idx;
                }while (values.containsKey(newName));
                values.put(newName,serieValues);
            }else
                values.put(series.get(i).name,serieValues);
            //values.put(series.get(i).name,serieValues);
        }

        //Creating series for dmats...
        if (dmats != null) {
            for (int i = 0; i < dmats.size(); i++) {
                if (values == null) values = new TreeMap<String,double[]>();
                double[] serieValues = this.neighborhoodHit(dmats.get(i),maxneigh);
                String name = "";
                if (dmats.get(i) instanceof DetailedDistanceMatrix)
                    name = ((DetailedDistanceMatrix)dmats.get(i)).getName();
                else
                    name = dmats.get(i).toString();
                if (name.isEmpty())
                    name = "Distance Matrix "+i;
                //XYSeries xyseries = createSerie(name,serieValues);
                values.put(name,serieValues);
                //xyseriescollection.addSeries(xyseries);
            }
        }
        
        for (int i=0;i<values.size();i++) {
            xyseriescollection.addSeries(createSerie((String)values.keySet().toArray()[i],(double[])values.values().toArray()[i]));
        }

        return xyseriescollection;
    }

    private XYSeries createSerie(String name, double[] values) {
        
        String n = name;

        XYSeries xyseries = new XYSeries(n);

        for (int i = 0; i < values.length; i++) {
            xyseries.add(i + 1, values[i]);
        }

        return xyseries;
    }

    public static AbstractMatrix exportPoints(ArrayList<AbstractInstance> instances, Scalar scalar) {

        AbstractMatrix matrix = new DenseMatrix();

        if (scalar == null) {
            return null;
//            ArrayList<Scalar> scalars = model.getScalars();
//            if ((scalars != null)&&(!scalars.isEmpty())) scalar = scalars.get(0);
        }

        ArrayList<String> labels = new ArrayList<String>();

          for (int i = 0; i < instances.size(); i++) {
            float[] point = new float[2];
            point[0] = ((ProjectionInstance)instances.get(i)).getX();
            point[1] = ((ProjectionInstance)instances.get(i)).getY();

            float cdata = ((ProjectionInstance)instances.get(i)).getScalarValue(scalar);
            Integer id = ((ProjectionInstance)instances.get(i)).getId();
            labels.add(((ProjectionInstance)instances.get(i)).toString());

            matrix.addRow(new DenseVector(point, id, cdata));
        }

        matrix.setLabels(labels);

        ArrayList<String> attributes = new ArrayList<String>();
        attributes.add("x");
        attributes.add("y");

        matrix.setAttributes(attributes);

        return matrix;
    }
    
    public static AbstractMatrix exportPoints(ArrayList<AbstractInstance> instances, AbstractMatrix originalPoints) {

        AbstractMatrix matrix = new DenseMatrix();

        if (originalPoints == null) return null;

        ArrayList<String> labels = new ArrayList<String>();

        for (int i = 0; i < instances.size(); i++) {
            float[] point = new float[2];
            point[0] = ((ProjectionInstance)instances.get(i)).getX();
            point[1] = ((ProjectionInstance)instances.get(i)).getY();

            float cdata = 0;
            AbstractVector v = originalPoints.getRow(originalPoints.getIds().indexOf(((ProjectionInstance)instances.get(i)).getId()));
            if (v != null)
                cdata = v.getKlass();
            Integer id = ((ProjectionInstance)instances.get(i)).getId();
            labels.add(((ProjectionInstance)instances.get(i)).toString());

            matrix.addRow(new DenseVector(point, id, cdata));
        }

        matrix.setLabels(labels);

        ArrayList<String> attributes = new ArrayList<String>();
        attributes.add("x");
        attributes.add("y");

        matrix.setAttributes(attributes);

        return matrix;
    }

    public Connectivity createEuclideanWeightConnectivity(GraphModel model, Connectivity con) {

        AbstractMatrix points = exportPoints(model.getInstances(),model.addScalar("cdata"));

        ArrayList<Edge> connEdges = con.getEdges();
        ArrayList<Integer> ids = points.getIds();
        ArrayList<Edge> euclideanDistEdges = new ArrayList<Edge>();

        if (connEdges == null) return null;
        if (ids == null) return null;

        for (int i=0;i<connEdges.size();i++) {
            int source = connEdges.get(i).getSource();
            int target = connEdges.get(i).getTarget();
            if (ids.indexOf(source) != -1 && ids.indexOf(target) != -1) {
                float distance = (new Euclidean()).calculate(points.getRow(ids.indexOf(source)),points.getRow(ids.indexOf(target)));
                euclideanDistEdges.add(new Edge(source,target,distance));
            }
        }

        Connectivity conn = new Connectivity("Plane Euclidian",euclideanDistEdges);
        return conn;
    }

    public DistanceMatrix createDistanceMatrixByFloydWarshall(GraphModel model, boolean useWeights,Connectivity euclideanDistAsWeightsCon) {

        DistanceMatrix dm = new DistanceMatrix();

        float[][] dmat = new float[model.getInstances().size()][model.getInstances().size()];
        for (int i=0;i<dmat.length;i++)
            for (int j=0;j<dmat[i].length;j++)
                if (i == j) dmat[i][j] = 0;
                else dmat[i][j] = Float.MAX_VALUE;

        ArrayList<Edge> edges = null;

        if (euclideanDistAsWeightsCon != null) {
            edges = euclideanDistAsWeightsCon.getEdges();
        }else {
            if (model.getSelectedConnectivity() != null)
                edges = model.getSelectedConnectivity().getEdges();
            else if ((model.getConnectivities() != null)&&(model.getConnectivities().size() > 1))
                    edges = model.getConnectivities().get(1).getEdges();
        }

        if (edges == null) return null;

        for (int k=0;k<edges.size();k++) {
            Edge ed = edges.get(k);
            //int x = model.getInstances().indexOf(model.getInstanceById(ed.getSource()));
            //int y = model.getInstances().indexOf(model.getInstanceById(ed.getTarget()));
            int x = model.getInstances().indexOf(getInstanceById(model,ed.getSource()));
            int y = model.getInstances().indexOf(getInstanceById(model,ed.getTarget()));
            if (useWeights) dmat[x][y] = dmat[y][x] = ed.getWeight();
            else dmat[x][y] = dmat[y][x] = 1.0f;
        }

        //Calculating the shortest path, in the tree, among all nodes (including virtual nodes)
        //Floyd Warshall algorithm.
        int n = dmat.length;
        for (int k=0; k<n; k++)
            for (int i=0; i<n; i++)
                for (int j=0; j<n; j++) {
                    float dd = dmat[i][k] + dmat[k][j];
                    if (dmat[i][j] > dd) dmat[i][j] = dd;
                }

        int k = -1;
        ArrayList<ArrayList<Float>> ndmat = new ArrayList<ArrayList<Float>>();
        for (int i=0;i<dmat.length;i++) {
            if (model instanceof SimpleTreeModel) {
                if (((GraphInstance)model.getInstances().get(i)).isValid()) {
                    k++;
                    ndmat.add(new ArrayList<Float>());
                    for (int j=0;j<dmat[i].length;j++) {
                        if (((GraphInstance)model.getInstances().get(j)).isValid()) {
                            ndmat.get(k).add(dmat[i][j]);
                        }
                    }
                }
            }else {
                if (!model.getInstances().get(i).toString().isEmpty()) {
                    k++;
                    ndmat.add(new ArrayList<Float>());
                    for (int j=0;j<dmat[i].length;j++) {
                        if (!model.getInstances().get(j).toString().isEmpty()) {
                            ndmat.get(k).add(dmat[i][j]);
                        }
                    }
                }
            }
        }

        //Create and fill the distance distmatrix
        dm.setElementCount(ndmat.size());

        float maxDistance = Float.NEGATIVE_INFINITY;
        float minDistance = Float.POSITIVE_INFINITY;

        float[][] distmat = new float[ndmat.size() - 1][];
        for (int i=0; i<ndmat.size()-1; i++) {
            distmat[i] = new float[i + 1];
            for (int j=0;j<distmat[i].length; j++) {
                float distance = ndmat.get(i+1).get(j);
                if (distance < minDistance) minDistance = distance;
                if (distance > maxDistance) maxDistance = distance;
                if ((i+1)!=j) {
                    if ((i+1) < j) distmat[j - 1][(i+1)] = distance;
                    else distmat[(i+1) - 1][j] = distance;
                }
            }
        }
        dm.setMinDistance(minDistance);
        dm.setMaxDistance(maxDistance);
        dm.setDistmatrix(distmat);

        return dm;

    }

    public DistanceMatrix createDistanceMatrixByHeight(AbstractMatrix dataDmat, GraphModel model, boolean useWeights, Connectivity euclideanDistAsWeightsCon) {

        DistanceMatrix dm = new DistanceMatrix();

        ArrayList<Edge> edges = null;

        if (euclideanDistAsWeightsCon != null) {
            edges = euclideanDistAsWeightsCon.getEdges();
        }else {
            if (model.getSelectedConnectivity() != null)
                edges = model.getSelectedConnectivity().getEdges();
            else if ((model.getConnectivities() != null)&&(model.getConnectivities().size() > 1))
                    edges = model.getConnectivities().get(1).getEdges();
        }

        if (edges == null) return null;

        //Create and fill the distance distmatrix
        int numInstances = getValidInstances(model).size();
        dm.setElementCount(numInstances);

        float maxDistance = Float.NEGATIVE_INFINITY;
        float minDistance = Float.POSITIVE_INFINITY;

        System.out.println("Creating distance matrix from tree...");
        TreeDistanceCalculator t = new TreeDistanceCalculator();
        float[][] distmat = new float[numInstances - 1][];
        for (int i=0; i<numInstances-1; i++) {
            if (i % ((int)(numInstances*0.1)) == 0) System.out.println("-- Processing instance "+i);
            distmat[i] = new float[i + 1];
            for (int j=0;j<distmat[i].length; j++) {
                float distance = t.getDistance(edges,dataDmat.getIds().get(j),dataDmat.getIds().get(i+1),useWeights);
                if (distance < minDistance) minDistance = distance;
                if (distance > maxDistance) maxDistance = distance;
                distmat[i][j] = distance;
                //if ((i+1)!=j) {
                //    if ((i+1) < j) distmat[j - 1][(i+1)] = distance;
                //    else distmat[(i+1) - 1][j] = distance;
                //}
            }
        }
        dm.setMinDistance(minDistance);
        dm.setMaxDistance(maxDistance);
        dm.setDistmatrix(distmat);
//        try {
//            dm.save("D:\\DistUsingHeights.dmat");
//        } catch (IOException ex) {
//            Logger.getLogger(NeighborhoodPreservation.class.getName()).log(Level.SEVERE, null, ex);
//        }

        System.out.println("Distance matrix from tree created.");
        return dm;

    }
    
    private double[] neighborhoodHit(ProjectionModel model, int maxneigh, boolean useVisEuclideanDistance,
                                     boolean useWeight, boolean useEuclideanAsWeights, AbstractMatrix originalPoints) {
        double[] valuesRet = new double[maxneigh];

        if (model == null) return valuesRet;

        try {           
            Scalar scdata = model.addScalar("cdata");
            //AbstractMatrix points = exportPoints(model,scdata);

            DistanceMatrix projectionDmat = null;
            AbstractMatrix validPoints = null;

            if (model instanceof GraphModel) {//Hierarquical Projection, uses paths to determine distances...
                //validPoints = exportPoints(((LabeledGraphModel)model).getValidInstances(),scdata);
                if (model instanceof SimpleTreeModel)
                    validPoints = exportPoints(getValidInstances((GraphModel)model),originalPoints);
                else
                    validPoints = exportPoints(getValidInstances((GraphModel)model),scdata);
                //validPoints = exportPoints(((LabeledGraphModel)model).getValidInstances(),scdata);
                if (useVisEuclideanDistance)
                    projectionDmat = new DistanceMatrix(validPoints, new Euclidean());
                else {
                    if (useEuclideanAsWeights) {
                        Connectivity gCon = null;
                        if (((GraphModel)model).getSelectedConnectivity() != null)
                            gCon = ((GraphModel)model).getSelectedConnectivity();
                        else if ((((GraphModel)model).getConnectivities() != null)&&(((GraphModel)model).getConnectivities().size() > 1))
                                gCon = ((GraphModel)model).getConnectivities().get(1);
                        if (gCon == null) throw new IOException("Error: Graph connectivity not found.");
                        Connectivity euclideanWeightsConn = createEuclideanWeightConnectivity((GraphModel)model,gCon);
                        //projectionDmat = createDistanceMatrix((GraphModel)model,useWeight,euclideanWeightsConn);
                        projectionDmat = createDistanceMatrixByHeight(originalPoints,(GraphModel)model,useWeight,euclideanWeightsConn);
                    } else
                        //projectionDmat = createDistanceMatrix((GraphModel)model,useWeight,null);
                        projectionDmat = createDistanceMatrixByHeight(originalPoints,(GraphModel)model,useWeight,null);

                    ArrayList<String> labels = validPoints.getLabels();
                    ArrayList<Integer> ids = validPoints.getIds();

                    float[] c = null;
                    
//                    if (model instanceof SimpleTreeModel) {
//                        c = new float[ids.size()];
//                        for (int i=0;i<ids.size();i++) {
//                            AbstractVector v = originalPoints.getRow(originalPoints.getIds().indexOf(ids.get(i)));
//                            if (v != null) {
//                                c[i] = v.getKlass();
//                                //validPoints.getRow(i).setKlass(v.getKlass());
//                            } else {
//                                c[i] = 0;
//                                //validPoints.getRow(i).setKlass(0);
//                            }
//                        }
//                    } else
                    c = validPoints.getClassData();
                    
    //                ArrayList<Float> cdatas = new ArrayList<Float>();

    //                int k=0;
    //                //excluding virtual nodes...
    //                for (int i=0;i<labels.size();i++) {
    //                    if (labels.get(i).isEmpty()) {
    //                        labels.remove(i);
    //                        ids.remove(i);
    //                        i--;
    //                    }else cdatas.add(c[k]);
    //                    k++;
    //                }
    //                float[] c2 = new float[cdatas.size()];
    //                for (int i=0;i<cdatas.size();i++) c2[i] = cdatas.get(i);
                    projectionDmat.setLabels(labels);
                    projectionDmat.setIds(ids);
                    projectionDmat.setClassData(c);
                }
            }else {
                validPoints = exportPoints(model.getInstances(),scdata);
                projectionDmat = new DistanceMatrix(validPoints, new Euclidean());
            }
            System.out.println("Calculating neighbors...");
            for (int n = 0; n < maxneigh; n++) {
                KNN knn = new KNN(n + 1);
                Pair[][] neighbors = knn.execute(projectionDmat);

                float percentage = 0.0f;
                System.out.println("- Calculating neighbor "+n);
                for (int i = 0; i < validPoints.getRowCount(); i++) {
                    if (i % ((int)(projectionDmat.getElementCount()*0.1)) == 0) System.out.println("-- Calculating neighborhood hit for instance "+i);
                    float c = validPoints.getRow(i).getKlass();

                    float total = 0.0f;
                    for (int j = 0; j < n + 1; j++) {
                        if (c == validPoints.getRow(neighbors[i][j].index).getKlass()) {
                            total++;
                        }
                    }
                    //System.out.println(total);

                    percentage += total / (n + 1);
                }

                valuesRet[n] = percentage / validPoints.getRowCount();
            }
            System.out.println("Neighborhood Hit:");
            StringBuilder resp = new StringBuilder();
            if (valuesRet != null && valuesRet.length > 0) {
                resp.append("[ ");
                for (int ii=0;ii<valuesRet.length;ii++)
                    resp.append(valuesRet[ii]+" ");
                resp.append("]");
            }
            System.out.println(resp);
        } catch (IOException ex) {
            Logger.getLogger(NeighborhoodHit.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valuesRet;
    }

    private double[] neighborhoodHit(DistanceMatrix dmat, int maxneigh) {

        double[] valuesRet = new double[maxneigh];
        try {
            for (int n = 0; n < maxneigh; n++) {
                KNN knn = new KNN(n + 1);
                Pair[][] neighbors = knn.execute(dmat);
                float percentage = 0.0f;
                for (int i = 0; i < dmat.getElementCount(); i++) {// validPoints.getRowCount(); i++) {
                    float c = dmat.getClassData()[i];// validPoints.getRow(i).getKlass();
                    float total = 0.0f;
                    for (int j = 0; j < n + 1; j++) {
                        if (c == dmat.getClassData()[neighbors[i][j].index]) {//validPoints.getRow(neighbors[i][j].index).getKlass()) {
                            total++;
                        }
                    }
                    percentage += total / (n + 1);
                }
                valuesRet[n] = percentage / dmat.getElementCount();//validPoints.getRowCount();
            }
        } catch (IOException ex) {
            Logger.getLogger(NeighborhoodHit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valuesRet;
    }

    //The next two methods are here because the GraphModel does not have them, but two of its subclasses, SIMPLETREEMODEL and LABELEDGRAPHMODEL have. The
    //ideal is put it on the GraphModel, it is not done because the project is on the trunk and I did not want to modify it.
    private GraphInstance getInstanceById(GraphModel model, int id) {
        for (int i=0;i<model.getInstances().size();i++) {
            if (model.getInstances().get(i).getId() == id) {
                return (GraphInstance)model.getInstances().get(i);
            }
        }
        return null;
    }
    
    private ArrayList<AbstractInstance> getValidInstances(GraphModel model) {
        ArrayList selinsts = model.getInstances();
        ArrayList<AbstractInstance> valids = new ArrayList<AbstractInstance>();
        if (selinsts != null) {
            for (int i=0;i<selinsts.size();i++) {
                if (((GraphInstance)(selinsts.get(i))).isValid())
                    valids.add((AbstractInstance) selinsts.get(i));
            }
        }
        return valids;
    }
    
    private JFreeChart freechart;
    private JPanel panel;
    private TreeMap<String,double[]> values;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton saveImageButton;
    private javax.swing.JButton ttestButton;
    // End of variables declaration//GEN-END:variables
}
