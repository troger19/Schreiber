package com.example.schreiber;

import java.text.ChoiceFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.schreiber.util.ExternalDbOpenHelper;
import com.example.schreiber.util.Message;
import com.example.schreiber.util.Sentence;

public class ActivateSentences extends Fragment {

	String names[];
	private ExternalDbOpenHelper dbOpenHelper;
	private SQLiteDatabase database;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activate_fragment_list_layout, container, false);
		dbOpenHelper = new ExternalDbOpenHelper(view.getContext(), ExternalDbOpenHelper.DB_NAME);
		database = dbOpenHelper.openDataBase();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<String> slovakSentences = new ArrayList<String>();
		final Map<Integer, String> mapa = new HashMap<>();
		List<Sentence> sentences = new ArrayList<Sentence>();
		String topicName = null;

		ListView listView = (ListView) getActivity().findViewById(R.id.listView1);
		
		Bundle bundle = this.getArguments();
		if(bundle != null){
			topicName = bundle.getString("topic", "Topic was not selected");
		}
		
		sentences = dbOpenHelper.getAllSentence(topicName);
		for (int i = 0; i < sentences.size(); i++) {
			slovakSentences.add(sentences.get(i).getFrom()); // fill all the sentences to listView
			mapa.put(sentences.get(i).getId(), sentences.get(i).getFrom());
		}

		listView.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, slovakSentences));
		listView.setChoiceMode(2);

		// must run second time, after the listView is created
		for (int i = 0; i < sentences.size(); i++) {
			if (sentences.get(i).getKnown() == 1) {
				listView.setItemChecked(i, true); // check the box with KNOWN
			}
		}

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 final String item = (String) parent.getItemAtPosition(position); 
				 int resetId = Message.getKeyByValue(mapa, item);
				 boolean tag= ((ListView)parent).isItemChecked(position);
				 dbOpenHelper.updateKnown(resetId, tag ? 1:0);
			}
		});

	}


}