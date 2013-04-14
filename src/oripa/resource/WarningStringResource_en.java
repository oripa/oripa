package oripa.resource;

import java.util.ListResourceBundle;

public class WarningStringResource_en extends ListResourceBundle {

    static final Object[][] strings = { 
        { StringID.Warning.SAME_FILE_EXISTS_ID, "Same name file exists. Over write?" }, 
        { StringID.Warning.FOLD_FAILED_DUPLICATION_ID, "Failed to fold. Try again by deleting duplicating segments?"},
        { StringID.Warning.FOLD_FAILED_WRONG_STRUCTURE_ID, "Failed to fold. It seems the pattern has basic problems."},
    };
    
    @Override
	protected Object[][] getContents() {
		// TODO Auto-generated method stub
		return strings;
	}


	
}
