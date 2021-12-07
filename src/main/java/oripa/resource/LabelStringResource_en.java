package oripa.resource;

import java.util.ListResourceBundle;

public class LabelStringResource_en extends ListResourceBundle {

	private static final Object[][] strings = {
			{ StringID.Main.TITLE_ID, "ORIPA " + Version.ORIPA_VERSION },
			{ StringID.Main.FILE_ID, "File" },
			{ StringID.Main.EDIT_ID, "Edit" },
			{ StringID.Main.HELP_ID, "Help" },
			{ StringID.Main.NEW_ID, "New" },
			{ StringID.Main.OPEN_ID, "Open" },
			{ StringID.Main.SAVE_ID, "Save" },
			{ StringID.Main.SAVE_AS_ID, "Save As ..." },
			{ StringID.Main.SAVE_AS_IMAGE_ID, "Save As Image ..." },

			{ StringID.Main.EXPORT_FOLD_ID, "Export FOLD" },
			{ StringID.Main.EXPORT_CP_ID, "Export CP" },
			{ StringID.Main.EXPORT_DXF_ID, "Export DXF" },
			{ StringID.Main.EXPORT_SVG_ID, "Export SVG" },

			{ StringID.Main.PROPERTY_ID, "Property" },
			{ StringID.Main.EXIT_ID, "Exit" },
			{ StringID.Main.ABOUT_ID, "About" },
			{ StringID.Main.UNDO_ID, "Undo" },
			{ StringID.Main.REDO_ID, "Redo" },
			{ StringID.Main.ARRAY_COPY_ID, "Array Copy" },
			{ StringID.Main.CIRCLE_COPY_ID, "Circle Copy" },
			{ StringID.Main.UNSELECT_ALL_ID, "Unselect All" },
			{ StringID.Main.DELETE_SELECTED_ID, "Delete Selected Lines" },

			{ StringID.UI.ZERO_LINE_WIDTH_ID, "Zero line width" },
			{ StringID.UI.AUX_ID, "Aux" },
			{ StringID.UI.VALLEY_ID, "Valley" },
			{ StringID.UI.MOUNTAIN_ID, "Mountain" },

			{ StringID.UI.INPUT_LINE_ID, "Input Line" },
			{ StringID.UI.SELECT_ID, "Select" },
			{ StringID.UI.DELETE_LINE_ID, "Delete Line" },

			{ StringID.UI.SHOW_GRID_ID, "Show Grid" },
			{ StringID.UI.SHOW_MV_ID, "Show M/V Lines" },
			{ StringID.UI.SHOW_AUX_ID, "Show Aux Lines" },

			{ StringID.UI.CHANGE_LINE_TYPE_FROM_ID, "from" },
			{ StringID.UI.CHANGE_LINE_TYPE_TO_ID, "to" },

			{ StringID.Main.ORIPA_FILE_ID, "ORIPA file" },
			{ StringID.Main.PICTURE_FILE_ID, "Picture file" },

			{ StringID.Main.PROP_DIAL_TITLE_ID, "Model Information" },
			{ StringID.Main.PROP_DIAL_MODEL_TITLE_ID, "Title" },
			{ StringID.Main.PROP_DIAL_AUTHOR_ID, "Editor data" },
			{ StringID.Main.PROP_DIAL_CREATOR_ID, "Original Author" },
			{ StringID.Main.PROP_DIAL_SOURCE_ID, "References" },
			{ StringID.Main.PROP_DIAL_MEMO_ID, "Memo" },
			{ StringID.Main.PROP_DIAL_OK_ID, "OK" },

			// ---------------------------------------------------------
			// Integrated IDs
			{ StringID.COPY_PASTE_ID, "Copy and Paste" },
			{ StringID.CUT_PASTE_ID, "Cut and Paste" },
			{ StringID.EDIT_CONTOUR_ID, "Edit Contour" },
			{ StringID.SELECT_ID, "Select" },
			{ StringID.DELETE_LINE_ID, "Delete Line" },
			{ StringID.CHANGE_LINE_TYPE_ID, "Change Line Type" },
			{ StringID.ADD_VERTEX_ID, "Add Vertex" },
			{ StringID.DELETE_VERTEX_ID, "Delete Vertex" },
			{ StringID.SELECT_ALL_LINE_ID, "Select All" },

			{ StringID.UI.MEASURE_ID, "Measure" },
			{ StringID.UI.FOLD_ID, "Fold..." },
			{ StringID.UI.FULL_ESTIMATION_ID, "Full Estimation" },
			{ StringID.UI.CHECK_WINDOW_ID, "Check Window" },

			{ StringID.UI.GRID_SIZE_CHANGE_ID, "Set" },
			{ StringID.UI.SHOW_VERTICES_ID, "Show Vertices" },
			{ StringID.UI.EDIT_MODE_ID, "Edit Mode" },
			{ StringID.UI.LINE_INPUT_MODE_ID, "Line Input Mode" },
			{ StringID.UI.LENGTH_ID, "Length" },
			{ StringID.UI.ANGLE_ID, "Angle" },
			{ StringID.UI.GRID_DIVIDE_NUM_ID, "Div Num" },

			{ StringID.UI.GENERAL_SETTINGS_ID, "General Settings" },
			{ StringID.UI.LINE_INPUT_PANEL_ID, "Draw Lines" },
			{ StringID.UI.ANGLE_STEP_ID, "Stepping Angle" },
			{ StringID.UI.TOOL_PANEL_ID, "Tools" },
			{ StringID.UI.TOOL_SETTINGS_PANEL_ID, "Tool Settings" },
			{ StringID.UI.ALTER_LINE_TYPE_PANEL_ID, "Alter Line Type" },
			{ StringID.UI.INSERT_BY_VALUE_PANEL_ID, "Insert by Value" },
			{ StringID.UI.GRID_SETTINGS_PANEL_ID, "Grid" },
			{ StringID.UI.VIEW_SETTINGS_PANEL_ID, "View" },

			{ StringID.UI.SHORTCUT_ID, "Shortcut:" },

			// Estimation Result Frame String Resources
			{ StringID.EstimationResultUI.TITLE_ID, "Folded Origami" },

			{ StringID.EstimationResultUI.ANSWERS_PANEL_ID, "Answers" },
			{ StringID.EstimationResultUI.NEXT_RESULT_ID, "Next" },
			{ StringID.EstimationResultUI.PREV_RESULT_ID, "Prev" },
			{ StringID.EstimationResultUI.INDEX_ID, "Folded model" },

			{ StringID.EstimationResultUI.DRAWING_CONFIG_PANEL_ID, "Drawing Config" },
			{ StringID.EstimationResultUI.ORDER_FLIP_ID, "Flip" },
			{ StringID.EstimationResultUI.SHADOW_ID, "Shadow" },
			{ StringID.EstimationResultUI.USE_COLOR_ID, "Use Color" },
			{ StringID.EstimationResultUI.EDGE_ID, "Draw Edge" },
			{ StringID.EstimationResultUI.FILL_FACE_ID, "Fill Face" },

			{ StringID.EstimationResultUI.FACE_COLOR_PANEL_ID, "Color Config" },
			{ StringID.EstimationResultUI.FACE_COLOR_FRONT_ID, "Choose Front Color" },
			{ StringID.EstimationResultUI.FACE_COLOR_BACK_ID, "Choose Back Color" },

			{ StringID.EstimationResultUI.EXPORT_ID, "Export" },

			{ StringID.EstimationResultUI.HINT_LABEL_ID, "L: Rotate / Wheel: Zoom / R: Move" },

			{ StringID.ModelMenu.FILE_ID, "File" },
			{ StringID.ModelMenu.DISPLAY_ID, "Display" },
			{ StringID.ModelMenu.EXPORT_DXF_ID, "Export model lines (DXF)" },
			{ StringID.ModelMenu.EXPORT_OBJ_ID, "Export to OBJ file" },
			{ StringID.ModelMenu.EXPORT_SVG_ID, "Export to SVG file" },
			{ StringID.ModelMenu.INVERT_ID, "Invert" },
			{ StringID.ModelMenu.DIRECTION_BASIC_ID, "L: Rot R:Move Wheel:Zoom" },
			{ StringID.ModelMenu.DISPLAY_TYPE_ID, "Drawing type" },
			{ StringID.ModelMenu.FILL_ALPHA_ID, "Fill transmission" },
			{ StringID.ModelMenu.DRAW_LINES_ID, "Draw lines" },
			{ StringID.ModelMenu.TITLE_ID, "Expected Folded Origami" },
			{ StringID.ModelMenu.SHOW_CROSS_LINE_ID, "Show cross-line" },
	};

	@Override
	protected Object[][] getContents() {
		return strings;
	}

}
