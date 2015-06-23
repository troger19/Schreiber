package com.example.schreiber;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.schreiber.util.ExternalDbOpenHelper;
import com.example.schreiber.util.Sentence;

public class ViewAllFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "viewAllFragment";
	private ExternalDbOpenHelper dbOpenHelper;
	private SQLiteDatabase database;
	ArrayAdapter<String> adapter;
	List<Sentence> allSentences = new ArrayList<Sentence>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, container, false);
		dbOpenHelper = new ExternalDbOpenHelper(view.getContext(), ExternalDbOpenHelper.DB_NAME);
		database = dbOpenHelper.openDataBase();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<String> slovakSentences = new ArrayList<String>();
		List<String> sentences = new ArrayList<String>();

		ListView listView = (ListView) getActivity().findViewById(R.id.listOfTopics);
		listView.setOnItemClickListener(this);
		EditText search = (EditText) getActivity().findViewById(R.id.inputSearchTopics);

		sentences = dbOpenHelper.getAllSlovakList();
		allSentences = dbOpenHelper.getAllSentence();
		for (int i = 0; i < sentences.size(); i++) {
			slovakSentences.add(sentences.get(i));
		}

		adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, slovakSentences);
		listView.setAdapter(adapter);

		// Enabling Search Functionality
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				ViewAllFragment.this.adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String item = parent.getItemAtPosition(position).toString();
		Log.i(TAG, findGerman(item));
		Toast.makeText(getActivity(), findGerman(item), Toast.LENGTH_LONG).show();
	}

	private String findGerman(String slovak) {
		String germanWord;
		for (Sentence sentence : allSentences) {
			if (sentence.getFrom().equals(slovak)) {
				germanWord = sentence.getTo();
				return germanWord;
			}
		}
		return "No german phrase";
	}

}