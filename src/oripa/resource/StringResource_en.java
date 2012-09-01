/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.resource;

import java.util.ListResourceBundle;

public class StringResource_en extends ListResourceBundle {

	
    static final Object[][] strings = { 
        { StringID.Menu.TITLE_ID, "ORIPA S " + Version.ORIPAS_VERSION + ": Origami Pattern Editor based on ORIPA" }, 
        { StringID.Menu.FILE_ID, "File" }, 
        { StringID.Menu.EDIT_ID, "Edit" }, 
        { StringID.Menu.HELP_ID, "Help" }, 
        { StringID.Menu.NEW_ID, "New" }, 
        { StringID.Menu.OPEN_ID, "Open" }, 
        { StringID.Menu.SAVE_ID, "Save" }, 
        { StringID.Menu.SAVE_AS_ID, "Save As ..." }, 
        { StringID.Menu.SAVE_AS_IMAGE_ID, "Save As Image ..." }, 
        { StringID.Menu.EXPORT_DXF_ID, "Export (DXF)" }, 
        { StringID.Menu.EDIT_CONTOUR_ID, "Edit Contour" }, 
        { StringID.Menu.PROPERTY_ID, "Property" }, 
        { StringID.Menu.EXIT_ID, "Exit" }, 
        { StringID.Menu.UNDO_ID, "Undo" }, 
        { StringID.Menu.ABOUT_ID, "About" }, 
        
        { StringID.CommandHint.DIRECT_V_ID, "Specify two end poionts.[Ctrl] allows to pick any point on an edge." }, 
        { StringID.CommandHint.ON_V_ID, "Specify two points that lie on the line" }, 
        { StringID.CommandHint.SYMMETRIC_ID, "Input a symmetrical segment. 1st, 2nd are for target. 2nd, 3rd are for base.+[Ctrl] continues automatically." }, 
        { StringID.CommandHint.TRIANGLE_ID, "Specify 3 points to input 3 segments to incenter." }, 
        { StringID.CommandHint.BISECTOR_ID, "Input a bisector. Specify 3 points and a segment." }, 
        { StringID.CommandHint.VERTICAL_ID, "Input a vertical line. Specify a point and a line." }, 
        { StringID.CommandHint.MIRROR_ID, "Mirror copy. Pick target segments and [Ctrl]+Click for the base segment." }, 
        { StringID.CommandHint.BY_VALUE_ID, "Input length and angle, then specify the start point. Push [Mesure] button to get value from segment." }, 
        { StringID.CommandHint.PICK_LENGTH_ID, "Mesure distance between 2 points." }, 
        { StringID.CommandHint.PICK_ANGLE_ID, "Mesure angle. Specify 3 points" }, 
        { StringID.CommandHint.CHANGE_LINE_TYPE_ID, "Change type of a segment. Pick a segment." }, 
        { StringID.CommandHint.DELETE_LINE_ID, "Delete a segment. Pick a segment" }, 
        { StringID.CommandHint.ADD_VERTEX_ID, "Add vertex on a segment." }, 
        { StringID.CommandHint.DELETE_VERTEX_ID, "Delete vertex that does not change the structure." }, 
        { StringID.CommandHint.CONTOUR_ID, "Edit contour. The contour must be convex." }, 
        { StringID.CommandHint.PERPENDICULAR_BISECTOR_ID, "Input Perpendicular Bisector of two vertices. Select two vertices by left click." }, 
        { StringID.CommandHint.SELECT_ID, "Select/UnSelect Lines by Left Click or Left Drag"},

        
        { StringID.Default.FILE_NAME_ID, "NoTitle" }, 
        { StringID.DIALOG_TITLE_SAVE_ID, "Save" }, 
        { StringID.Error.SAVE_FAILED_ID, "Failed to save." }, 
        { StringID.Error.LOAD_FAIELD_ID, "Failed to load." }, 
        { StringID.Warning.SAME_FILE_EXISTS_ID, "Same name file exists. Over write?" }, 
        { StringID.ORIPA_FILE_ID, "ORIPA file" }, 
        { StringID.PICTURE_FILE_ID, "Picture file"},
        { StringID.UI.INPUT_LINE_ID, "Input Line" }, 
        { StringID.UI.SELECT_ID, "Select" }, 
        { StringID.UI.DELETE_LINE_ID, "Delete Line" }, 
        { StringID.UI.SHOW_GRID_ID, "Show Grid" }, 
        { StringID.UI.CHANGE_LINE_TYPE_ID, "Change Line Type" }, 
        { StringID.UI.ADD_VERTEX_ID, "Add Vertex" }, 
        { StringID.UI.DELETE_VERTEX_ID, "Delete Vertex" }, 
        { StringID.UI.MEASURE_ID, "Mesure" }, 
        { StringID.UI.FOLD_ID, "Fold..." }, 
        { StringID.UI.GRID_SIZE_CHANGE_ID, "Set" }, 
        { StringID.UI.SHOW_VERTICES_ID, "Show Vertices" }, 
        { StringID.UI.EDIT_MODE_ID, "Edit Mode" }, 
        { StringID.UI.LINE_INPUT_MODE_ID, "Line Input Mode"},
        { StringID.UI.LENGTH_ID, "Length"},
        { StringID.UI.ANGLE_ID, "Angle"},
        { StringID.UI.GRID_DIVIDE_NUM_ID, "Div Num"},

        { "Warning_foldFail1", "Failed to fold. Try again by deleting duplicating segments?"},
        { "Warning_foldFail2", "Failed to fold. It seems the pattern has basic problems."},
        { "MENU_Disp", "Display" },
        { "MENU_ExportModelLine_DXF", "Export Model Line(DXF)" },
        { "MENU_Invert", "Invert" },
        { "MENU_SlideFaces", "Slide Faces" },
        { "Direction_Basic", "    L: Rot R:Move Wheel:Zoom " },
        { "MENU_DispType", "Drawing type" },
        { "MENU_FillColor", "Fill Color: may be wrong" },
        { "MENU_FillWhite", "Fill White: may be wrong" },
        { "MENU_FillAlpha", "Fill Transmission" },
        { "MENU_DrawLines", "Draw Lines" },
        { "ExpectedFoldedOrigami", "Expected Folded Origami" }
    };
    
    @Override
    protected Object[][] getContents() {
        return strings;
    }
}
