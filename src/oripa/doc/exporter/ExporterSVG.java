package oripa.doc.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;
import oripa.geom.OriLine;
import oripa.view.RenderScreen2;

public class ExporterSVG implements Exporter{

    static final int size = 1000;
    final static String head =
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n"
            + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"\n"
            + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n"
            + "<svg xmlns=\"http://www.w3.org/2000/svg\"\n"
            + " xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\"\n"
            + " width=\"" + size + "px\" height=\"" + size + "px\"\n"
            + " viewBox=\"0 0 " + size + " " + size + "\" >\n";
    final static String end = "</svg>";
    final static String gradient =
            " <linearGradient id=\"Gradient1\" x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n"
            + " <stop offset=\"5%\" stop-color=\"#DDEEFF\" />\n"
            + " <stop offset=\"95%\" stop-color=\"#7788FF\" />\n"
            + " </linearGradient>\n"+
            " <linearGradient id=\"Gradient2\" x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n"
            + " <stop offset=\"5%\" stop-color=\"#FFFFEE\" />\n"
            + " <stop offset=\"95%\" stop-color=\"#DDDDDD\" />\n"
            + " </linearGradient>\n";
    final static String lineStart = " <line stroke=\"blue\" stroke-width=\"2\" ";
    final static String polygonStart = "<path style=\"fill:url(#Gradient1);"
            + "stroke:#0000ff;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;"
            + "stroke-opacity:1;fill-opacity:1.0\" d=\"M ";
    final static String polygonStart2 = "<path style=\"fill:url(#Gradient2);"
            + "stroke:#0000ff;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;"
            + "stroke-opacity:1;fill-opacity:1.0\" d=\"M ";


    public static void exportModel(Doc doc, String filepath) throws Exception {
        double scale = (size-5) / doc.size;
        double center = size / 2;
        FileWriter fw = new FileWriter(filepath);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
            Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
            Vector2d modelCenter = new Vector2d();
            for (OriFace face : ORIPA.doc.faces) {
                for (OriHalfedge he : face.halfedges) {
                    maxV.x = Math.max(maxV.x, he.vertex.p.x);
                    maxV.y = Math.max(maxV.y, he.vertex.p.y);
                    minV.x = Math.min(minV.x, he.vertex.p.x);
                    minV.y = Math.min(minV.y, he.vertex.p.y);
                }
            }
            modelCenter.x = (maxV.x + minV.x) / 2;
            modelCenter.y = (maxV.y + minV.y) / 2;
            bw.write(head);
            bw.write(gradient);
            
            ArrayList<OriFace> sortedFaces = new ArrayList<>();
            boolean [] isSorted = new boolean[ORIPA.doc.faces.size()];
            for (int i = 0; i < ORIPA.doc.faces.size(); i++) {
                for (int j = 0; j < ORIPA.doc.overlapRelation.length; j++) {
                    int numberOf2 = 0;
                    if(!isSorted[j]){
                        for (int k = 0; k < isSorted.length; k++) {
                            if ((!isSorted[k]) && ORIPA.doc.overlapRelation[j][k]==2) {
                                numberOf2++;
                            }
                        }
                        if(numberOf2==0){
                            isSorted[j] = true;
                            sortedFaces.add(ORIPA.doc.faces.get(j));
                            break;
                        }                        
                    }
                }
            }
            
            
            for (int i=0; i<sortedFaces.size(); i++) {
                OriFace face = RenderScreen2.isM_bFaceOrderFlip() ? sortedFaces.get(i)
                        : sortedFaces.get(sortedFaces.size()-i-1);
                java.util.ArrayList<Vector2d> points = new java.util.ArrayList<>();
                for (OriHalfedge he : face.halfedges) {

                    if (he.vertex.p.x > maxV.x) {
                        throw new Exception("Size of vertices exceeds maximum");
                    }

                    double x1 = (he.vertex.p.x - modelCenter.x) * scale + center;
                    double y1 = -(he.vertex.p.y - modelCenter.y) * scale + center;
                    double x2 = (he.next.vertex.p.x - modelCenter.x) * scale + center;
                    double y2 = -(he.next.vertex.p.y - modelCenter.y) * scale + center;
                    if (!points.contains(new Vector2d(x1, y1))) {
                        points.add(new Vector2d(x1, y1));
                    }
                    if (!points.contains(new Vector2d(x2, y2))) {
                        points.add(new Vector2d(x2, y2));
                    }
                }
                if((!face.faceFront&&RenderScreen2.isM_bFaceOrderFlip())
                        || (face.faceFront&&!RenderScreen2.isM_bFaceOrderFlip())){
                    bw.write(polygonStart);
                }else{
                    bw.write(polygonStart2);
                }
                for (Vector2d p : points) {
                    bw.write(p.x + "," + p.y + " ");
                }
                bw.write(" z\" />\n");
            }
            bw.write(end);
        }
        
    }

    public boolean export(Doc doc, String filepath) throws Exception {
        double scale = size / doc.size;
        double center = size / 2;
        FileWriter fw = new FileWriter(filepath);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(head);
            for (OriLine line : doc.creasePattern) {
                bw.write(" <line style=\"");
                String style = "stroke:gray;stroke-width:1;";
                switch (line.typeVal) {
                    case OriLine.TYPE_CUT:
                        style = "stroke:black;stroke-width:4;";
                        break;
                    case OriLine.TYPE_RIDGE:
                        style = "stroke:red;stroke-width:2;";
                        break;
                    case OriLine.TYPE_VALLEY:
                        style = "stroke:blue;stroke-width:2;stroke-opacity:1";
                        break;
                }
                bw.write(style + "\" ");
                bw.write("x1=\"");
                bw.write("" + (line.p0.x * scale + center) + "\"");
                bw.write(" y1=\"");
                bw.write("" + ((doc.size / 2 - line.p0.y) * scale) + "\"");
                bw.write(" x2=\"");
                bw.write("" + (line.p1.x * scale + center) + "\"");
                bw.write(" y2=\"");
                bw.write("" + ((doc.size / 2 - line.p1.y) * scale) + "\" />\n");
            }
            bw.write(end);
        }
        
        return true;
    }    
    
    public static void exportDotted(Doc doc, String filepath) throws Exception {
        double scale = size / doc.size;
        double center = size / 2;
        FileWriter fw = new FileWriter(filepath);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(head);
            for (OriLine line : doc.creasePattern) {
                bw.write(" <line style=\"");
                String style = "stroke:gray;stroke-width:1;";
                switch (line.typeVal) {
                    case OriLine.TYPE_CUT:
                        style = "stroke:black;stroke-width:5;";
                        break;
                    case OriLine.TYPE_RIDGE:
                        style = "stroke-dasharray: 15, 10;stroke:red;stroke-width:4;";
                        break;
                    case OriLine.TYPE_VALLEY:
                        style = "stroke:blue;stroke-width:4;stroke-opacity:1";
                        break;
                }
                bw.write(style + "\" ");
                bw.write("x1=\"");
                bw.write("" + (line.p0.x * scale + center) + "\"");
                bw.write(" y1=\"");
                bw.write("" + ((doc.size / 2 - line.p0.y) * scale) + "\"");
                bw.write(" x2=\"");
                bw.write("" + (line.p1.x * scale + center) + "\"");
                bw.write(" y2=\"");
                bw.write("" + ((doc.size / 2 - line.p1.y) * scale) + "\" />\n");
            }
            bw.write(end);
        }
    }
}
