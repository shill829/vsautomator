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
	
		return (ssid);
		
	}
	
	public Boolean equals(WifiNetwork w) {
		if(ssid.equals(w.ssid)){
			return true;
		}
		else {
			return false;
		}
	}
	
	public Boolean isProtected() {
		if(password.equals("")) {
			return false;
		}
		else {
			return true;
		}
	}

}
