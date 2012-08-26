package oripa;

import oripa.file.Exporter;
import oripa.file.SavingAction;

public class SavingDoc implements SavingAction{

	private Exporter exporter;
	
	public SavingDoc(Exporter exporter){
		this.exporter = exporter;
	}
	
	@Override
	public boolean save(String path) {
		boolean success = false;
		try {
			success = exporter.export(ORIPA.doc, path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}
	
}