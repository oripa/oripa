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
package oripa.application.model;

import java.awt.Component;
import java.io.IOException;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistent.entity.OrigamiModelFileTypeKey;
import oripa.persistent.filetool.FileAccessActionProvider;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileChooserCanceledException;
import oripa.persistent.filetool.FileChooserFactory;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelFileAccess {
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	private FileAccessSupportFilter<OrigamiModel> createFilter(final OrigamiModelFileTypeKey type) {
		FileAccessSupportFilter<OrigamiModel> filter = new FileAccessSupportFilter<OrigamiModel>(
				type,
				FileAccessSupportFilter.createDefaultDescription(
						type, resourceHolder.getString(
								ResourceKey.LABEL, StringID.ModelMenu.FILE_ID)));

		if (filter.getSavingAction() == null) {
			throw new RuntimeException("Wrong implementation");
		}

		return filter;
	}

	public void save(final OrigamiModelFileTypeKey type, final OrigamiModel origamiModel,
			final Component owner)
			throws IllegalArgumentException, IOException, FileChooserCanceledException {

		FileChooserFactory<OrigamiModel> chooserFactory = new FileChooserFactory<>();
		FileAccessActionProvider<OrigamiModel> chooser = chooserFactory.createChooser(null,
				createFilter(type));

		chooser.getActionForSavingFile(owner).save(origamiModel);
	}

}
