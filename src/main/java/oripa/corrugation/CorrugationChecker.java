package oripa.corrugation;

import java.util.ArrayList;

import oripa.fold.OrigamiModel;
import oripa.fold.OriEdge;
import oripa.fold.OriVertex;
import oripa.value.OriLine;

public class CorrugationChecker {

    public int getVertexType(OriVertex v){
        int[] edgeTypeCount = {0, 0};
        for (OriEdge e: v.edges){
            if (e.type == OriLine.TYPE_CUT){
                return 0;
            }
            if (e.type == OriLine.TYPE_RIDGE){
                edgeTypeCount[0]++;
            }
            if (e.type == OriLine.TYPE_VALLEY){
                edgeTypeCount[1]++;
            }
        }

        if(edgeTypeCount[0] < edgeTypeCount[1]){
            return -1;
        }

        if(edgeTypeCount[0] > edgeTypeCount[1]){
            return 1;
        }

        return 0;
    }

    public boolean evaluateSingleVertexCondition(OriVertex v){
        OriVertex oppositeVertex = v;
        ArrayList<Integer> vertexTypes;
        int thisVertexType = getVertexType(v);
        int oppositeVertexType;
        boolean allNonOriented = true;
        if (thisVertexType == 0){
            return true;
        }

        for (OriEdge e: v.edges){
            if(e.sv == v){
                oppositeVertex = e.ev;
            }else{
                oppositeVertex = e.sv;
            }
            oppositeVertexType = getVertexType(oppositeVertex);
            if(oppositeVertexType != 0){
                allNonOriented = false;
            }
            if(oppositeVertexType != 0 && oppositeVertexType != thisVertexType){
                return true;
            }
        }
        return allNonOriented;
    }

    public boolean evaluateVertexConditionFull(OrigamiModel origamiModel){
        for (OriVertex v : origamiModel.getVertices()) {
            if (!evaluateSingleVertexCondition(v)){
                return false;
            }
        }
        return true;
    }

    public boolean evaluate(OrigamiModel origamiModel){
        return evaluateVertexConditionFull(origamiModel);
    }
}