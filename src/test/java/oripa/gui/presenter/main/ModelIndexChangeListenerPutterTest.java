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
package oripa.gui.presenter.main;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.gui.view.model.ModelViewFrameView;
import oripa.swing.view.estimation.EstimationResultFrame;
import oripa.swing.view.util.ListItemSelectionPanel;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class ModelIndexChangeListenerPutterTest {
	@InjectMocks
	ModelIndexChangeListenerPutter putter;

	@Nested
	class TestPut {

		@Captor
		ArgumentCaptor<PropertyChangeListener> modelFrame1ListenerCaptor;
		@Captor
		ArgumentCaptor<PropertyChangeListener> resultFrame1ListenerCaptor;
		@Captor
		ArgumentCaptor<PropertyChangeListener> modelFrame2ListenerCaptor;
		@Captor
		ArgumentCaptor<PropertyChangeListener> resultFrame2ListenerCaptor;

		@Test
		void FirstPutBothThenPutModelFrameOnly() {
			ModelViewFrameView modelFrame1 = mock();
			EstimationResultFrame resultFrame1 = mock();

			ModelViewFrameView modelFrame2 = mock();

			putter.put(modelFrame1, resultFrame1);

			verify(modelFrame1).putModelIndexChangeListener(eq(resultFrame1), resultFrame1ListenerCaptor.capture());
			verify(resultFrame1).putModelIndexChangeListener(eq(modelFrame1), modelFrame1ListenerCaptor.capture());

			resultFrame1ListenerCaptor.getValue()
					.propertyChange(new PropertyChangeEvent(modelFrame1, ListItemSelectionPanel.INDEX, 0, 1));

			verify(resultFrame1).selectModel(1);

			modelFrame1ListenerCaptor.getValue()
					.propertyChange(new PropertyChangeEvent(resultFrame1, ListItemSelectionPanel.INDEX, 0, 1));

			verify(modelFrame1).selectModel(1);

			putter.put(modelFrame2, null);

			verify(modelFrame2, never()).putModelIndexChangeListener(any(), any());

			verify(modelFrame1, never()).putModelIndexChangeListener(eq(modelFrame2), any());
			verify(resultFrame1, never()).putModelIndexChangeListener(eq(modelFrame2), any());
		}
	}

}
