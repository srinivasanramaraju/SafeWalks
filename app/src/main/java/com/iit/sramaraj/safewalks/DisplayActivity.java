package com.iit.sramaraj.safewalks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    ListView list;

        ArrayList no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaly);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        final ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("array_list");
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        InputValues in =new InputValues();
        in.InputValues(myList);
        CustomList adapter = new
                CustomList(DisplayActivity.this,in.summary, in.distance,in.duration);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /* Todo get the placemeter count from here */
                  //ClientserverAsync cs=new ClientserverAsync();
                   //cs.execute();

                Intent i = new Intent(getApplicationContext(), DirectionMapActivity.class);
                // sending data to new activity
                Bundle extras = new Bundle();
                i.putExtra("position", position);

               /* PlacemeterClass requestPlacemeter= new PlacemeterClass();
                 Need to neccessary values for placemeter here in input values class

                try {
                    no = requestPlacemeter.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                 need to change this - temporary
                Log.d("DisplayActi",Integer.toString((Integer) no.get(1)));*/
                i.putExtra("ped_no", no);
                i.putExtra("array_list", myList);
                startActivity(i);
            }
        });

    }

}
