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

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelSVGConfig {
	private double faceStrokeWidth = 2;
	private double precreaseStrokeWidth = 1;

	// don't serialize these.
	private transient String frontFillColorCode = "#B3B3B3";
	private transient String backFillColorCode = "#FFFFFF";

	public double getFaceStrokeWidth() {
		return faceStrokeWidth;
	}

	/**
	 *
	 * @param faceStrokeWidth
	 *            the unit is [px]
	 */
	public void setFaceStrokeWidth(final double faceStrokeWidth) {
		this.faceStrokeWidth = faceStrokeWidth;
	}

	public double getPrecreaseStrokeWidth() {
		return precreaseStrokeWidth;
	}

	/**
	 *
	 * @param precreaseStrokeWidth
	 *            the unit is [px]
	 */
	public void setPrecreaseStrokeWidth(final double precreaseStrokeWidth) {
		this.precreaseStrokeWidth = precreaseStrokeWidth;
	}

	public String getFrontFillColorCode() {
		return frontFillColorCode;
	}

	public void setFrontFillColorCode(final String frontFillColorCode) {
		this.frontFillColorCode = frontFillColorCode;
	}

	public String getBackFillColorCode() {
		return backFillColorCode;
	}

	public void setBackFillColorCode(final String backFillColorCode) {
		this.backFillColorCode = backFillColorCode;
	}

}
