package io.scal.ambi.notebooks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ambi.work.R;

public class Newnotebook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newnotebook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
//
        setSupportActionBar(toolbar);
        mTitle.setText("new notebook");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_send_icon);
    }
}
