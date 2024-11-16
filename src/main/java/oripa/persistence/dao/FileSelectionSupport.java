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
package oripa.persistence.dao;

import java.util.function.Supplier;

import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.LoadingAction;
import oripa.persistence.filetool.SavingAction;

/**
 * @author OUCHI Koji
 *
 */
public class FileSelectionSupport<Data> implements Comparable<FileSelectionSupport<Data>> {
	private final FileAccessSupport<Data> fileAccessSupport;

	public FileSelectionSupport(final FileAccessSupport<Data> fileAccessSupport) {
		this.fileAccessSupport = fileAccessSupport;
	}

	FileAccessSupport<Data> getFileAccessSupport() {
		return fileAccessSupport;
	}

	/**
	 *
	 * @return acceptable extensions
	 */
	public String[] getExtensions() {
		return fileAccessSupport.getExtensions();
	}

	public boolean extensionsMatch(final String filePath) {
		return fileAccessSupport.extensionsMatch(filePath);
	}

	public String getDescription() {
		return fileAccessSupport.getDescription();
	}

	public FileType<Data> getTargetType() {
		return new FileType<Data>(fileAccessSupport.getTargetType());
	}

	/**
	 * @return loadingAction
	 */
	LoadingAction<Data> getLoadingAction() {
		return fileAccessSupport.getLoadingAction();
	}

	/**
	 * @return savingAction
	 */
	SavingAction<Data> getSavingAction() {
		return fileAccessSupport.getSavingAction();
	}

	boolean isLoadable() {
		return fileAccessSupport.getLoadingAction() != null;
	}

	boolean isSavable() {
		return fileAccessSupport.getSavingAction() != null;
	}

	void setConfigToSavingAction(final Supplier<Object> configSupplier) {
		fileAccessSupport.setConfigToSavingAction(configSupplier);
	}

	@Override
	public int compareTo(final FileSelectionSupport<Data> o) {
		return fileAccessSupport.compareTo(o.fileAccessSupport);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof FileSelectionSupport f) {
			return fileAccessSupport.equals(f.fileAccessSupport);
		}

		return false;
	}
}
