package viewspotautomator;

public class WifiNetwork {
	public String ssid;
	public String password;
	
	public WifiNetwork(String s,String p) {
		ssid=s;
		password=p;
	}
	
	public WifiNetwork(String s) {
		ssid=s;
		password="";
	}
	
	public String toString() {
		String passStat;
		if(password=="") {
			passStat="unprotected";
		}
		else {
			passStat="protected";
		}
		
		
		return (ssid+": "+passStat);
		
	}
	
	public Boolean equals(WifiNetwork w) {
		if(ssid.equals(w.ssid)){
			return true;
		}
		else {
			return false;
		}
	}

}
