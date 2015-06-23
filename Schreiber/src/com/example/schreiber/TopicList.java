package com.example.schreiber;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TopicList extends Fragment {

	String topicList[]; // names of the topic
	EditText inputSearch;
	ArrayAdapter<String> adapter;
	int root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ListView listView = (ListView) getActivity().findViewById(R.id.listOfTopics);
		EditText search = (EditText) getActivity().findViewById(R.id.inputSearchTopics);
		topicList = getResources().getStringArray(R.array.topic_array);
		// listView.setAdapter(new ArrayAdapter(getActivity(),
		// android.R.layout.simple_list_item_multiple_choice, topicList));
		// listView.setChoiceMode(2);
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.topic_name, topicList);
		listView.setAdapter(adapter);

		Bundle bundle = this.getArguments();
		if (bundle != null) {
			root = bundle.getInt("root", 0);
		}

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				Fragment newFragment = null;
				switch (root) {
				case 0:
					newFragment = new TestVocab();
					break;
				case 1:
					newFragment = new ActivateSentences();
					break;
				default:
					break;
				}

				// Create new fragment and transaction

				FragmentTransaction transaction = getFragmentManager().beginTransaction();

				Bundle bundle = new Bundle();
				bundle.putString("topic", item);
				newFragment.setArguments(bundle);

				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.replace(R.id.mainContent, newFragment);
				transaction.addToBackStack("topicList");

				// Commit the transaction
				transaction.commit();
			}
		});

		// Enabling Search Functionality
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				TopicList.this.adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

	}

}