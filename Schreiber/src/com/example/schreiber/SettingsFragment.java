package com.example.schreiber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;



import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;









import android.content.SharedPreferences;

import com.example.schreiber.util.ExternalDbOpenHelper;
import com.example.schreiber.util.Message;

public class SettingsFragment extends Fragment {

	private SQLiteDatabase database;
	private ExternalDbOpenHelper dbOpenHelper;
	private Button btnView, btnActivateAll, btnDeactivateAll;
	private ToggleButton togglePercentualMatch;
	private EditText txtPercentage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment_layout, container, false);
		dbOpenHelper = new ExternalDbOpenHelper(view.getContext(), ExternalDbOpenHelper.DB_NAME);
		database = dbOpenHelper.openDataBase();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);		
		Button btnView = (Button) getActivity().findViewById(R.id.btnView);
		Button btnActivateAll = (Button) getActivity().findViewById(R.id.btnActivateAll);
		Button btnDeactivateAll = (Button) getActivity().findViewById(R.id.btnDeactivateAll);
		final ToggleButton togglePercentualMatch = (ToggleButton) getActivity().findViewById(R.id.togglePercentualMatch);
		final EditText txtPercentage = (EditText) getActivity().findViewById(R.id.txtPercentage);
		
		togglePercentualMatch.setChecked(preferences.getBoolean("togglePercentualMatchPref", true)); // retrieve the state of toggle button from shared pref
		txtPercentage.setEnabled(preferences.getBoolean("percentualMatch", false));// retrieve the enable state of txt from pref
		txtPercentage.setText(String.valueOf((preferences.getInt("percentage", 80))));
		
		btnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new ViewAllFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();
				
				
			}
		});

		btnActivateAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
				builder.setTitle("Activate all");
		        builder.setMessage("Are you sure you want to activate all phrases?");
		        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface arg0, int arg1) {
		            	dbOpenHelper.resetKnown(0);
						Message.messsage(getView().getContext(), "Everything was activated");
		            }
		          });
		        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface arg0, int arg1) {
		            }
		          });
		        builder.show(); 
			}
		});
	
		btnDeactivateAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
				builder.setTitle("Deactivate all");
		        builder.setMessage("Are you sure you want to deactivate all phrases?");
		        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface arg0, int arg1) {
		            	dbOpenHelper.resetKnown(1);
						Message.messsage(getView().getContext(), "Everything was deactivated");
		            }
		          });
		        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface arg0, int arg1) {
		            }
		          });
		        builder.show(); 
			}
		});
		
		togglePercentualMatch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				  SharedPreferences.Editor editor = preferences.edit();
				if (isChecked) {
					txtPercentage.setEnabled(true);
					editor.putBoolean("percentualMatch",true); // value to store
					
				} else {
					txtPercentage.setEnabled(false);
					editor.putBoolean("percentualMatch",false); // value to store
				}
				editor.commit();
				
			}
		});
		
		txtPercentage.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SharedPreferences.Editor editor = preferences.edit();
				if(!txtPercentage.getText().toString().isEmpty()){
				editor.putInt("percentage", Integer.parseInt(txtPercentage.getText().toString()));
				editor.commit();
				}
			}
		});
		
//		togglePercentualMatch.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				 SharedPreferences.Editor editor = preferences.edit();
//				if (togglePercentualMatch.isChecked()) {
//					txtPercentage.setEnabled(true);
//					editor.putBoolean("percentualMatch",true); // value to store
//					editor.putInt("percentage", Integer.parseInt(txtPercentage.getText().toString()));
//				} else {
//					txtPercentage.setEnabled(false);
//					editor.putBoolean("percentualMatch",false); // value to store
//				}
//			}
//		});
		
		
		togglePercentualMatch.setOnClickListener(new OnClickListener()
		{
		    @Override
		    public void onClick(View v)
		    {
		       SharedPreferences.Editor editor = preferences.edit();
		       editor.putBoolean("togglePercentualMatchPref", togglePercentualMatch.isChecked()); // value to store
		       editor.commit();
		    }
		});
	}

}