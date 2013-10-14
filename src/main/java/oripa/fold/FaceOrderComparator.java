package oripa.fold;

import java.util.Comparator;

import oripa.geom.OriFace;

public class FaceOrderComparator implements Comparator<OriFace> {

	@Override
	public int compare(OriFace f1, OriFace f2) {
		return f1.z_order > f2.z_order ? 1 : -1;
	}
}