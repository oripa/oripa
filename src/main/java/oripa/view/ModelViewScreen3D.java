/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.view;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.event.*;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import oripa.ORIPA;
import oripa.geom.OriFace;
import oripa.geom.OriHalfedge;

class J3DFace {

    OriFace oriFace;
    Shape3D frontShape3D;
    Shape3D backShape3D;
    boolean bSelected;

    J3DFace(OriFace f) {
        oriFace = f;
        bSelected = false;
    }
}

public class ModelViewScreen3D extends Canvas3D implements MouseListener, MouseMotionListener, MouseWheelListener,
        ComponentListener {

    ArrayList<J3DFace> faces = new ArrayList<>();
    TransformGroup objTrans = new TransformGroup();
    Appearance mainAppearance = new Appearance();
    SimpleUniverse universe;
    BranchGroup objRoot = new BranchGroup();
    BranchGroup scene;
    Color3f faceColorFront = new Color3f(1.0f, 0.8f, 0.8f);
    Color3f faceColorBack = new Color3f(0.8f, 0.8f, 1.0f);
    Color3f faceColorSelected = new Color3f(Color.RED);

    public ModelViewScreen3D(GraphicsConfiguration config) {
        super(config);

        scene = createSceneGraph();


        PolygonAttributes pa = new PolygonAttributes(PolygonAttributes.POLYGON_FILL,
                PolygonAttributes.CULL_BACK, // Types of culling
                0.1f); // Polygon offset 
        mainAppearance.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
        mainAppearance.setPolygonAttributes(pa);

        universe = new SimpleUniverse(this);
        universe.getViewingPlatform().setNominalViewingTransform();

    }

    public void setModel() {
        faces.clear();
        BranchGroup faceBG = new BranchGroup();
        BranchGroup lineBG = new BranchGroup();

        objTrans.addChild(faceBG);
        objTrans.addChild(lineBG);

        int fCount = 0;
        Point2d maxPoint = new Point2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
        Point2d minPoint = new Point2d(Double.MAX_VALUE, Double.MAX_VALUE);
        for (OriFace face : ORIPA.doc.sortedFaces) {
            for (OriHalfedge he : face.halfedges) {
                maxPoint.x = Math.max(maxPoint.x, he.positionForDisplay.x);
                maxPoint.y = Math.max(maxPoint.y, he.positionForDisplay.y);
                minPoint.x = Math.min(minPoint.x, he.positionForDisplay.x);
                minPoint.y = Math.min(minPoint.y, he.positionForDisplay.y);
            }
        }
        Point3d centerP = new Point3d();
        centerP.set((maxPoint.x + minPoint.x) * 0.5, (maxPoint.y + minPoint.y) * 0.5, 0);
        double size = maxPoint.distance(minPoint);

        LineAttributes lineAttributes =
                new LineAttributes(1.0f, // Line thickness
                LineAttributes.PATTERN_SOLID, // Line type
                false); // Whether to handle anti-aliasing

        LineAttributes lineAttributesBold =
                new LineAttributes(3.0f, // Line thickness
                LineAttributes.PATTERN_SOLID, // Line type
                false); // Whether to handle anti-aliasing

        for (OriFace face : ORIPA.doc.sortedFaces) {
            J3DFace j3dFace = new J3DFace(face);
            faces.add(j3dFace);

            double z = (fCount - ORIPA.doc.sortedFaces.size() * 0.5) * (-10);
            OriHalfedge startHe = face.halfedges.get(0);
            Point3d[] p = new Point3d[3];
            p[0] = new Point3d(startHe.positionForDisplay.x, startHe.positionForDisplay.y, z);
            p[0].sub(centerP);
            p[0].scale(1.0 / size);

            TriangleArray faceGeometryFront = new TriangleArray(3 * (face.halfedges.size() - 2), 
                    GeometryArray.COORDINATES | GeometryArray.COLOR_3);
            TriangleArray faceGeometryBack = new TriangleArray(3 * (face.halfedges.size() - 2), 
                    GeometryArray.COORDINATES | GeometryArray.COLOR_3);

            for (int i = 0; i < face.halfedges.size(); i++) {
                OriHalfedge he = face.halfedges.get(i);

                p[1] = new Point3d(he.positionForDisplay.x, he.positionForDisplay.y, z);
                p[2] = new Point3d(he.next.positionForDisplay.x, he.next.positionForDisplay.y, z);

                p[1].sub(centerP);
                p[1].scale(1.0 / size);
                p[2].sub(centerP);
                p[2].scale(1.0 / size);

                // Outline
                LineArray lineGeometry = new LineArray(2, GeometryArray.COORDINATES | 
                        GeometryArray.COLOR_3);
                lineGeometry.setCoordinate(0, p[1]);
                lineGeometry.setCoordinate(1, p[2]);
                Appearance lineAppearance = new Appearance();
                lineAppearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);
                if (he.pair == null) {
                    lineAppearance.setLineAttributes(lineAttributesBold);
                    lineGeometry.setColor(0, new Color3f(Color.RED));
                    lineGeometry.setColor(1, new Color3f(Color.RED));
                } else {
                    lineAppearance.setLineAttributes(lineAttributes);
                    lineGeometry.setColor(0, new Color3f(Color.BLACK));
                    lineGeometry.setColor(1, new Color3f(Color.BLACK));
                }
                Shape3D lines = new Shape3D(lineGeometry, lineAppearance);
                lines.setUserData("line" + fCount);

                lineBG.addChild(lines);
                if (i == 0 || i == face.halfedges.size() - 1) {
                    continue;
                }

                for (int j = 0; j < 3; j++) {
                    int k = (i - 1) * 3 + j;
                    faceGeometryFront.setCoordinate(k, p[j]);
                    faceGeometryBack.setCoordinate(k, p[2 - j]);
                    faceGeometryFront.setColor(k, faceColorFront);
                    faceGeometryBack.setColor(k, faceColorBack);
                }
            }
            faceGeometryFront.setCapability(Geometry.ALLOW_INTERSECT);
            faceGeometryBack.setCapability(Geometry.ALLOW_INTERSECT);
            faceGeometryFront.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
            faceGeometryBack.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
            faceGeometryFront.setCapability(GeometryArray.ALLOW_COUNT_READ);
            faceGeometryBack.setCapability(GeometryArray.ALLOW_COUNT_READ);

            Shape3D shapeFront = new Shape3D(faceGeometryFront, mainAppearance);
            Shape3D shapeBack = new Shape3D(faceGeometryBack, mainAppearance);
            j3dFace.frontShape3D = shapeFront;
            j3dFace.backShape3D = shapeBack;
            shapeFront.setUserData(j3dFace);
            shapeBack.setUserData(j3dFace);
            shapeFront.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            shapeBack.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            faceBG.addChild(shapeFront);
            faceBG.addChild(shapeBack);

            fCount++;
        }

        BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
        SimplePicking picker =
                new SimplePicking(faceBG/*
                 * objRoot
                 */, this, bounds);

        picker.setupCallback(new SimplePickingCallback() {

            @Override
            public void picked(int type, Node node) {
                if (node == null) {
                    return;
                }
                Shape3D sp = (Shape3D) node;
                J3DFace face = (J3DFace) sp.getUserData();
                setSelected(face);
            }
        });
        objRoot.addChild(picker);

        universe.addBranchGraph(scene);

    }

    private void setSelected(J3DFace face) {
        for (J3DFace f : faces) {
            setColor(f.frontShape3D, faceColorFront);
            setColor(f.backShape3D, faceColorBack);
        }

        setColor(face.backShape3D, faceColorSelected);
        setColor(face.frontShape3D, faceColorSelected);

        for (OriHalfedge he : face.oriFace.halfedges) {
            if (he.pair == null) {
                continue;
            }
            for (J3DFace f : faces) {
                if (f == face) {
                    continue;
                }
                if (f.oriFace == he.pair.face) {
                    setColor(f.backShape3D, new Color3f(0.0f, 0.5f, 0.0f));
                    setColor(f.frontShape3D, new Color3f(0.0f, 0.5f, 0.0f));
                }
            }

        }
    }

    private void setColor(Shape3D sp, Color3f color) {
        for (int i = 0; i < sp.numGeometries(); i++) {
            TriangleArray ta = (TriangleArray) sp.getGeometry(i);
            int vNum = ta.getVertexCount();
            for (int j = 0; j < vNum; j++) {
                ta.setColor(j, color);
            }
        }
    }

    private BranchGroup createSceneGraph() {
        Background background = new Background();
        background.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        background.setApplicationBounds(new BoundingSphere(new Point3d(), 10000.0));
        objRoot.addChild(background);

        {
            BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
            DirectionalLight dlight =
                    new DirectionalLight(new Color3f(1.0f, 0.0f, 0.0f),
                    new Vector3f(0.87f, 0.0f, -0.5f));
            dlight.setInfluencingBounds(bounds);
            objRoot.addChild(dlight);

            AmbientLight alight = new AmbientLight();
            alight.setInfluencingBounds(bounds);
            objRoot.addChild(alight);
        }


        objRoot.addChild(objTrans);

        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        MouseRotate rotat = new MouseRotate(objTrans);
        MouseTranslate trans = new MouseTranslate(objTrans);
        MouseZoom zoom = new MouseZoom(objTrans);

        BoundingSphere bounds = new BoundingSphere();
        bounds.setRadius(1.0);
        rotat.setSchedulingBounds(bounds);
        trans.setSchedulingBounds(bounds);
        zoom.setSchedulingBounds(bounds);

        objTrans.addChild(rotat);
        objTrans.addChild(trans);
        objTrans.addChild(zoom);
        return objRoot;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }
}

interface SimplePickingCallback {

    void picked(int nodeType, Node node);
}

class SimplePicking extends PickMouseBehavior {

    protected SimplePickingCallback callback = null;

    public SimplePicking(BranchGroup root, Canvas3D canvas, Bounds bounds) {
        super(canvas, root, bounds);

        this.setSchedulingBounds(bounds);
        System.out.println("tolerance" + getTolerance());
        this.setTolerance(0.0f);
        setMode(PickCanvas.GEOMETRY);
    }

    public void setupCallback(SimplePickingCallback callback) {
        this.callback = callback;
    }

    @Override
    public void updateScene(int xpos, int ypos) {
        pickCanvas.setShapeLocation(xpos, ypos);
        PickResult res = pickCanvas.pickClosest();
        if (res != null) {
            callback.picked(PickResult.SHAPE3D, res.getNode(PickResult.SHAPE3D));
        }
    }
}
