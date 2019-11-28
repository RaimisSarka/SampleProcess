package com.example.sampleprocess;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smapleprocess.R;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProcessFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProcessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcessFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    ///private static final String ARG_PARAM1 = "param1";
   // private static final String ARG_PARAM2 = "param2";
    private static final String MY_SHARED_PREFERENCES = "preferences";
    private static final String SAVED_IP = "ip";
    private static final String PORT = "port";
    private static final String STATUS_WORD = "statusWord";
    private static final String CONTROL_WORD = "controlWord";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;
    private Handler handler;

    public EditText mStatusWordRes;
    public EditText mControlWordRes;
    public TextView mCycleReadIndication;
    public String IP;
    public String port;
    public String statusWord;
    public String controlWord;
    public boolean cycleRead = false;


    private OnFragmentInteractionListener mListener;

    class CycleReadModbus implements Runnable{
        boolean allways = true;

        @Override
        public void run() {
            while (allways){
                try {
                    if (cycleRead) {
                        handler.obtainMessage(1).sendToTarget();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ie){
                    Log.d("Smaple", ie.getMessage());
                }
            }
        }
    }

    class UpdateModbusWords extends Handler{

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                readDataFromModbus();
            }
        }
    }


    public ProcessFragment() {
        // Required empty public constructor
    }

    public static ProcessFragment newInstance(String param1, String param2) {
        ProcessFragment fragment = new ProcessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStatusWordRes = (EditText) view.findViewById(R.id.process_SW_editText);
        mControlWordRes = (EditText) view.findViewById(R.id.process_CW_editText);
        mCycleReadIndication = (TextView) view.findViewById(R.id.cycle_read_indication_textview);

        Button mReadDataOnce = (Button) view.findViewById(R.id.read_once_button);
        Button mReadDataCycle = (Button) view.findViewById(R.id.cycle_read_button);
        Button mWriteDataToCW = (Button) view.findViewById(R.id.write_data_to_process_button);
        getReferences();
        updateIndication();

        mControlWordRes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    offCycleRead();
                } else {
                    writeDataToModbus(mControlWordRes.getText().toString());
                    onCycleRead();
                }
                updateIndication();
            }
        });

        mReadDataOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readDataFromModbus();
            }
        });

        handler = new UpdateModbusWords();
        final Thread updaterThread = new Thread(new CycleReadModbus());
        updaterThread.start();

        mReadDataCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cycleRead){
                    cycleRead = false;
                } else {
                    cycleRead = true;
                }
                updateIndication();
            }
        });

        mWriteDataToCW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDataToModbus(mControlWordRes.getText().toString());
                mControlWordRes.clearFocus();
            }
        });
    }

    public void offCycleRead(){
        cycleRead = false;
    }

    public void onCycleRead(){
        cycleRead = true;
    }

    public void updateIndication(){
        if (cycleRead){
            mCycleReadIndication.setText(getContext().getResources().getString(R.string.read_cycle_indication_text_ON));
        } else {
            mCycleReadIndication.setText(getContext().getResources().getString(R.string.read_cycle_indication_text_OFF));
        }
    }

    public void readDataFromModbus(){
        String[] paramsSW = {"","","",""};
        String[] paramsCW = {"","","",""};
        paramsSW[0] = IP;
        paramsSW[1] = port;
        paramsSW[2] = statusWord;
        paramsSW[3] = "1";

        paramsCW[0] = IP;
        paramsCW[1] = port;
        paramsCW[2] = controlWord;
        paramsCW[3] = "1";

        ReadFromModbus mReadSW = new ReadFromModbus();
        ReadFromModbus mReadCW = new ReadFromModbus();
        try {
            mStatusWordRes.setText(mReadSW.execute(paramsSW).get());
            mControlWordRes.setText(mReadCW.execute(paramsCW).get());
        } catch (ExecutionException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException ie){
            System.out.println(ie.getMessage());
        }
    }

    public void writeDataToModbus(String value){
        String[] paramsCW = {"","","",""};

        paramsCW[0] = IP;
        paramsCW[1] = port;
        paramsCW[2] = controlWord;
        paramsCW[3] = value;

        WriteToModbus mWriteCW = new WriteToModbus();
        mWriteCW.execute(paramsCW);

    }

    public void getReferences(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        IP = sharedPreferences.getString(SAVED_IP, getContext().getResources().getString(R.string.ip_input_default_text));
        port = sharedPreferences.getString(PORT, getContext().getResources().getString(R.string.port_input_default_text));
        statusWord = sharedPreferences.getString(STATUS_WORD, getContext().getResources().getString(R.string.device_SW_reg_val_default_text));
        controlWord = sharedPreferences.getString(CONTROL_WORD, getContext().getResources().getString(R.string.device_CW_reg_val_default_text));

    }

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

}
