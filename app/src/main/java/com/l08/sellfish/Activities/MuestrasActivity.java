package com.l08.sellfish.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.l08.sellfish.Fragments.PoblacionRecyclerViewAdapter;
import com.l08.sellfish.Persistance.PoblacionDatabaseHelper;
import com.l08.sellfish.R;

public class MuestrasActivity extends AppCompatActivity {

    private PoblacionDatabaseHelper pbh;
    private long poblacionId;
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestras);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        poblacionId = extras.getLong("poblacionId");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pbh = PoblacionDatabaseHelper.getInstance(getApplicationContext());
        // Set the adapter
        rv = (RecyclerView)findViewById(R.id.listMuestras);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

       System.out.println("Tamaño: "+pbh.getFishesFromPopulation(poblacionId).size());
        rv.setAdapter(new PezRecyclerViewAdapter(pbh.getFishesFromPopulation(poblacionId)));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
