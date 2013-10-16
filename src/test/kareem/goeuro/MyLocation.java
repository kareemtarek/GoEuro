package test.kareem.goeuro;

public class MyLocation implements Comparable<MyLocation>{
	public static double Mylat=123;
	public static double Mylon=27;
	long id;
	String name;
	double lat;
	double lon;
	public MyLocation(long id,String name,double lat,double lon) {
		this.id=id;
		this.name=name;
		this.lat=lat;
		this.lon=lon;
	}
	@Override
	public int compareTo(MyLocation L2) {
		// TODO Auto-generated method stub
		double d1=distance(lat,lon,Mylat,Mylon);
		double d2=distance(L2.lat,L2.lon,Mylat,Mylon);
		if(d1<d2)return -1;
		else if(d1>d2)return 1;
		return 0;
	}
	private double distance(double lat1, double lon1, double lat2,
			double lon2) {
		double R = 6371; // km
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c;
		return d;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
	return 	id+" : "+name;
	}
}
