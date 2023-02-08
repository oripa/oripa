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
	@SerializedName("frame_title")
	@Expose
	private String frameTitle;

	@SerializedName("frame_classes")
	@Expose
	private List<String> frameClasses;

	@SerializedName("frame_attributes")
	@Expose
	private List<String> frameAttributes;

	@SerializedName("frame_description")
	@Expose
	private String frameDescription;

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

	public String getFrameTitle() {
		return frameTitle;
	}

	public void setFrameTitle(final String frameTitle) {
		this.frameTitle = frameTitle;
	}

	public List<String> getFrameClasses() {
		return frameClasses;
	}

	public void setFrameClasses(final List<String> frameClasses) {
		this.frameClasses = frameClasses;
	}

	public List<String> getFrameAttributes() {
		return frameAttributes;
	}

	public void setFrameAttributes(final List<String> frameAttributes) {
		this.frameAttributes = frameAttributes;
	}

	public String getFrameDescription() {
		return frameDescription;
	}

	public void setFrameDescription(final String frameDescription) {
		this.frameDescription = frameDescription;
	}

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

	public boolean frameClassesContains(final String frameClass) {
		return getFrameClasses() == null || getFrameClasses().contains(frameClass);
	}

}
