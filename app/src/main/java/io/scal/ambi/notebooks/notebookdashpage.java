package io.scal.ambi.notebooks;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.ambi.work.R;

public class notebookdashpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebookdashpage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
//
        setSupportActionBar(toolbar);
       mTitle.setText("Notebooks");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_send_icon);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }



}
