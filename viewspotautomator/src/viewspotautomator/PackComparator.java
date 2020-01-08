package viewspotautomator;

import java.util.Comparator;
import se.vidstige.jadb.managers.Package;

public class PackComparator implements Comparator<Package> {

	@Override
	public int compare(Package arg0, Package arg1) {
		return arg0.toString().compareTo(arg1.toString());
	}

}
