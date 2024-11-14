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
import oripa.gui.view.file.SavingFileChooserView;
import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
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

			var view = setupChooserFactory(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.getSelectedFilterDescription()).thenReturn("description");

			FileTypeProperty<Doc> type = mock();

			setupFileSelectionService("opx", type);

			setupFileFactory(correctedPath, false);

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

			var view = setupChooserFactory(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.getSelectedFilterDescription()).thenReturn("description");

			FileTypeProperty<Doc> type = mock();

			setupFileSelectionService("opx", type);

			setupFileFactory(correctedPath, false);

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

			var view = setupChooserFactory(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.getSelectedFilterDescription()).thenReturn("description");
			when(view.showOverwriteConfirmMessage()).thenReturn(true);

			FileTypeProperty<Doc> type = mock();

			setupFileSelectionService("opx", type);

			setupFileFactory(correctedPath, true);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.SELECTED, result.action());
			assertEquals(correctedPath, result.path());
			assertEquals(type, result.type());
		}

		@Test
		void returnsCancelWhenUserCanceled() {
			var path = "dummy/path.opx";

			var view = setupChooserFactory(path);

			when(view.showDialog(any())).thenReturn(false);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.CANCELED, result.action());
		}

		@Test
		void returnsCancelWhenOverwritingCanceled() {
			var path = "dummy/path.opx";
			var correctedPath = "dummy/path.opx";
			when(extensionCorrector.correct(eq(path), any())).thenReturn(correctedPath);

			var view = setupChooserFactory(path);

			File file = mock();
			when(file.getPath()).thenReturn(path);

			when(view.showDialog(any())).thenReturn(true);
			when(view.getSelectedFile()).thenReturn(file);
			when(view.getSelectedFilterExtensions()).thenReturn(new String[] { "opx" });
			when(view.showOverwriteConfirmMessage()).thenReturn(false);

			setupFileFactory(correctedPath, true);

			var result = presenter.saveUsingGUI(path);

			assertEquals(UserAction.CANCELED, result.action());
		}

		SavingFileChooserView setupChooserFactory(final String path) {
			SavingFileChooserView view = mock();

			when(chooserFactory.createForSaving(eq(path), any())).thenReturn(view);

			return view;
		}

		void setupFileSelectionService(final String extension, final FileTypeProperty<Doc> type) {
			FileAccessSupport<Doc> support = mock();
			when(support.getExtensions()).thenReturn(new String[] { "opx" });

			when(fileSelectionService.getSavableSupports()).thenReturn(List.of(support));
			when(fileSelectionService.getSavableTypeByDescription(anyString())).thenReturn(type);
		}

		void setupFileFactory(final String path, final boolean exists) {
			File file = mock();
			when(file.exists()).thenReturn(exists);

			when(fileFactory.create(eq(path))).thenReturn(file);

		}

	}
}
