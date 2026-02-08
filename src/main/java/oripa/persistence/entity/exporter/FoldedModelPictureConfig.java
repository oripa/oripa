/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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
package oripa.persistence.entity.exporter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import oripa.domain.fold.halfedge.OriVertex;
import oripa.renderer.estimation.DistortionMethod;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelPictureConfig {
    private double eps;

    private boolean faceFlipped;
    private boolean fillFaces;
    private boolean drawEdges;
    private boolean ambientOcclusion;
    private Color frontColor;
    private Color backColor;

    private double rotateAngle;

    private DistortionMethod distortionMethod;
    private Vector2d distortionParameter;
    private Map<OriVertex, Integer> vertexDepths;

    public double getEps() {
        return eps;
    }

    public FoldedModelPictureConfig setEps(final double eps) {
        this.eps = eps;
        return this;
    }

    public boolean isFaceFlipped() {
        return faceFlipped;
    }

    public FoldedModelPictureConfig setFaceOrderFlipped(final boolean faceOrderFlipped) {
        this.faceFlipped = faceOrderFlipped;
        return this;
    }

    public boolean isFillFaces() {
        return fillFaces;
    }

    public FoldedModelPictureConfig setFillFaces(final boolean fillFaces) {
        this.fillFaces = fillFaces;
        return this;
    }

    public boolean isDrawEdges() {
        return drawEdges;
    }

    public FoldedModelPictureConfig setDrawEdges(final boolean drawEdges) {
        this.drawEdges = drawEdges;
        return this;
    }

    public boolean isAmbientOcclusion() {
        return ambientOcclusion;
    }

    public FoldedModelPictureConfig setAmbientOcclusion(final boolean ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
        return this;
    }

    public FoldedModelPictureConfig setColors(final Color front, final Color back) {
        frontColor = front;
        backColor = back;
        return this;
    }

    public Color getFrontColor() {
        return frontColor;
    }

    public FoldedModelPictureConfig setFrontColor(final Color frontColor) {
        this.frontColor = frontColor;
        return this;
    }

    public Color getBackColor() {
        return backColor;
    }

    public FoldedModelPictureConfig setBackColor(final Color backColor) {
        this.backColor = backColor;
        return this;
    }

    public double getRotateAngle() {
        return rotateAngle;
    }

    public FoldedModelPictureConfig setRotateAngle(final double rotateAngle) {
        this.rotateAngle = rotateAngle;
        return this;
    }

    /**
     *
     * @return given value. NONE if any value is given or null is set.
     */
    public DistortionMethod getDistortionMethod() {
        return distortionMethod == null ? DistortionMethod.NONE : distortionMethod;
    }

    public FoldedModelPictureConfig setDistortionMethod(final DistortionMethod distortionMethod) {
        this.distortionMethod = distortionMethod;
        return this;
    }

    /**
     *
     * @return given parameter. null if any parameter is given.
     */
    public Vector2d getDistortionParameter() {
        return distortionParameter;
    }

    /**
     * This is an optional property. This can be null if distortion method is
     * {@link DistortionMethod#NONE}.
     *
     * @param distortionParameter
     * @return
     */
    public FoldedModelPictureConfig setDistortionParameter(final Vector2d distortionParameter) {
        this.distortionParameter = distortionParameter;
        return this;
    }

    /**
     *
     * @return given map. empty map if any map is given or null is set.
     */
    public Map<OriVertex, Integer> getVertexDepths() {
        return vertexDepths == null ? new HashMap<>() : vertexDepths;
    }

    /**
     * This is an optional property. This can be null if distortion method is
     * not {@link DistortionMethod#DEPTH}.
     *
     * @param vertexDepths
     * @return
     */
    public FoldedModelPictureConfig setVertexDepths(final Map<OriVertex, Integer> vertexDepths) {
        this.vertexDepths = vertexDepths;
        return this;
    }

}
