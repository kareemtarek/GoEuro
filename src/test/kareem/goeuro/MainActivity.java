package test.kareem.goeuro;

import java.util.Arrays;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements LocationListener {

	
	public static final String SERVER_URL = "http://pre.dev.goeuro.de:12345/api/v1/suggest/position/en/name/";
	public static final int EndTextType = 1;
	public static final int StartTextType = 0;
	static AutoCompleteTextView startLocation;
	static AutoCompleteTextView endLocation;
	static ArrayAdapter<String> startAdapter,endAdapter;
	String startText,endText;
	
	static Button dateText,search;
	
	static MyLocation[] locationsStart,locationsEnd;
	static String[] locationsStartString,locationsEndString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialization();
		getMyLocation(); // Using GPS
		
		
		// button listeners
		
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(MainActivity.this, "Search is not yet implemented", Toast.LENGTH_LONG).show();
			}
		});
		startLocation.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				startText=((TextView)startLocation).getText()+"";
				startText=startText.replaceAll(" ", "%20");
				if(startText!=null&&!startText.equals("")&&startText.length()>1)GetMsg(startText,StartTextType,MainActivity.this);
			}
		});
		
		
		endLocation.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				endText=((TextView)endLocation).getText()+"";
				endText=endText.replaceAll(" ", "%20");// for white spaces.
				if(endText!=null&&!endText.equals("")&&endText.length()>1)GetMsg(endText,EndTextType,MainActivity.this);
			}
		});
		
		
	}
	
	private void initialization() {
		// initialize UI views && Objects. 
		search= (Button)findViewById(R.id.Search);
		startLocation=(AutoCompleteTextView)findViewById(R.id.startLocation);
		endLocation=(AutoCompleteTextView)findViewById(R.id.endLocation);
		dateText=(Button)findViewById(R.id.date);
		
		
		locationsStart=new MyLocation[0];
		locationsEnd=new MyLocation[0];
		locationsStartString=new String[0];
		locationsEndString=new String[0];
		
		
		startAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, locationsStartString);
		endAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, locationsEndString);
		
		
		startLocation.setThreshold(2);
		endLocation.setThreshold(2);
		startLocation.setAdapter(startAdapter);
		endLocation.setAdapter(endAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	public static void GetMsg(final String msg,final int type,final Activity activity){
		   new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.d("debug", "Server_MSG: "+msg);
				
				   String response = "";
				   try {
					   
				       HttpClient client = new DefaultHttpClient();  
				       String postURL = SERVER_URL+msg;
				       HttpGet post = new HttpGet(postURL); 
				       HttpResponse responsePOST = client.execute(post);  
				       HttpEntity resEntity = responsePOST.getEntity(); 
				       Log.d("debug","Message Sent>>>>>"+msg);
				       if (resEntity != null) {  
				    	   	
				    	       response = EntityUtils.toString(resEntity);
				    	       
				               Log.d("debug","Response: ");
				               
				               
				               JSONArray pages     =  new JSONObject(response).getJSONArray("results");
				               
				               if(type==EndTextType){
				            	   locationsEnd=new MyLocation[pages.length()];
				            	   locationsEndString=new String[pages.length()];
				               }
				               else{
				            	   locationsStart=new MyLocation[pages.length()];
				            	   locationsStartString=new String[pages.length()];
				               }
				               Log.d("debug", pages.length()+" here");
				               for (int i = 0; i < pages.length(); ++i) {
				            	   
				                   JSONObject rec = pages.getJSONObject(i);
//				                   JSONObject jsonPage =rec.getJSONObject("results");
				                   long  id = Long.valueOf(rec.getString("_id"));
				                   String name = rec.getString("name");
				                   JSONObject geo =  rec.getJSONObject("geo_position");
				                   double lat=Double.valueOf(geo.getString("latitude"));
				                   double lon=Double.valueOf(geo.getString("longitude"));
				                   MyLocation loc=new MyLocation(id, name, lat, lon);
				                   Log.d("debug", loc.toString());
				                   if(type==EndTextType){
				                	   locationsEnd[i]=(loc);
				                   }
				                   else{
				                	   locationsStart[i]=(loc);
				                   }
				               	}
				               if(type==EndTextType){
			                	   Arrays.sort(locationsEnd);
			                	   for (int i = 0; i < locationsEnd.length; i++) {
			                		   locationsEndString[i]=locationsEnd[i].name;
			                	   }

			                	   
			                	   activity.runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											try{
											endAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, locationsEndString);										
											endLocation.setThreshold(2);
											endLocation.setAdapter(endAdapter);
						                	endLocation.showDropDown();
										}catch (Exception e) {
											Log.d("debug","exceptionaas  ");
										}
										}
									});
			                	  
			                   }
			                   else{
			                	   Arrays.sort(locationsStart);
			                	   for (int i = 0; i < locationsStart.length; i++) {
			                		   locationsStartString[i]=locationsStart[i].name;
			                	   }
			                	   
			                	   activity.runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										try{
											startAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, locationsStartString);										
											startLocation.setThreshold(2);
											startLocation.setAdapter(startAdapter);
						                	startLocation.showDropDown();
										}catch (Exception e) {
											Log.d("debug","exceptionaas  ");
										}
										
									}
								});
			                	   
			                   }
				               
				       }
				       

				       
				      
				   } catch (Exception e) {
				       Log.d("debug", e.toString());
				       Log.d("debug","exception");
				   }
				
			}
		}).start();
		   
	   }
	private void getMyLocation() {
	   	 LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		     Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		     if(location != null){
		    	 Toast.makeText(this, "Location found!", Toast.LENGTH_LONG).show();
		    	 	MyLocation.Mylat=(location.getLatitude());
		    	 	MyLocation.Mylon=(location.getLongitude());

		     }
		     else{
		    	 Toast.makeText(this, "Location undefined", Toast.LENGTH_LONG).show();
		     }
	   	
}

	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			dateText.setText(day+" / "+(month+1)+" / "+year);
		}
	}
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	
}
