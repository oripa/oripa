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

	private boolean faceOrderFlipped;
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

	public void setEps(final double eps) {
		this.eps = eps;
	}

	public boolean isFaceOrderFlipped() {
		return faceOrderFlipped;
	}

	public void setFaceOrderFlipped(final boolean faceOrderFlipped) {
		this.faceOrderFlipped = faceOrderFlipped;
	}

	public boolean isFillFaces() {
		return fillFaces;
	}

	public void setFillFaces(final boolean fillFaces) {
		this.fillFaces = fillFaces;
	}

	public boolean isDrawEdges() {
		return drawEdges;
	}

	public void setDrawEdges(final boolean drawEdges) {
		this.drawEdges = drawEdges;
	}

	public boolean isAmbientOcclusion() {
		return ambientOcclusion;
	}

	public void setAmbientOcclusion(final boolean ambientOcclusion) {
		this.ambientOcclusion = ambientOcclusion;
	}

	public void setColors(final Color front, final Color back) {
		frontColor = front;
		backColor = back;
	}

	public Color getFrontColor() {
		return frontColor;
	}

	public void setFrontColor(final Color frontColor) {
		this.frontColor = frontColor;
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(final Color backColor) {
		this.backColor = backColor;
	}

	public double getRotateAngle() {
		return rotateAngle;
	}

	public void setRotateAngle(final double rotateAngle) {
		this.rotateAngle = rotateAngle;
	}

	public DistortionMethod getDistortionMethod() {
		return distortionMethod;
	}

	public void setDistortionMethod(final DistortionMethod distortionMethod) {
		this.distortionMethod = distortionMethod;
	}

	public Vector2d getDistortionParameter() {
		return distortionParameter;
	}

	public void setDistortionParameter(final Vector2d distortionParameter) {
		this.distortionParameter = distortionParameter;
	}

	public Map<OriVertex, Integer> getVertexDepths() {
		return vertexDepths;
	}

	public void setVertexDepths(final Map<OriVertex, Integer> vertexDepths) {
		this.vertexDepths = vertexDepths;
	}

}
