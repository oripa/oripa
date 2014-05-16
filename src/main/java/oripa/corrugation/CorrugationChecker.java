package oripa.corrugation;

import java.util.ArrayList;
import javax.vecmath.Vector2d;

import oripa.fold.OrigamiModel;
import oripa.fold.OriEdge;
import oripa.fold.OriFace;
import oripa.fold.OriHalfedge;
import oripa.fold.OriVertex;
import oripa.value.OriLine;

public class CorrugationChecker {

    final public static int TYPE_EDGE_VERTEX = 0;
    final public static int TYPE_NONORIENTED_VERTEX = 1;
    final public static int TYPE_VALLEY_VERTEX = 2;
    final public static int TYPE_MOUNTAIN_VERTEX = 3;


    public int getVertexType(OriVertex v){
        int[] edgeTypeCount = {0, 0};
        for (OriEdge e: v.edges){
            if (e.type == OriLine.TYPE_CUT){
                return TYPE_EDGE_VERTEX;
            }
            if (e.type == OriLine.TYPE_RIDGE){
                edgeTypeCount[0]++;
            }
            if (e.type == OriLine.TYPE_VALLEY){
                edgeTypeCount[1]++;
            }
        }

        if(edgeTypeCount[0] < edgeTypeCount[1]){
            return TYPE_MOUNTAIN_VERTEX;
        }

        if(edgeTypeCount[0] > edgeTypeCount[1]){
            return TYPE_VALLEY_VERTEX;
        }

        return TYPE_NONORIENTED_VERTEX;
    }

    public boolean evaluateSingleVertexTypeCondition(OriVertex v){
        OriVertex oppositeVertex = v;
        ArrayList<Integer> vertexTypes;
        int thisVertexType = getVertexType(v);
        int oppositeVertexType;
        boolean anyEdges = false;
        if (thisVertexType == TYPE_EDGE_VERTEX){
            return true;
        }

        for (OriEdge e: v.edges){
            if(e.sv == v){
                oppositeVertex = e.ev;
            }else{
                oppositeVertex = e.sv;
            }
            oppositeVertexType = getVertexType(oppositeVertex);
            if(oppositeVertexType == TYPE_EDGE_VERTEX){
                anyEdges = true;
            }
            if(oppositeVertexType != TYPE_EDGE_VERTEX && oppositeVertexType != TYPE_NONORIENTED_VERTEX && oppositeVertexType != thisVertexType){
                return true;
            }
        }
        return anyEdges;
    }

    public boolean evaluateVertexTypeConditionFull(OrigamiModel origamiModel){
        for (OriVertex v : origamiModel.getVertices()) {
            if (!evaluateSingleVertexTypeCondition(v)){
                return false;
            }
        }
        return true;
    }

    public boolean evaluateSingleVertexEdgeCountCondition(OriVertex v){
        int[] edgeTypeCount = {0, 0};
        int mvDifference;
        for(OriEdge e: v.edges){
            if(e.type == OriLine.TYPE_CUT){
                return true;
            }
            if(e.type == OriLine.TYPE_RIDGE){
                edgeTypeCount[0]++;
            }
            if(e.type == OriLine.TYPE_VALLEY){
                edgeTypeCount[1]++;
            }
        }
        mvDifference = Math.abs(edgeTypeCount[0]-edgeTypeCount[1]);
        return mvDifference == 0 || mvDifference == 2;
    }

    public boolean evaluateVertexEdgeCountConditionFull(OrigamiModel origamiModel){
        for (OriVertex v: origamiModel.getVertices()){
            if (!evaluateSingleVertexEdgeCountCondition(v)){
                return false;
            }
        }
        return true;
    }

    public double roundOff(double d){
        int precision = 14;
        double factor = Math.pow(10, precision);
        return Math.round(d * factor)/factor;

    }

    private double getAngle(OriEdge e1, OriEdge e2){
        OriVertex v = e1.intersectionVertex(e2);
        Vector2d preP = new Vector2d(e1.oppositeVertex(v).p);
        Vector2d nxtP = new Vector2d(e2.oppositeVertex(v).p);

        nxtP.sub(v.p);
        preP.sub(v.p);

        return roundOff(preP.angle(nxtP));
    }

