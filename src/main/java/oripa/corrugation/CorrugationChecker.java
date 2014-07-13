package oripa.corrugation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.vecmath.Vector2d;

import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

import oripa.fold.OrigamiModel;
import oripa.fold.OriEdge;
import oripa.fold.OriFace;
import oripa.fold.OriHalfedge;
import oripa.fold.OriVertex;
import oripa.value.OriLine;
import oripa.corrugation.EdgePair;

public class CorrugationChecker {

    final public static int TYPE_EDGE_VERTEX = 0;
    final public static int TYPE_NONORIENTED_VERTEX = 1;
    final public static int TYPE_VALLEY_VERTEX = 2;
    final public static int TYPE_MOUNTAIN_VERTEX = 3;

    private LinkedList<OriVertex> vertexTypeConditionFailures;
    private LinkedList<OriFace> faceConditionFailures;
    private LinkedList<OriVertex> vertexEdgeCountConditionFailures; // really just Maekawa's condition violations
    private LinkedList<EdgePair> vertexAngleConditionFailures;
    
    public CorrugationChecker() {
    	this.vertexTypeConditionFailures = new LinkedList<OriVertex>();
    	this.vertexEdgeCountConditionFailures = new LinkedList<OriVertex>();
    	this.faceConditionFailures = new LinkedList<OriFace>();
    	this.vertexAngleConditionFailures = new LinkedList<EdgePair>();
    }

    public LinkedList<OriVertex> getVertexTypeFailures() {
    	return vertexTypeConditionFailures;
    }
    public LinkedList<OriFace> getFaceFailures(){
    	return faceConditionFailures;
    }
    public LinkedList<OriVertex> getVertexEdgeCountFailures(){
    	return vertexEdgeCountConditionFailures;
    }
    public LinkedList<EdgePair> getVertexAngleFailures(){
    	return vertexAngleConditionFailures;
    }
    
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
                this.vertexTypeConditionFailures.add(v);
            }
        }
        return this.vertexTypeConditionFailures.isEmpty();
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
        /*
         * Modified Maekawa's theorem: |M-V| = {0,2} to allow radial vertices.
         */
        for (OriVertex v: origamiModel.getVertices()){
            if (!evaluateSingleVertexEdgeCountCondition(v)){
                this.vertexEdgeCountConditionFailures.add(v);
            }
        }
        return this.vertexEdgeCountConditionFailures.isEmpty();
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
        double EPS = 1e-6;
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
                continue;
            }

            double theta = getAngle(e1, e2);
            
            if(theta < rightAngle - EPS && e1.type == e2.type){
            	EdgePair ep = new EdgePair(e1, e2);
            	this.vertexAngleConditionFailures.add(ep);
                isOk = false;
            }

            if(theta > rightAngle + EPS && e1.type != e2.type){
            	EdgePair ep = new EdgePair(e1, e2);
            	this.vertexAngleConditionFailures.add(ep);
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
            return (edgeTypeCount[0] > 0 && edgeTypeCount[1] > 0);
        }
        
        return true;
    }

    public <V, E> Map<Integer, Set<V>> getColoring(Graph<V, E> graph){
        NeighborIndex<V, E> neighborindex = new NeighborIndex<V, E>(graph);
        Set<V> front = new HashSet<V>();
        Set<V> back = new HashSet<V>();
        GraphIterator<V, E> iterator = new DepthFirstIterator<V, E>(graph);
        while (iterator.hasNext()){
            V f = iterator.next();
            Set<V> neighbors = neighborindex.neighborsOf(f);
            if (front.contains(f)){
                back.addAll(neighbors);
            }else if(back.contains(f)){
                front.addAll(neighbors);
            }else{
                boolean added = false;
                for (V n: neighbors){
                    if (front.contains(n)){
                        added = true;
                        front.addAll(neighbors);
                        back.add(f);
                        break;
                    }else if(back.contains(n)){
                        added = true;
                        back.addAll(neighbors);
                        front.add(f);
                        break;
                    }
                }
                if (!added){
                    System.out.println("Adding initial face");
                    front.add(f);
                    back.addAll(neighbors);
                }
            }
        }
        Map<Integer, Set<V>> coloring = new HashMap<Integer, Set<V>>();
        coloring.put(0, front);
        coloring.put(1, back);
        return coloring;
        // return ChromaticNumber.findGreedyColoredGroups(undirectedFaceGraph);
    }

    public DefaultDirectedGraph<OriFace, DefaultEdge> getFaceGraph(OrigamiModel origamiModel){
        SimpleGraph<OriFace, DefaultEdge> undirectedFaceGraph = new SimpleGraph<OriFace, DefaultEdge>(DefaultEdge.class);
        DefaultDirectedGraph<OriFace, DefaultEdge> faceGraph = new DefaultDirectedGraph<OriFace, DefaultEdge>(DefaultEdge.class);
        for (OriFace f: origamiModel.getFaces()){
            undirectedFaceGraph.addVertex(f);
            faceGraph.addVertex(f);
        }

        for (OriFace f: origamiModel.getFaces()){
            for (OriFace n: f.getFaceNeighbors()){
                undirectedFaceGraph.addEdge(f, n);
            }
        }

        Map<Integer, Set<OriFace>> graphColoring = getColoring(undirectedFaceGraph);
        if (graphColoring.size() > 2){
            for(int i = 2; i < graphColoring.size(); i++){
                faceConditionFailures.addAll(graphColoring.get(i));
            }
        }
        Set<OriFace> frontFaces = graphColoring.get(0);
        Set<OriFace> backFaces = graphColoring.get(1);

        for (DefaultEdge e: undirectedFaceGraph.edgeSet()){
            OriFace source = undirectedFaceGraph.getEdgeSource(e);
            OriFace target = undirectedFaceGraph.getEdgeTarget(e);
            if (
                    (frontFaces.contains(source) && source.getNeighborEdgeType(target) == OriLine.TYPE_VALLEY) ||
                    (backFaces.contains(source) && source.getNeighborEdgeType(target) == OriLine.TYPE_RIDGE)){
                faceGraph.addEdge(source, target);
            }else if (
                    (frontFaces.contains(target) && target.getNeighborEdgeType(source) == OriLine.TYPE_VALLEY) ||
                    (backFaces.contains(target) && target.getNeighborEdgeType(source) == OriLine.TYPE_RIDGE)){
                faceGraph.addEdge(target, source);
            }else{
                System.out.println("Invalid edge" + e);
            }
        }
        return faceGraph;
    }

    public boolean evaluateFaceEdgeConditionFull(OrigamiModel origamiModel){
        /***
         * Each face with more than one crease edge has different crease edges. In graph
         * theory terms, the directed graph of faces has no internal sources or sinks.
         */

        DefaultDirectedGraph<OriFace, DefaultEdge> faceGraph = getFaceGraph(origamiModel);

        for (OriFace f: origamiModel.getFaces()){
            if (
                f.isInternalFace() && (
                    faceGraph.incomingEdgesOf(f).size() == 0 ||
                    faceGraph.outgoingEdgesOf(f).size() == 0)){
                faceConditionFailures.add(f);
            }
        }
        return faceConditionFailures.isEmpty();
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