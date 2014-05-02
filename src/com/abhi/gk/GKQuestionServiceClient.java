/**
 * 
 */
package com.abhi.gk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author abbharadwaj
 * this class interact with the Rest service 
 * send request and get response in xml format. 
 */
public class GKQuestionServiceClient {

	public static final String BASE_URI = "http://localhost:8080/TEST_REST";

	public void getDescriptionClient(String id)
	{
		String url = "http://localhost:8080/TEST_REST/rest/GKQuestionAnswerService/getquestion/"+id;
	
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = httpClient.execute(request);

			HttpEntity httpEntity = response.getEntity();
			InputStream is = httpEntity.getContent();   
			String line = "";

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

		} 
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*public static void main(String[] args)
	{
		GKQuestionServiceClient obj = new GKQuestionServiceClient();
		obj.getDescriptionClient("1");
	}*/

}
