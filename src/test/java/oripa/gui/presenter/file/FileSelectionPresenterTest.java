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
package oripa.gui.presenter.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.FileSelectionService;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.file.LoadingFileChooserView;
import oripa.gui.view.file.SavingFileChooserView;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.util.file.ExtensionCorrector;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class FileSelectionPresenterTest {
	@InjectMocks
	FileSelectionPresenter<Doc> presenter;

	@Mock
	FrameView parent;
	@Mock
	FileChooserFactory chooserFactory;
	@Mock
	FileFactory fileFactory;
	@Mock
	FileSelectionService<Doc> fileSelectionService;
	@Mock
	ExtensionCorrector extensionCorrector;

	@Nested
	class TestSaveUsingGUI_NoType {

		@Test
		void returnsCorrectedPathWhenUserSelected() {
			var path = "dummy/path.ext";
			var correctedPath = "dummy/path.opx";

			when(extensionCorrector.correct(eq(path), any())).thenReturn(correctedPath);

			var view = setupChooserFactoryForSaving(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.getSelectedFilterDescription()).thenReturn("description");

			FileType<Doc> type = mock();

			setupFileSelectionServiceForSaving("opx", type);

			setupFileFactoryForSaving(correctedPath, false);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.SELECTED, result.action());
			assertEquals(correctedPath, result.path());
			assertEquals(type, result.type());
		}

		@Test
		void returnsGivenPathWhenUserSelected() {
			var path = "dummy/path.opx";
			var correctedPath = "dummy/path.opx";

			when(extensionCorrector.correct(eq(path), any())).thenReturn(correctedPath);

			var view = setupChooserFactoryForSaving(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.getSelectedFilterDescription()).thenReturn("description");

			FileType<Doc> type = mock();

			setupFileSelectionServiceForSaving("opx", type);

			setupFileFactoryForSaving(correctedPath, false);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.SELECTED, result.action());
			assertEquals(correctedPath, result.path());
			assertEquals(type, result.type());
		}

		@Test
		void returnsPathWhenOverwriting() {
			var path = "dummy/path.opx";
			var correctedPath = "dummy/path.opx";

			when(extensionCorrector.correct(eq(path), any())).thenReturn(correctedPath);

			var view = setupChooserFactoryForSaving(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.getSelectedFilterDescription()).thenReturn("description");
			when(view.showOverwriteConfirmMessage()).thenReturn(true);

			FileType<Doc> type = mock();

			setupFileSelectionServiceForSaving("opx", type);

			setupFileFactoryForSaving(correctedPath, true);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.SELECTED, result.action());
			assertEquals(correctedPath, result.path());
			assertEquals(type, result.type());
		}

		@Test
		void returnsCancelWhenUserCanceled() {
			var path = "dummy/path.opx";

			var view = setupChooserFactoryForSaving(path);

			when(view.showDialog(any())).thenReturn(false);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.CANCELED, result.action());
		}

		@Test
		void returnsCancelWhenOverwritingCanceled() {
			var path = "dummy/path.opx";
			var correctedPath = "dummy/path.opx";
			when(extensionCorrector.correct(eq(path), any())).thenReturn(correctedPath);

			var view = setupChooserFactoryForSaving(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.showOverwriteConfirmMessage()).thenReturn(false);

			setupFileFactoryForSaving(correctedPath, true);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.CANCELED, result.action());
		}

		void setupFileSelectionServiceForSaving(final String extension, final FileType<Doc> type) {
			FileAccessSupport<Doc> support = mock();
			when(support.getExtensions()).thenReturn(new String[] { "opx" });

			when(fileSelectionService.getSavableSupports()).thenReturn(List.of(support));
			when(fileSelectionService.getSavableTypeByDescription(anyString())).thenReturn(type);
		}
	}

	@Nested
	class TestSaveUsingGUI_WithTypes {
		// Not necessary to test exhaustively because the target method is a
		// thin wrapper method.
		@Test
		void returnsPathWhenUserSelected() {
			var path = "dummy/path.fold";
			var correctedPath = "dummy/path.fold";

			when(extensionCorrector.correct(eq(path), any())).thenReturn(correctedPath);

			var view = setupChooserFactoryForSaving(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.getSelectedFilterDescription()).thenReturn("description");

			FileType<Doc> type1 = mock();
			FileAccessSupport<Doc> support1 = mock();
			when(support1.getExtensions()).thenReturn(new String[] { "opx" });

			FileType<Doc> type2 = mock();
			FileAccessSupport<Doc> support2 = mock();
			when(support2.getExtensions()).thenReturn(new String[] { "fold" });

			when(fileSelectionService.getSavableSupportsOf(any())).thenReturn(List.of(support1, support2));

			when(fileSelectionService.getSavableTypeByDescription(anyString())).thenReturn(type2);

			setupFileFactoryForSaving(correctedPath, false);

			// assume that several types are given.
			var result = presenter.saveUsingGUI(path, List.of(type1, type2));

			assertEquals(UserAction.SELECTED, result.action());
			assertEquals(correctedPath, result.path());
			assertEquals(type2, result.type());
		}

	}

	SavingFileChooserView setupChooserFactoryForSaving(final String path) {
		SavingFileChooserView view = mock();

		when(chooserFactory.createForSaving(eq(path), any())).thenReturn(view);

		return view;
	}

	void setupFileFactoryForSaving(final String path, final boolean exists) {
		File file = mock();
		when(file.exists()).thenReturn(exists);

		when(fileFactory.create(eq(path))).thenReturn(file);

	}

	@Nested
	class TestLoadUsingGUI {
		@Test
		void returnsSelectedPathWhenUserSelected() {
			var lastPath = "last/path.opx";

			var view = setupChooserFactoryForLoading(lastPath);
			when(view.showDialog(any())).thenReturn(true);

			File selectedFile = mock();
			var selectedPath = "selected/path.opx";
			when(selectedFile.getPath()).thenReturn(selectedPath);
			when(view.getSelectedFile()).thenReturn(selectedFile);

			FileAccessSupport<Doc> type = mock();
			when(fileSelectionService.getLoadableSupportsWithMultiType()).thenReturn(List.of(type));

			var result = presenter.loadUsingGUI(lastPath);

			assertEquals(UserAction.SELECTED, result.action());
			assertEquals(selectedPath, result.path());
		}

		@Test
		void returnsCanceledWhenUserCanceled() {
			var lastPath = "last/path.opx";

			var view = setupChooserFactoryForLoading(lastPath);
			when(view.showDialog(any())).thenReturn(false);

			var result = presenter.loadUsingGUI(lastPath);

			assertEquals(UserAction.CANCELED, result.action());
		}

	}

	LoadingFileChooserView setupChooserFactoryForLoading(final String path) {
		LoadingFileChooserView view = mock();

		when(chooserFactory.createForLoading(eq(path), any())).thenReturn(view);

		return view;
	}

	void setupFileFactoryForLoading(final String path) {
		File file = mock();
		when(fileFactory.create(eq(path))).thenReturn(file);

	}

}
