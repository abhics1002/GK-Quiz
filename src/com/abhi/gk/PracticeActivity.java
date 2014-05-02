package com.abhi.gk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PracticeActivity extends Activity {

	ListView listView;
	List<String> practicevalues = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	public static Hashtable<Integer,String[]> questionMap = new Hashtable<Integer,String[]>();

	int current_qno =1;
	int question_count=0;
	int score = 0;
	String uttar;

	List<String> userSubmitList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);

		listView = (ListView) findViewById(R.id.practicelist);

		try{
			LoadDataTask obj = new LoadDataTask();
			obj.execute();
		}
		catch(Exception ex)
		{
			Toast.makeText(getApplicationContext(), "No internet connectivity ", Toast.LENGTH_LONG).show();
			finish();
		}

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, practicevalues);
		listView.setAdapter(adapter); 

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item index
				int itemPosition     = position;
				String user_answer_submit= null;
				// ListView Clicked item value
				String  itemValue    = (String) listView.getItemAtPosition(position);

				if(itemPosition > 0)
				{
					Toast.makeText(getApplicationContext(), "you answered : " +itemValue , Toast.LENGTH_LONG).show();
					switch(itemPosition)
					{
					case 1:
						user_answer_submit= "a";
						break;
					case 2:
						user_answer_submit = "b";
						break;
					case 3:
						user_answer_submit ="c";
						break;
					case 4:
						user_answer_submit ="d";
						break;
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "You have skipped this Question.", Toast.LENGTH_LONG).show();
					user_answer_submit= "Did not answered";
				}

				userSubmitList.add(user_answer_submit);


				if (uttar.equalsIgnoreCase(user_answer_submit))
				{
					score++;
				}


				if(current_qno < question_count)
				{

					practicevalues.clear();
					current_qno++;
					getQuestion(current_qno);
					adapter.notifyDataSetChanged();
				}
				else
				{
					start_score_page();
				}
			}
		});

	}

	public class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog progressDialog;
		//declare other objects as per your need
		@Override
		protected void onPreExecute()
		{

			progressDialog= new ProgressDialog(PracticeActivity.this);
			progressDialog.setTitle("Please Wait..");
			progressDialog.setMessage("Loading");
			progressDialog.setCancelable(false);
			progressDialog.show();

			//do initialization of required objects objects here                
		};      
		@Override
		protected Boolean doInBackground(Void... arg0)
		{   
			System.out.println("inside GetQuestionSetFromServer");
			Log.v("GetQuestionSetFromServer", "--------doIGetQuestionSetFromServernBackground-------START");


			try{
				String practice_set_url = "https://dl.dropboxusercontent.com/u/105989177/practice_set/sample_set-1.txt";
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet(practice_set_url);
				HttpResponse response = httpClient.execute(request);

				HttpEntity httpEntity = response.getEntity();
				InputStream is = httpEntity.getContent();           

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				int number_of_question =0;
				while ((line = reader.readLine()) != null ) 
				{
					Log.v("GetQuestionSetFromServer",line);

					String output="";
					output += line;
					String[] ques = output.split("@@");
					number_of_question++;

					Log.v("ques[0]", ques[0]);

					questionMap.put(number_of_question, ques);

				}
				question_count= number_of_question;
				is.close();
				reader.close();
				is = null;
				reader = null;
				httpEntity = null;
				response = null;
				request = null;
				httpClient = null;

				//return output;
			}
			catch(Exception ex)
			{				
				return false;
				//return null;
			}
			return true;

			//return null;

		}       

		@Override
		protected void onPostExecute(Boolean result)
		{
			if(result == false)
			{
				super.onPostExecute(result);
				progressDialog.dismiss();

				Toast.makeText(getApplicationContext(), "No internet connectivity ", Toast.LENGTH_LONG).show();
				finish();
			}
			else
			{
				super.onPostExecute(result);
				progressDialog.dismiss();
				printMapContent();
				getQuestion(current_qno);

				adapter.notifyDataSetChanged();
			}
		}

	}

	public int getIndex(String ans)
	{
		int index =0;
		if (ans.equalsIgnoreCase("a"))
			index = 1;
		else if (ans.equalsIgnoreCase("b"))
			index = 2; 
		else if (ans.equalsIgnoreCase("c"))
			index = 3;
		else if (ans.equalsIgnoreCase("d"))
			index = 4;
		else
			index = -1;

		return index;
	}
	public String[] setquestionAnswerPair(List<String> userSubmitList)
	{	
		String[] question_answer_pair = new String[question_count];

		int i = 0;
		int index =0;
		try{
			while (i < question_count)
			{	
				String ans = practicevalues.get(5);

				index = getIndex(ans);

				if(index !=0)
				{
					getQuestion(i);
					String temp = "Ques "+(i+1)+": "+practicevalues.get(0)+ "\n"
							+"Ans: "+practicevalues.get(index)+"\n"+
							"Your Answer :"+practicevalues.get(getIndex(userSubmitList.get(i)))+"\n"; 
					question_answer_pair[i] = (temp);
				}
				else
				{
					String temp = "Ques "+(i+1)+": "+practicevalues.get(0)+ "\n"
							+"Ans: "+practicevalues.get(index)+"\n"+
							"you did not answered this question \n"; 
					question_answer_pair[i] = (temp);
				}

				i++;
			}
		}
		catch(Exception ex)
		{	ex.fillInStackTrace();
		Log.v("EXCEPTION",ex.getMessage());
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Can not find your score at that moment . please try again");
		builder.setPositiveButton("Okay", null);
		//AlertDialog alert = builder.create();
		builder.show();
		}
		return question_answer_pair;

	}

	public void start_score_page()
	{
		Intent viewScore = new Intent(PracticeActivity.this, ViewScore.class);
		viewScore.putExtra("sourcepage", "practiceactivity");
		viewScore.putExtra("score", score);
		viewScore.putExtra("no_of_question", question_count);
		;
		viewScore.putExtra("question_answers", setquestionAnswerPair(userSubmitList));
		startActivity(viewScore);
	}


	public void getQuestion(int qno)
	{
		Log.v("getQuestion", "--------getQuestion-------START");
		
		Iterator<Entry<Integer, String[]>> it = questionMap.entrySet().iterator();
		String[] temp = new String[10];
		try{
			while(it.hasNext())

			{	
				Entry<Integer, String[]> entry  = it.next();

				temp = (String [])entry.getValue();
				int key = entry.getKey();
				Log.v("Getquestion"," "+key);
				if( key == qno)
				{
					practicevalues.add("Ques"+ current_qno +": "+temp[0].trim());
					practicevalues.add("(A) "+temp[1].trim());
					practicevalues.add("(B) "+temp[2].trim());
					practicevalues.add("(C) "+temp[3].trim());
					practicevalues.add("(D) "+temp[4].trim());
					uttar = temp[5].trim();
					break;
					//return values;
				}

			}

			Log.v("getQuestion", temp[0] + temp[1] + temp[2]+ temp[3]+temp[4]+temp[5]);
		}
		catch(Exception ex)
		{
			practicevalues.clear();
			practicevalues.add("unable to find question from server");
			practicevalues.add("-- ");
			practicevalues.add("---");
			practicevalues.add("--- ");
			practicevalues.add("--- ");

			//return values;
		}
		//return null;
	}
	public void printMapContent()
	{
		Iterator<Entry<Integer, String[]>> it = questionMap.entrySet().iterator();

		while(it.hasNext())

		{	
			Entry<Integer, String[]> entry  = it.next();

			String[] temp = (String [])entry.getValue();
			Log.v("printMapContent",  " "+entry.getKey());
			Log.v("printMapContent",   temp[0]);
			Log.v("printMapContent",  temp[1]);
			Log.v("printMapContent",   temp[2]);
			Log.v("printMapContent",   temp[3]);
			Log.v("printMapContent",   temp[4]);
			Log.v("printMapContent",   temp[5]);
		}
	}



}
