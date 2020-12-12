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
package oripa.file;

import java.util.Collection;

/**
 * create {@link oripa.file.InitData} if all parameters are set
 *
 * @author OUCHI Koji
 *
 */
public class InitDataBuilder {

	public String lastUsedFile = ""; // dead property
	public String[] MRUFiles = null;

	private Boolean zeroLineWidth = null;
	private Boolean mvLineVisible = null;
	private Boolean auxLineVisible = null;
	private Boolean vertexVisible = null;

	public InitDataBuilder() {
	}

	public InitDataBuilder setLastUsedFile(final String filePath) {
		lastUsedFile = filePath;
		return this;
	}

	public InitDataBuilder setMRUFiles(final Collection<String> mru) {
		MRUFiles = mru.toArray(new String[mru.size()]);
		return this;
	}

	public InitDataBuilder setZeroLineWidth(final boolean zeroLineWidth) {
		this.zeroLineWidth = Boolean.valueOf(zeroLineWidth);
		return this;
	}

	public InitDataBuilder setMVLineVisible(final boolean mvLineVisible) {
		this.mvLineVisible = Boolean.valueOf(mvLineVisible);
		return this;
	}

	public InitDataBuilder setAuxLineVisible(final boolean auxLineVisible) {
		this.auxLineVisible = Boolean.valueOf(auxLineVisible);
		return this;
	}

	public InitDataBuilder setVertexVisible(final boolean vertexVisible) {
		this.vertexVisible = Boolean.valueOf(vertexVisible);
		return this;
	}

	private void assertValueIsSet(final Object obj, final String displayName) {
		if (obj == null) {
			throw new IllegalStateException(displayName + " should have some value.");
		}
	}

	/**
	 * build object
	 *
	 * @return constructed InitData Object
	 * @throws IllegalStateException
	 *             if some the data fields is not set
	 */
	public InitData get() throws IllegalStateException {
		assertValueIsSet(MRUFiles, "MRUFiles");

		assertValueIsSet(zeroLineWidth, "zeroLineWidth");

		assertValueIsSet(mvLineVisible, "mvLineVisible");
		assertValueIsSet(auxLineVisible, "auxLineVisible");
		assertValueIsSet(vertexVisible, "vertexVisible");

		var initData = new InitData();

		initData.setLastUsedFile(lastUsedFile);
		initData.setMRUFiles(MRUFiles);

		initData.setZeroLineWidth(zeroLineWidth);

		initData.setMvLineVisible(mvLineVisible);
		initData.setAuxLineVisible(auxLineVisible);
		initData.setVertexVisible(vertexVisible);

		return initData;
	}
}