    public boolean evaluateSingleVertexAngleCondition(OriVertex v){
        boolean isOk = true;
        double rightAngle = roundOff(Math.PI/2);
        for (int i = 0; i < v.edges.size(); i++) {
            int ePrevIdx = i-1;
            if (ePrevIdx < 0){
                ePrevIdx += v.edges.size();
            }
            OriEdge e1 = v.edges.get(i);
            OriEdge e2 = v.edges.get((i + 1) % v.edges.size());
            OriEdge eNext = v.edges.get((i + 2) % v.edges.size());
            OriEdge ePrev = v.edges.get(ePrevIdx);
            if (e1.type == OriLine.TYPE_CUT || e2.type == OriLine.TYPE_CUT) {
                break;
            }

            double theta = getAngle(e1, e2);
            
            if(theta < rightAngle && e1.type == e2.type){
                isOk = false;
            }

            if(theta > rightAngle && e1.type != e2.type){
                isOk = false;
            }

            // TODO:
            // if(theta == rightAngle)...
        }
        return isOk;
    }

    public boolean evaluateVertexAngleConditionFull(OrigamiModel origamiModel){
        /*
         * At each vertex, adjacent edges meeting at <90° are different.
         * Edges meeting at >90° are the same.
         */
        boolean isOk = true;
        for (OriVertex v: origamiModel.getVertices()){
            if(!evaluateSingleVertexAngleCondition(v)){
                isOk = false;
            }
        }
        return isOk;
    }

    public boolean evaluateSingleFaceEdgeCondition(OriFace f){
        /*
         * Face condition:
         *  All faces must have both mountain and valley folds,
         *  unless the vertices of the face are both edge and internal.
         *  this is to allow degree 4 with faces on the edge that only
         *  have mountains/valleys.
         */
        int[] edgeTypeCount = {0, 0, 0};
        int[] vertexTypeCount = {0, 0};
        for(OriHalfedge he: f.halfedges){
            if(getVertexType(he.vertex) == TYPE_EDGE_VERTEX){
                vertexTypeCount[0]++;
            }else{
                vertexTypeCount[1]++;
            }
            if (vertexTypeCount[0] > 0 && vertexTypeCount[1] > 0){
                // Mixed vertex-type face, does not apply.
                return true;
            }
            if(he.edge.type == OriLine.TYPE_RIDGE){
                edgeTypeCount[0]++;
            }
            if(he.edge.type == OriLine.TYPE_VALLEY){
                edgeTypeCount[1]++;
            }
            if(he.edge.type == OriLine.TYPE_CUT){
                edgeTypeCount[2]++;
            }
        }

        if (edgeTypeCount[0] + edgeTypeCount[1] > 1){
            return edgeTypeCount[0] > 0 && edgeTypeCount[1] > 0;
        }
        
        return true;
    }

    public boolean evaluateFaceEdgeConditionFull(OrigamiModel origamiModel){
        /***
         * Each face with more than one crease edge has different crease edges.
         */
        for (OriFace f: origamiModel.getFaces()){
            if (!evaluateSingleFaceEdgeCondition(f)){
                return false;
            }
        }
        return true;
    }

    public boolean evaluate(OrigamiModel origamiModel){
        boolean vertexType = evaluateVertexTypeConditionFull(origamiModel);
        boolean vertexEdgeCount = evaluateVertexEdgeCountConditionFull(origamiModel);
        boolean faceEdge = evaluateFaceEdgeConditionFull(origamiModel);
        boolean vertexAngle = evaluateVertexAngleConditionFull(origamiModel);
        System.out.println("Vertex Type:  " + (vertexType ? "PASSED" : "FAILED"));
        System.out.println("Vertex Edge Count:  " + (vertexEdgeCount ? "PASSED" : "FAILED"));
        System.out.println("Face Edge:    " + (faceEdge ? "PASSED" : "FAILED"));
        System.out.println("Vertex Angle: " + (vertexAngle ? "PASSED" : "FAILED"));
        return vertexEdgeCount && vertexAngle && faceEdge;
    }
}