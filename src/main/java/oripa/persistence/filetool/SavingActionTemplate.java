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
package oripa.persistence.filetool;

import java.io.IOException;

/**
 * @author Koji
 *
 */
public class SavingActionTemplate<Data> extends AbstractSavingAction<Data> {
	private final Exporter<Data> exporter;

	public SavingActionTemplate(final Exporter<Data> exporter) {
		this.exporter = exporter;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.filetool.SavingAction#save(java.lang.String)
	 */
	@Override
	public boolean save(final Data data) throws IOException, IllegalArgumentException {
		return exporter.export(data, getPath());
	}

}
