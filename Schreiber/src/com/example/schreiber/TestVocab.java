package com.example.schreiber;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.example.schreiber.util.ExternalDbOpenHelper;
import com.example.schreiber.util.Message;
import com.example.schreiber.util.Sentence;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class TestVocab extends Fragment {

	private SQLiteDatabase database;
	private ArrayList<Sentence> sentenceList;
	private int counter = 0;
	private int sentenceListLength; // how many record was loaded from db

	private Button  btnCheck;
	private ProgressBar progressBar;
	private TextView txtFrom, txtGerman, txtCorrected, txtProgressStatus, txtActualPercentageMatch;
	private EditText editTextTranslation;
	private ExternalDbOpenHelper dbOpenHelper;
	private ImageView imageAnswer;
	private SharedPreferences preferences;
	private CheckBox checkboxPercentualMatch;
	private boolean useMatch;
	private double percentage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.test_vocab, container, false);
		btnCheck = (Button) view.findViewById(R.id.btnCheck);
		txtFrom = (TextView) view.findViewById(R.id.txtFrom);
		txtCorrected = (TextView) view.findViewById(R.id.txtCorrect);
		txtGerman = (TextView) view.findViewById(R.id.txtGerman);
		txtGerman.setVisibility(View.INVISIBLE);
		editTextTranslation = (EditText) view.findViewById(R.id.editTextTranslation);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		txtProgressStatus = (TextView) view.findViewById(R.id.txtProgressStatus);
		imageAnswer = (ImageView) view.findViewById(R.id.imageViewAnswer);
		checkboxPercentualMatch = (CheckBox) view.findViewById(R.id.checkboxPercentualMatch);
		txtActualPercentageMatch = (TextView) view.findViewById(R.id.txtActualPercentageMatch);
		
		dbOpenHelper = new ExternalDbOpenHelper(view.getContext(), ExternalDbOpenHelper.DB_NAME);
		database = dbOpenHelper.openDataBase();
		String topicName = null;
		preferences = getActivity().getPreferences(Context.MODE_PRIVATE);	
		useMatch = preferences.getBoolean("percentualMatch",false);
		percentage = preferences.getInt("percentage",99);
		
		Bundle bundle = this.getArguments();
		if(bundle != null){
			topicName = bundle.getString("topic", "Topic was not selected");
		}
		loadSentences(topicName);

		//only if there are some UNKNOWN sentences
		if (sentenceListLength > 0){
			txtFrom.setText(sentenceList.get(counter).getFrom().toString());
			txtGerman.setText(sentenceList.get(counter).getTo().toString());
			progressBar.setProgress(counter*100/sentenceListLength);
			txtProgressStatus.setText(counter + "/" +  sentenceListLength);
		}else{
			Fragment newFragment = new DetailFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Bundle bundle2 = new Bundle();
			bundle2.putString("message", "You know everything. Activate some topic");
			newFragment.setArguments(bundle2);

			// Replace the fragment_container view with this fragment,
			// and add the transaction to the back stack
			transaction.replace(R.id.mainContent, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
		txtGerman.setVisibility(View.INVISIBLE);
		editTextTranslation.setText("");
		txtCorrected.setText("");
		checkboxPercentualMatch.setChecked(useMatch);
		if (useMatch) {
			checkboxPercentualMatch.setText(Double.toString(percentage) + " %");
		} else {
			checkboxPercentualMatch.setText("% OFF");
		}
		

		// Check and Continue
		btnCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(btnCheck.getText().equals("Check")){
					check();
					btnCheck.setText("Continue");
				}else{
					imageAnswer.setImageResource(0);
					btnCheck.setText("Check");
					if (counter < sentenceListLength -1) {
						counter++;
						progressBar.setProgress(counter*100/sentenceListLength);
						txtProgressStatus.setText(counter + "/" +  sentenceListLength);
					} else {
						Toast.makeText(getView().getContext(), "We are at the end", Toast.LENGTH_LONG).show();
						progressBar.setProgress(progressBar.getMax());
						txtProgressStatus.setText(sentenceListLength + "/" +  sentenceListLength);
						txtFrom.setVisibility(View.INVISIBLE);
					}
					txtGerman.setVisibility(View.INVISIBLE);
					txtFrom.setText(sentenceList.get(counter).getFrom().toString());
					txtGerman.setText(sentenceList.get(counter).getTo().toString());
					editTextTranslation.setText("");
					txtCorrected.setText("");

					
					
				}
				
			}
		});

		// watcher for disabling the check button if nothing is written
		editTextTranslation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String ed_text = editTextTranslation.getText().toString().trim();
				if ((ed_text.isEmpty() || ed_text.length() == 0 || ed_text.equals(""))) {
					btnCheck.setEnabled(false);
				} else {
					btnCheck.setEnabled(true);
				}
			}
		});


		return view;
	}

	// method for closing the keyboard, is called when drawer is opened
	public static void closeKeyboard(Context c, IBinder windowToken) {
		InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(windowToken, 0);
	}

	// ---------------------- DATABASE PART ------------------------//

	private void loadSentences(String topicName) {
		Cursor cursor;
		sentenceList = new ArrayList<Sentence>();
		//read just records NOT marked as known --> KNOWN = 0;
		cursor = database.query(ExternalDbOpenHelper.TABLE_NAME, new String[] { ExternalDbOpenHelper.ID,ExternalDbOpenHelper.SLOVAK, ExternalDbOpenHelper.GERMAN,
				ExternalDbOpenHelper.TOPIC, ExternalDbOpenHelper.KNOWN }, "KNOWN = ? AND TOPIC = ?", new String[]{Integer.toString(0), topicName}, null, null, ExternalDbOpenHelper.ID);//sort by ID, so the counter
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				int id = cursor.getInt(0);
				String sentenceSlovak = cursor.getString(1);
				String sentenceGerman = cursor.getString(2);
				String topic = cursor.getString(3);
				int skill = cursor.getInt(4);
				Sentence sentence = new Sentence(id, sentenceSlovak, sentenceGerman, topic, skill);
				sentenceList.add(sentence);
			} while (cursor.moveToNext());
		}
		cursor.close();
		// setting the size of records, use for scrolling in Frontend
		sentenceListLength = sentenceList.size();
	}

	// ---------- HELPER --------------------------//

	/**
	 * check the translation, place the ICON, write result to DB if success and highlight the errors
	 */
	private void check() {
		txtGerman.setVisibility(View.VISIBLE);
		String original = txtGerman.getText().toString();
		String translation = editTextTranslation.getText().toString();
		List<Integer> mistakeIndexes = new ArrayList<Integer>();
		SpannableString text = new SpannableString(translation);

		String[] originalArray = original.split("(?!^)");
		String[] translationArray = translation.split("(?!^)");
		int commonLetters = originalArray.length - translationArray.length;

		// original is longer
		if (commonLetters >= 0) {
			for (int i = 0; i < originalArray.length - commonLetters; i++) {
				if (!originalArray[i].equals(translationArray[i])) {
					mistakeIndexes.add(i);
				}
			}
			// translation is longer
		} else {
			for (int i = 0; i < originalArray.length; i++) {
				if (!originalArray[i].equals(translationArray[i])) {
					mistakeIndexes.add(i);
				}
			}
			for (int j = translationArray.length + commonLetters; j < translationArray.length; j++) {
				mistakeIndexes.add(j);
			}
		}

		for (Integer i : mistakeIndexes) {
			text.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, 0);
		}
		
		// text.setSpan(new StrikethroughSpan(), 12, 17, 0);

			
		if(translationArray.length!=originalArray.length){
			mistakeIndexes.add(1);
		}
		double match = Math.round( percentualMatch(original, translation) * 100.0 ) / 100.0; ; // get % of the match between 2 lists
		
		Log.i("TOKENYY", "percentualMatchPref= "+ Boolean.toString(useMatch));
		Log.i("TOKENYY", "percentage= "+ Double.toString(percentage));
		
		// display a change the color of the current %
		if (useMatch) {
			txtActualPercentageMatch.setText(Double.toString(match) + " %");
			if (match > percentage) {
				txtActualPercentageMatch.setTextColor(Color.GREEN);
			} else {
				txtActualPercentageMatch.setTextColor(Color.RED);
			}
			
		}
		
		// show styled text into the TextView
		txtCorrected.setText(text, BufferType.SPANNABLE);
		// if NO errors or (percentualMatch is ON and match is greater than percentage set in settings)
		if(mistakeIndexes.isEmpty() || (useMatch && (match > percentage))){
			imageAnswer.setImageResource(R.drawable.correct);
			int idToSet = sentenceList.get(counter).getId(); // id of the row, to which set KNOWN flag to true
			dbOpenHelper.updateKnown(idToSet, 1); // update database if answer was Correct
		}else{
			imageAnswer.setImageResource(R.drawable.incorrect);
		}
		
		percentualMatch(original, translation);

	}
	
	/**
	 * Remove , and . and calculate the % of matched words in original and translation texts
	 * 
	 * @param original
	 * @param translation
	 */
	private double percentualMatch(String original, String translation ){
		original = original.replaceAll("[,.]", "");
		translation = translation.replaceAll("[,.]", "");
		double percentualMatch;
		
		List<String> tokenOriginal = new ArrayList<>(Arrays.asList(original.split("\\s")));
		List<String> tokenTranslation = new ArrayList<>(Arrays.asList(translation.split("\\s")));
		
		double numberOfMatches=0;
		
		for (int i = 0; i < tokenTranslation.size(); i++) {
			if (tokenOriginal.contains(tokenTranslation.get(i))){
				numberOfMatches++;
			}
		}
		percentualMatch = 100 * (numberOfMatches / tokenOriginal.size()) ;
		Log.i("TOKENY", "numberOfMatches " + numberOfMatches + ", percentualna zhoda " + percentualMatch + " %");
		
		return percentualMatch;
	}
	

}