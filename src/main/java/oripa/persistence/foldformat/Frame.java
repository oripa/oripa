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
package oripa.persistence.foldformat;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author OUCHI Koji
 *
 */
public class Frame {
	@SerializedName("vertices_coords")
	@Expose
	private List<List<Double>> verticesCoords = null;

	@SerializedName("faces_vertices")
	@Expose
	private List<List<Integer>> facesVertices = null;

	@SerializedName("edges_vertices")
	@Expose
	private List<List<Integer>> edgesVertices = null;

	@SerializedName("edges_assignment")
	@Expose
	private List<String> edgesAssignment = null;

	@SerializedName("faceOrders")
	@Expose
	private List<List<Integer>> faceOrders = null;

	@SerializedName("frame_parent")
	@Expose
	private Integer frameParent = null;

	@SerializedName("frame_inherit")
	@Expose
	private Boolean frameInherit = null;

	public List<List<Double>> getVerticesCoords() {
		return verticesCoords;
	}

	public void setVerticesCoords(final List<List<Double>> verticesCoords) {
		this.verticesCoords = verticesCoords;
	}

	public List<List<Integer>> getFacesVertices() {
		return facesVertices;
	}

	public void setFacesVertices(final List<List<Integer>> facesVertices) {
		this.facesVertices = facesVertices;
	}

	public List<List<Integer>> getEdgesVertices() {
		return edgesVertices;
	}

	public void setEdgesVertices(final List<List<Integer>> edgesVertices) {
		this.edgesVertices = edgesVertices;
	}

	public List<String> getEdgesAssignment() {
		return edgesAssignment;
	}

	public void setEdgesAssignment(final List<String> edgesAssignment) {
		this.edgesAssignment = edgesAssignment;
	}

	public List<List<Integer>> getFaceOrders() {
		return faceOrders;
	}

	public void setFaceOrders(final List<List<Integer>> faceOrders) {
		this.faceOrders = faceOrders;
	}

	public Integer getFrameParent() {
		return frameParent;
	}

	public void setFrameParent(final Integer frameParent) {
		this.frameParent = frameParent;
	}

	public Boolean getFrameInherit() {
		return frameInherit;
	}

	public void setFrameInherit(final Boolean frameInherit) {
		this.frameInherit = frameInherit;
	}

}
