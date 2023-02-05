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
public abstract class FOLDFormat extends Frame {
	@SerializedName("file_spec")
	@Expose
	private double fileSpec = 1.1;

	@SerializedName("file_creator")
	@Expose
	private String fileCreator = "ORIPA";

	@SerializedName("file_author")
	@Expose
	private String fileAuthor;

	@SerializedName("file_classes")
	@Expose
	private List<String> fileClasses;

	@SerializedName("file_frames")
	@Expose
	private List<Frame> fileFrames = null;

	public double getFileSpec() {
		return fileSpec;
	}

	public void setFileSpec(final double fileSpec) {
		this.fileSpec = fileSpec;
	}

	public String getFileCreator() {
		return fileCreator;
	}

	public void setFileCreator(final String fileCreator) {
		this.fileCreator = fileCreator;
	}

	public String getFileAuthor() {
		return fileAuthor;
	}

	public void setFileAuthor(final String fileAuthor) {
		this.fileAuthor = fileAuthor;
	}

	public List<String> getFileClasses() {
		return fileClasses;
	}

	public void setFileClasses(final List<String> fileClasses) {
		this.fileClasses = fileClasses;
	}

	public List<Frame> getFileFrames() {
		return fileFrames;
	}

	public void setFileFrames(final List<Frame> fileFrames) {
		this.fileFrames = fileFrames;
	}

	/**
	 *
	 * @param index
	 *            0 points the key frame (this instance), and 1 or larger points
	 *            child frame.
	 * @return frame at the index. if the frame inherits other frame, the
	 *         returned frame is the result of merging the frame and ancestors.
	 */
	public Frame getFrame(final int index) {
		if (index == 0) {
			return this;
		}
		var frame = fileFrames.get(index - 1);
		if (frame.getFrameInherit()) {
			var parent = getFrame(frame.getFrameParent());
			return merge(parent, frame);
		}

		return frame;
	}

	private Frame merge(final Frame parent, final Frame child) {
		var frame = new Frame();

		frame.setFrameClasses(parent.getFrameClasses());
		frame.setFrameAttributes(parent.getFrameAttributes());
		frame.setFrameTitle(parent.getFrameTitle());
		frame.setFrameDescription(parent.getFrameDescription());

		if (child.getFrameClasses() != null) {
			frame.setFrameClasses(child.getFrameClasses());
		}

		if (child.getFrameAttributes() != null) {
			frame.setFrameAttributes(child.getFrameAttributes());
		}

		if (child.getFrameTitle() != null) {
			frame.setFrameTitle(child.getFrameTitle());
		}

		if (child.getFrameDescription() != null) {
			frame.setFrameDescription(child.getFrameDescription());
		}

		frame.setEdgesAssignment(parent.getEdgesAssignment());
		frame.setEdgesVertices(parent.getEdgesVertices());
		frame.setVerticesCoords(parent.getVerticesCoords());
		frame.setFaceOrders(parent.getFaceOrders());
		frame.setFacesVertices(parent.getFacesVertices());

		if (child.getEdgesAssignment() != null) {
			frame.setEdgesAssignment(child.getEdgesAssignment());
		}

		if (child.getEdgesVertices() != null) {
			frame.setEdgesVertices(child.getEdgesVertices());
		}

		if (child.getVerticesCoords() != null) {
			frame.setVerticesCoords(child.getVerticesCoords());
		}

		if (child.getFaceOrders() != null) {
			frame.setFaceOrders(child.getFaceOrders());
		}

		if (child.getFacesVertices() != null) {
			frame.setFacesVertices(child.getFacesVertices());
		}

		return frame;
	}

}
