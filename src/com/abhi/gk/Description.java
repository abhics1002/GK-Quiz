/*package com.abhi.gk;

import javax.xml.namespace.QName;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.rmi.RemoteException;

public class Description extends Activity{

	TextView desc;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);
		
		desc = (TextView) findViewById(R.id.description);
		
		Log.v("DESCRIPTION", "trying to display description");
		desc.setText(callService(101));
		Log.v("DESCRIPTION", "after Set description");
		
}
	
	public String callService(int questionId)
	{
		Log.v("DESCRIPTION", "inside callService"+questionId);
		try {
			String endpoint =
					"http://localhost:8692/hello/services/Hello";

			Service  service = new Service();
			Call     call    = (Call) service.createCall();

			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName(new QName("http://soapinterop.org/", "getInfo"));

			String ret = (String) call.invoke( new Object[] { questionId } );

			System.out.println("Sent 'Hello!', got '" + ret + "'");
			
			return ret;

		} catch (Exception e) {
			System.err.println(e.toString());
			return "error while trying to load description. make sure your you are connected to internet.";
		}
		
		
	}
}
*/