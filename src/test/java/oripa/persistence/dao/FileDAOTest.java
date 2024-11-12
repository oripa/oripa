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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.LoadingAction;
import oripa.persistence.filetool.SavingAction;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.util.file.FileFactory;

/**
 *
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class FileDAOTest {
	@InjectMocks
	FileDAO<Doc> dao;

	@Mock
	FileFactory fileFactory;

	@Mock
	FileAccessSupportSelector<Doc> selector;

	@Nested
	class TestLoad {
		@Test
		void loadSucceedsWhenFileExists()
				throws IOException, IllegalArgumentException, FileVersionError, WrongDataFormatException {

			FileAccessSupport<Doc> support = mock();
			LoadingAction<Doc> loadingAction = mock();

			when(selector.getLoadableOf(anyString())).thenReturn(support);

			when(support.getLoadingAction()).thenReturn(loadingAction);

			when(loadingAction.load(anyString())).thenReturn(Optional.of(mock()));

			File file = mock();
			when(file.exists()).thenReturn(true);
			when(file.getCanonicalPath()).thenReturn("canonical path");

			when(fileFactory.create(anyString())).thenReturn(file);

			var docOpt = dao.load("path");

			verify(loadingAction).load("canonical path");

			assertTrue(docOpt.isPresent());
		}

		@Test
		void exceptionIsThrownWhenfileDoesNotExist()
				throws IOException, IllegalArgumentException, FileVersionError, WrongDataFormatException {

			File file = mock();
			when(file.exists()).thenReturn(false);
			when(file.getCanonicalPath()).thenReturn("canonical path");

			when(fileFactory.create(anyString())).thenReturn(file);

			assertThrows(FileNotFoundException.class, () -> dao.load("path"));
		}
	}

	@Nested
	class TestSave {
		@Test
		void saveSucceedsWhenFileTypeIsGiven() throws IllegalArgumentException, IOException {

			Doc doc = mock();
			FileTypeProperty<Doc> fileType = mock();

			FileAccessSupport<Doc> support = mock();
			SavingAction<Doc> savingAction = mock();

			when(selector.getSavablesOf(anyList())).thenReturn(List.of(support));

			when(support.getSavingAction()).thenReturn(savingAction);

			when(savingAction.save(eq(doc), anyString())).thenReturn(true);

			File file = mock();
			when(file.getCanonicalPath()).thenReturn("canonical path");

			when(fileFactory.create(anyString())).thenReturn(file);

			dao.save(doc, "canonical path", fileType);

			verify(savingAction).save(doc, "canonical path");
		}

		@Test
		void saveSucceedsWhenFileTypeIsNotGiven() throws IllegalArgumentException, IOException {

			Doc doc = mock();

			FileAccessSupport<Doc> support = mock();
			SavingAction<Doc> savingAction = mock();

			when(selector.getSavableOf(anyString())).thenReturn(support);

			when(support.getSavingAction()).thenReturn(savingAction);

			when(savingAction.save(eq(doc), anyString())).thenReturn(true);

			File file = mock();
			when(file.getCanonicalPath()).thenReturn("canonical path");

			when(fileFactory.create(anyString())).thenReturn(file);

			dao.save(doc, "canonical path");

			verify(savingAction).save(doc, "canonical path");
		}
	}

	@Nested
	class TestSetConfigToSavingAction {
		@Test
		void supportMethodIsCalledWhenSupportExists() {

			FileTypeProperty<Doc> fileType = mock();
			FileAccessSupport<Doc> support = mock();

			when(selector.getFileAccessSupport(fileType)).thenReturn(Optional.of(support));

			dao.setConfigToSavingAction(fileType, mock());

			verify(support).setConfigToSavingAction(any());
		}

		@Test
		void supportMethodIsNotCalledWhenSupportDoesNotExist() {
			FileTypeProperty<Doc> fileType = mock();
			FileAccessSupport<Doc> support = mock();

			when(selector.getFileAccessSupport(fileType)).thenReturn(Optional.empty());

			dao.setConfigToSavingAction(fileType, mock());

			verify(support, never()).setConfigToSavingAction(any());
		}
	}

	@Nested
	class TestSetBeforeSave {
		@Test
		void supportMethodIsCalledWhenSupportExists() {
			FileTypeProperty<Doc> fileType = mock();
			FileAccessSupport<Doc> support = mock();

			when(selector.getFileAccessSupport(fileType)).thenReturn(Optional.of(support));

			dao.setBeforeSave(fileType, mock());

			verify(support).setBeforeSave(any());
		}

		@Test
		void supportMethodIsNotCalledWhenSupportDoesNotExist() {
			FileTypeProperty<Doc> fileType = mock();
			FileAccessSupport<Doc> support = mock();

			when(selector.getFileAccessSupport(fileType)).thenReturn(Optional.empty());

			dao.setBeforeSave(fileType, mock());

			verify(support, never()).setBeforeSave(any());
		}
	}

	@Nested
	class TestSetAfterSave {
		@Test
		void supportMethodIsCalledWhenSupportExists() {
			FileTypeProperty<Doc> fileType = mock();
			FileAccessSupport<Doc> support = mock();

			when(selector.getFileAccessSupport(fileType)).thenReturn(Optional.of(support));

			dao.setAfterSave(fileType, mock());

			verify(support).setAfterSave(any());
		}

		@Test
		void supportMethodIsNotCalledWhenSupportDoesNotExist() {
			FileTypeProperty<Doc> fileType = mock();
			FileAccessSupport<Doc> support = mock();

			when(selector.getFileAccessSupport(fileType)).thenReturn(Optional.empty());

			dao.setAfterSave(fileType, mock());

			verify(support, never()).setAfterSave(any());
		}
	}

}
