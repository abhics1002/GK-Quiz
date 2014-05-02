package com.abhi.gk;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Test_questions extends Activity {

	ListView listView;
	Cursor result;
	int number_of_question = 1;
	public int max_numberOfQuestion = 0;
	public String quiz_category = "ALL";
	int score = 0 ;
	List<String> values = new ArrayList<String>();
	List<String> user_answer_submit = new ArrayList<String>();
	boolean practiceMode = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_questions);

		listView = (ListView) findViewById(R.id.list);

		// Defined Array values to show in ListView
		// need to pass values from database.
		user_answer_submit.clear();

		showQuizSettings();
		initpreferences();
		getquestionfromdb();


		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// code block for starting animation - START		
		Animation animation = null;
		animation = AnimationUtils.loadAnimation(this, R.anim.left_to_right);

		animation.setDuration(500);
		listView.startAnimation(animation);
		animation = null;

		//  code block for starting animation - END		
		// Assign adapter to ListView
		listView.setAdapter(adapter); 

		// ListView Item Click Listener

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item index
				int itemPosition     = position;

				// ListView Clicked item value
				String  itemValue    = (String) listView.getItemAtPosition(position);

				if(itemPosition > 0)
				{
					Toast.makeText(getApplicationContext(), "you answered : " +itemValue , Toast.LENGTH_LONG).show();
					switch(itemPosition)
					{
					case 1:
						user_answer_submit.add("a");
						break;
					case 2:
						user_answer_submit.add("b");
						break;
					case 3:
						user_answer_submit.add("c");
						break;
					case 4:
						user_answer_submit.add("d");
						break;
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "You have skipped this Question.", Toast.LENGTH_LONG).show();
					user_answer_submit.add("Did not answered");
				}

				String uttar = result.getString( result.getColumnIndex("answer"));
				
				int answer_no= 0;

				if (uttar.equals("a"))
					answer_no = 1;
				else if (uttar.equals("b"))
					answer_no = 2; 
				else if (uttar.equals("c"))
					answer_no = 3;
				else if (uttar.equals("d"))
					answer_no = 4;

				// calculating score 
				if (itemPosition == answer_no){
					score ++; 
				}
				else 
				{
					Log.v("SCORE", "your answer is wrong");
				}

				result.moveToNext();

				Log.v("number_of_question", Integer.toString(number_of_question));
				if( number_of_question <= max_numberOfQuestion )
				{  
					adapter.clear();
					showQuestion(result);
					adapter.notifyDataSetChanged();
				}
				else if (number_of_question >= max_numberOfQuestion + 1)
				{
					// start score Activity.
					start_score_page();
				}
			}
		});

	}

	public void start_score_page()
	{
		Intent viewScore = new Intent(Test_questions.this, ViewScore.class);
		viewScore.putExtra("sourcepage", "Test_question");
		viewScore.putExtra("score", score);

		viewScore.putExtra("question_answers", getquestionAnswer(result, user_answer_submit));
		//    	viewScore.putExtra("question_answers", question_answer);

		viewScore.putExtra("no_of_question", number_of_question-1);
		result.close();
		startActivity(viewScore);
	}

	private void showQuizSettings() 
	{
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		String numOfQuestion = sharedPrefs.getString("numOfQuestion", "NULL");
		try{		
			max_numberOfQuestion = Integer.parseInt(numOfQuestion);
		}
		catch(NumberFormatException e){
			max_numberOfQuestion = 10; // setting default values to 10 if any exception occurs (if user enter string. )
		}

		if(max_numberOfQuestion <=1)
		{
			max_numberOfQuestion = 10;	// setting default value if one enter negative or zero questions.
		}
		else if (max_numberOfQuestion > 25)
		{
			max_numberOfQuestion = 25;
		}

		quiz_category = sharedPrefs.getString("prefcategory", "ALL");
	}

	public void initpreferences()
	{
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		if (prefs.getString("QUESTIONS_OLD", "").equals(""))
		{
			prefs.edit().putString("QUESTIONS_OLD", Integer.toString(0)).commit();
		}
	}

	public void setquestionsPreferences(int question_id)
	{	
		Log.v("API", "setquestionsPreferences ------------------------START ");
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String savedString = prefs.getString("QUESTIONS_OLD", "");

		String str = ","+ Integer.toString(question_id);
		savedString = savedString + str;
		prefs.edit().putString("QUESTIONS_OLD", savedString).commit();
		Log.v("API", "setquestionsPreferences ------------------------ END ");
	}

	public boolean Is_database_present()
	{	//boolean flag = false;
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String savedString = prefs.getString("Is_database_present", "0");
		if(savedString.equals("0"))
		{
			prefs.edit().putString("Is_database_present", "1").commit();
			return false;
		}
		return true;
	}
	public int numberOfQuestionSetByUser()
	{
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String num = prefs.getString("NUMBER_OF_QUESTION_SET_BY_USER", "");
		if (num.equals(""))
		{	// if no preferences set then return 10 default value
			return 10;
		}

		return Integer.parseInt(num);

	}

	//return string of question-answer use to show result to user.
	public String[] getquestionAnswer(Cursor ques_answer, List<String> user_answer_submit)
	{	
		String[] question_answer_pair = new String[max_numberOfQuestion];
		ques_answer.moveToFirst();
		int i = 0;
		try{
			while (i < max_numberOfQuestion)
			{	
				String ans = user_answer_submit.get(i);
				String temp, temp1;
				temp1= ques_answer.getString( ques_answer.getColumnIndex("answer")).trim().toLowerCase();
				if(ans == "a" || ans =="b" || ans =="c" || ans == "d")
				{
					temp = "Ques "+(i+1)+": "+ques_answer.getString( ques_answer.getColumnIndex("question") )+ "\n"
							+"Ans: "+ques_answer.getString( ques_answer.getColumnIndex(temp1))+"\n"+
							"Your Answer :"+ques_answer.getString( ques_answer.getColumnIndex(ans))+"\n"; 
					question_answer_pair[i] = (temp);
				}
				else
				{
					temp = "Ques "+(i+1)+": "+ques_answer.getString( ques_answer.getColumnIndex("question") )+ "\n"
							+"Ans: "+ques_answer.getString( ques_answer.getColumnIndex(temp1))+"\n"+
							"you did not answered this question \n"; 
					question_answer_pair[i] = (temp);
				}
				ques_answer.moveToNext();
				i++;
			}
		}
		catch(Exception ex)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Can not find your score at that moment . please try again");
			builder.setPositiveButton("Okay", null);
			builder.show();
		}
		return question_answer_pair;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}

	public void getquestionfromdb()
	{	
		Log.v("API","getquestionfromDB-----------START");

		DatabaseConnector databaseConnector = new DatabaseConnector(this);

		databaseConnector.open();

		// 		use to get question without preferences.
		//    	result = databaseConnector.getQuestions();
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);

		result = databaseConnector.getQuestionsUsingPreferences(prefs.getString("QUESTIONS_OLD", ""), quiz_category , max_numberOfQuestion);
		if(result.getCount() < max_numberOfQuestion)
		{
			clearPreferences();
			result = databaseConnector.getQuestionsUsingPreferences(prefs.getString("QUESTIONS_OLD", ""),"ALL" , max_numberOfQuestion);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("there is no questions available for selected category. please choose a different category. serving questions from General all questions category for now.");
			builder.setPositiveButton("Okay", null);
			//AlertDialog alert = builder.create();
			builder.show();
		}
		if(result == null)
		{	
			clearPreferences();
			result = databaseConnector.getQuestions();
		}

		result.moveToFirst();
		showQuestion(result);
		databaseConnector.close();
	}



	public void clearPreferences()
	{	
		Log.v("API", "clearPreferences ---------------START");
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		prefs.edit().putString("QUESTIONS_OLD", Integer.toString(0)).commit();
		showPreferences();
	}

	public void showPreferences()
	{
		Log.v("API", "showPreferences ---------------START");
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String savedPref = sharedPreferences.getString("QUESTIONS_OLD", "");
		Log.v("preferences", savedPref);
		Log.v("API", "showPreferences ---------------END");
	}

	public void showQuestion(Cursor ques)
	{	

		int question_id = ques.getInt(ques.getColumnIndex("question_id"));

		String question = "Ques "+ number_of_question +": "+ ques.getString( ques.getColumnIndex("question") );
		String option_a = "(A) "+ques.getString( ques.getColumnIndex("a"));
		String option_b = "(B) "+ques.getString( ques.getColumnIndex("b"));
		String option_c = "(C) "+ques.getString( ques.getColumnIndex("c"));
		String option_d = "(D) "+ques.getString( ques.getColumnIndex("d"));

		values.add(question);
		values.add(option_a);
		values.add(option_b);
		values.add(option_c);
		values.add(option_d);

		number_of_question++;

		Log.v("API", "showQuestion ------START "+Integer.toString(question_id));
		setquestionsPreferences(question_id);

		showPreferences();
	}

	public void showQuestion(int practice_set_id)
	{	

	}

	protected void onResume()
	{
		super.onResume();
	}



	class GetContactTask extends AsyncTask<Object,Object,Cursor>
	{
		DatabaseConnector databaseConnector = new DatabaseConnector(Test_questions.this);

		protected void onPostExecute(Cursor result) 
		{
			//  contactAdapter.changeCursor(result);
			result.moveToFirst();
			values.add(result.getString( result.getColumnIndex("question") ));
			values.add(result.getString( result.getColumnIndex("a")) );
			values.add(result.getString( result.getColumnIndex("b")) );
			values.add(result.getString( result.getColumnIndex("c")) );
			values.add(result.getString( result.getColumnIndex("d")) );
			values.add(result.getString( result.getColumnIndex("answer")) );
			databaseConnector.close();
		}

		@Override
		protected Cursor doInBackground(Object... params) 
		{
			// TODO Auto-generated method stub
			databaseConnector.open();
			return databaseConnector.getQuestions();

		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_test_questions, menu);
		return true;
	}


	public boolean onOptionsItemSelected(MenuItem item)
	{
		String question= "Ques :"+ result.getString( result.getColumnIndex("question")) +"\n"+"(A)"+ result.getString( result.getColumnIndex("a")) +"\n"+"(B)"+result.getString( result.getColumnIndex("b")) 
				+"\n"+"(C)"+ result.getString( result.getColumnIndex("c")) +"\n"+"(D)"+ result.getString( result.getColumnIndex("d"));

		switch( item.getItemId())
		{
		case R.id.menu_settings:
		{
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("message/rfc822");

			String wrong_question = question + "\n\n"+ "i feel this question is wrong." + "\n\nwrite what is wrong with this question.";
			sharingIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"appbytes8@gmail.com"});
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporting question[GENERAL KNOWLEDGE QUIZ]");
			sharingIntent.putExtra(Intent.EXTRA_TEXT   , wrong_question);

			try 
			{
				startActivity(Intent.createChooser(sharingIntent, "Send mail..."));
			} 
			catch (android.content.ActivityNotFoundException ex) 
			{
				Toast.makeText(Test_questions.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
			return false;
		}
		case R.id.share_fb:
		{
			String share_link = question+"\n for more questions. visit :"+"https://play.google.com/store/apps/details?id=com.abhi.gk";
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, share_link);
			startActivity(Intent.createChooser(intent, "Share with"));
			return false;
		}
		}
		return false;
	}


	public void onBackPressed() 
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(Test_questions.this);
		alert.setTitle(R.string.viewscore_alert_box_title);
		alert.setMessage(R.string.test_question_alert_box_msg);

		alert.setNegativeButton(R.string.alert_negative_button, null);

		alert.setPositiveButton(R.string.exit_alert_positive_button, 
				new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				finish();
			}

		});
		alert.show();
	}
}
