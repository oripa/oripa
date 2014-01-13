package oripa.corrugation;

import java.util.ArrayList;

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

    public boolean evaluateSingleVertexCondition(OriVertex v){
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

    public boolean evaluateVertexConditionFull(OrigamiModel origamiModel){
        for (OriVertex v : origamiModel.getVertices()) {
            if (!evaluateSingleVertexCondition(v)){
                return false;
            }
        }
        return true;
    }

    public boolean evaluateSingleFaceCondition(OriFace f){
        int[] edgeTypeCount = {0, 0, 0};
        for(OriHalfedge he: f.halfedges){
            if(he.edge.type == OriLine.TYPE_RIDGE){
                edgeTypeCount[0]++;
            }
            if(he.edge.type == OriLine.TYPE_VALLEY){
                edgeTypeCount[1]++;
            }
            if(he.edge.type == OriLine.TYPE_CUT){
                return true;
            }
        }
        return edgeTypeCount[0] >= 1 && edgeTypeCount[1] >= 1;
    }

    public boolean evaluateFaceConditionFull(OrigamiModel origamiModel){
        for (OriFace f: origamiModel.getFaces()){
            if (!evaluateSingleFaceCondition(f)){
                return false;
            }
        }
        return true;
    }

    public boolean evaluate(OrigamiModel origamiModel){
        return evaluateVertexConditionFull(origamiModel) && evaluateFaceConditionFull(origamiModel);
    }
}