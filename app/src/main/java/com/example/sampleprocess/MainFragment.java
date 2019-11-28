package com.example.sampleprocess;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.smapleprocess.R;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // done: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";
    private static final String MY_SHARED_PREFERENCES = "preferences";
    private static final String SAVED_IP = "ip";
    private static final String PORT = "port";
    private static final String STATUS_WORD = "statusWord";
    private static final String CONTROL_WORD = "controlWord";

    // done: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;
    public EditText mIPEditText;
    public EditText mPortEditText;
    public EditText mSWRegisterEditText;
    public EditText mCWRegisterEditText;
    public TextView mPingLabel;
    public String IP;
    public String port;
    public String statusWord;
    public String controlWord;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //    mParam1 = getArguments().getString(ARG_PARAM1);
        //    mParam2 = getArguments().getString(ARG_PARAM2);
        //}

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIPEditText = (EditText) view.findViewById(R.id.IP_input_editText);
        mPortEditText = (EditText) view.findViewById(R.id.port_input_editText);
        mSWRegisterEditText = (EditText) view.findViewById(R.id.device_SW_addr_editText);
        mCWRegisterEditText = (EditText) view.findViewById(R.id.device_CW_addr_editText);
        mPingLabel = (TextView) view.findViewById(R.id.ping_result_textView);

        getPreferences();

        Button mSaveSettingButton = (Button) view.findViewById(R.id.save_settings_button);
        mSaveSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClick(view);
            }
        });

        final Button mPing = (Button) view.findViewById(R.id.ping_ip_button);
        mPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PingAddress().execute(IP);
                mPingLabel.setText("Pinging.");
            }
        });
    }

    //Saving preferences
    public void onSaveButtonClick(View v){
        SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
        IP = mIPEditText.getText().toString();
        port = mPortEditText.getText().toString();
        statusWord = mSWRegisterEditText.getText().toString();
        controlWord = mCWRegisterEditText.getText().toString();
        editor.putString(SAVED_IP, IP);
        editor.putString(PORT, port);
        editor.putString(STATUS_WORD, statusWord);
        editor.putString(CONTROL_WORD, controlWord);
        editor.commit();
    }

    //Loading preferences
    public void getPreferences(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        IP = sharedPreferences.getString(SAVED_IP, getContext().getResources().getString(R.string.ip_input_default_text));
        port = sharedPreferences.getString(PORT, getContext().getResources().getString(R.string.port_input_default_text));
        statusWord = sharedPreferences.getString(STATUS_WORD, getContext().getResources().getString(R.string.device_SW_reg_val_default_text));
        controlWord = sharedPreferences.getString(CONTROL_WORD, getContext().getResources().getString(R.string.device_CW_reg_val_default_text));

        mIPEditText.setText(IP);
        mPortEditText.setText(port);
        mSWRegisterEditText.setText(statusWord);
        mCWRegisterEditText.setText(controlWord);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public class PingAddress extends AsyncTask<String, Void, Void> {


        boolean reachable = false;

        public PingAddress() {
        }

        @Override
        protected Void doInBackground(String... ipAddress) {
            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -w 1 -c 1 " + ipAddress[0]);
                int exitValue = ipProcess.waitFor();
                reachable = exitValue == 0;
                ipProcess.destroy();
            } catch (IOException e)          { e.printStackTrace(); }
            catch (InterruptedException e) { e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            mPingLabel.setText("Pinging...");
            mPingLabel.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (reachable){
                mPingLabel.setText("Reachable.");
            } else {
                mPingLabel.setText("Not Reachable.");
            }
        }
    }
}
