package oripa.resource;

import java.util.ListResourceBundle;

public class LabelStringResource_en extends ListResourceBundle {

		
	    static final Object[][] strings = { 
	        { StringID.Menu.TITLE_ID, "ORIPA S " + Version.ORIPAS_VERSION + 
	        	": Origami Pattern Editor based on ORIPA" + "  v" + Version.ORIPA_VERSION }, 
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
	        { StringID.Menu.ABOUT_ID, "About" }, 
	        { StringID.Menu.UNDO_ID, "Undo" }, 
	        { StringID.Menu.COPY_PASTE_ID, "Copy and Paste" }, 
	        { StringID.Menu.CONTOUR_ID, "Edit Contour" }, 
	        { StringID.Menu.SELECT_ALL_ID, "select all"},
	        
	        
	        { StringID.UI.AUX_ID, "Aux" }, 
	        { StringID.UI.VALLEY_ID, "Valley" }, 
	        { StringID.UI.MOUNTAIN_ID, "Mountain" }, 

	        { StringID.UI.INPUT_LINE_ID, "Input Line" }, 
	        { StringID.UI.SELECT_ID, "Select" }, 
	        { StringID.UI.DELETE_LINE_ID, "Delete Line" }, 
	        { StringID.UI.SHOW_GRID_ID, "Show Grid" }, 
	        { StringID.UI.CHANGE_LINE_TYPE_ID, "Change Line Type" }, 
	        { StringID.UI.ADD_VERTEX_ID, "Add Vertex" }, 
	        { StringID.UI.DELETE_VERTEX_ID, "Delete Vertex" }, 
	        { StringID.UI.MEASURE_ID, "Measure" }, 
	        { StringID.UI.FOLD_ID, "Fold..." }, 
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
