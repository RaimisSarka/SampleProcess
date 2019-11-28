package com.example.sampleprocess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.smapleprocess.R;

public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener, ProcessFragment.OnFragmentInteractionListener{

    public TextView mProcessButtonTextView;
    public TextView mSttingsButtonTextView;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProcessButtonTextView = (TextView) findViewById(R.id.process_menu_button_label_textview);
        mSttingsButtonTextView = (TextView) findViewById(R.id.settings_menu_button_textview);

        //MainFragment mMainFragment = new MainFragment();



        mProcessButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProcessButtonTextView.setText(R.string.process_u);
                mSttingsButtonTextView.setText(R.string.settings);
                ProcessFragment mProcessFragment = new ProcessFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment, mProcessFragment);
                ft.commit();
            }
        });

        mSttingsButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProcessButtonTextView.setText(R.string.process);
                mSttingsButtonTextView.setText(R.string.settings_u);
                MainFragment mMainFragment = new MainFragment();
                FragmentTransaction  ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment, mMainFragment);
                ft.commit();
            }
        });
        //getSupportFragmentManager().beginTransaction().add(R.id.process_layout, mMainFragment).commit();
    }

}
