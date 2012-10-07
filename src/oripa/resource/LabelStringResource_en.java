package oripa.resource;

import java.util.ListResourceBundle;

public class LabelStringResource_en extends ListResourceBundle {

		
	    static final Object[][] strings = { 
	        { StringID.Main.TITLE_ID, "ORIPA S " + Version.ORIPAS_VERSION + 
	        	": Origami Pattern Editor based on ORIPA" + "  v" + Version.ORIPA_VERSION }, 
	        { StringID.Main.FILE_ID, "File" }, 
	        { StringID.Main.EDIT_ID, "Edit" }, 
	        { StringID.Main.HELP_ID, "Help" }, 
	        { StringID.Main.NEW_ID, "New" }, 
	        { StringID.Main.OPEN_ID, "Open" }, 
	        { StringID.Main.SAVE_ID, "Save" }, 
	        { StringID.Main.SAVE_AS_ID, "Save As ..." }, 
	        { StringID.Main.SAVE_AS_IMAGE_ID, "Save As Image ..." }, 
	        { StringID.Main.EXPORT_DXF_ID, "Export (DXF)" }, 
	        { StringID.Main.PROPERTY_ID, "Property" }, 
	        { StringID.Main.EXIT_ID, "Exit" }, 
	        { StringID.Main.ABOUT_ID, "About" }, 
	        { StringID.Main.UNDO_ID, "Undo" }, 
	        { StringID.Main.SELECT_ALL_ID, "Select all"},
	        
	        
	        { StringID.UI.AUX_ID, "Aux" }, 
	        { StringID.UI.VALLEY_ID, "Valley" }, 
	        { StringID.UI.MOUNTAIN_ID, "Mountain" }, 

	        { StringID.UI.INPUT_LINE_ID, "Input Line" }, 
	        { StringID.UI.SELECT_ID, "Select" }, 
	        { StringID.UI.DELETE_LINE_ID, "Delete Line" }, 
	        
	        { StringID.UI.SHOW_GRID_ID, "Show Grid" }, 
	        { StringID.UI.SHOW_MV_ID, "Show M/V Lines"},
	        { StringID.UI.SHOW_AUX_ID, "Show Aux Lines"},

	        { StringID.UI.CHANGE_LINE_TYPE_FROM_ID, "  from" }, 
	        { StringID.UI.CHANGE_LINE_TYPE_TO_ID, "to" }, 
	        

	        
	        //---------------------------------------------------------
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
	        { StringID.UI.FULL_ESTIMATION_ID, "Full Estimation"},
	        { StringID.UI.CHECK_WINDOW_ID, "Check Window"},
	        
	        { StringID.UI.GRID_SIZE_CHANGE_ID, "Set" }, 
	        { StringID.UI.SHOW_VERTICES_ID, "Show Vertices" }, 
	        { StringID.UI.EDIT_MODE_ID, "Edit Mode" }, 
	        { StringID.UI.LINE_INPUT_MODE_ID, "Line Input Mode"},
	        { StringID.UI.LENGTH_ID, "Length"},
	        { StringID.UI.ANGLE_ID, "Angle"},
	        { StringID.UI.GRID_DIVIDE_NUM_ID, "Div Num"}

	    
	    };

	    @Override
	    protected Object[][] getContents() {
	        return strings;
	    }

}
