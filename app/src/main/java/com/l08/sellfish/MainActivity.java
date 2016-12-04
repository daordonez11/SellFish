package com.l08.sellfish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.l08.sellfish.Activities.GesturesActivity;
import com.l08.sellfish.Activities.LoginActivity;
import com.l08.sellfish.Activities.MuestrasActivity;
import com.l08.sellfish.Activities.RecordActivity;
import com.l08.sellfish.Fragments.AddMuestraDialogFragment;
import com.l08.sellfish.Fragments.AddPopulationFragment;
import com.l08.sellfish.Fragments.IndicadoresFragment;
import com.l08.sellfish.Fragments.PoblacionFragment;
import com.l08.sellfish.Models.Poblacion;
import com.l08.sellfish.Persistance.PoblacionDatabaseHelper;

import java.util.List;

import static com.l08.sellfish.R.id.fab;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddPopulationFragment.OnFragmentInteractionListener , PoblacionFragment.OnListFragmentInteractionListener, IndicadoresFragment.OnFragmentInteractionListener, View.OnClickListener {
    private static final String TAG = "MainTAG";
    PoblacionDatabaseHelper pbh;
    Button btnSignOut;
    com.rey.material.widget.FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.main_fragment_container, AddPopulationFragment.newInstance(""), "Add")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.add_name);
        pbh = PoblacionDatabaseHelper.getInstance(getApplicationContext());
        btnSignOut = (Button) findViewById(R.id.nav_signout);
        btnSignOut.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent mIntent = new Intent(MainActivity.this,LoginActivity.class);
                    finishAffinity();
                    startActivity(mIntent);
                }
                // ...
            }
        };

//        pbh.deleteAll();
//        Poblacion dummy = new Poblacion();
//        dummy.tamaño=3;
//        dummy.periodicidad="3 semanas";
//        dummy.estanque="Estanque 1";
//        dummy.especie="otrico";
//        pbh.addPoblacion(dummy);
        // Write a message to the database
        fab = (com.rey.material.widget.FloatingActionButton) findViewById(R.id.fabGestures);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GesturesActivity.class);
                startActivityForResult(i,1231);
            }
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
        //DatabaseReference myRef = database.getReference(user.getUid()+"/poblaciones");
        //myRef.setValue("Hello, World!");
//            List<Poblacion> poblaciones = pbh.getAllPopulations();
//            for (Poblacion poblacion : poblaciones) {
                // do something
//                myRef.setValue(poblacion);
                //System.out.println(poblacion.id+"-"+poblacion.especie);
//            }

        }else{
            Intent mIntent = new Intent(MainActivity.this,LoginActivity.class);
            finishAffinity();
            startActivity(mIntent);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Add) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.main_fragment_container, AddPopulationFragment.newInstance(""), "Add")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.add_name);
        } else if (id == R.id.nav_list) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.main_fragment_container, PoblacionFragment.newInstance(1), "List")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.list_name);
        }  else if (id == R.id.nav_manage) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.main_fragment_container, IndicadoresFragment.newInstance("",""), "KPI")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.kpi_name);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        if(uri.toString().equals("Agregar Poblacion"))
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.main_fragment_container, PoblacionFragment.newInstance(1), "List")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.list_name);
        }
    }

    @Override
    public void onListFragmentInteraction(final Poblacion item, String action) {
        if(action.equals("DELETE"))
        {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.accion))
                    .setMessage(String.format("Seguro desea eliminar la población?"))
                    .setPositiveButton(getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pbh.deletePoblacion(item.id);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace( R.id.main_fragment_container, PoblacionFragment.newInstance(1), "List")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                            final ActionBar actionBar = getSupportActionBar();
                            actionBar.setTitle(R.string.list_name);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create().show();

        }
        if(action.equals("CLICK")) {
//            Dialog d = new Dialog(this);
//            d.setTitle("Poblacion: " + item.id);
//            TextView nuevita = new TextView(this);
//            nuevita.setText(item.especie + "-" + item.estanque);
//            d.setContentView(nuevita);
//            d.show();
            Intent i = new Intent(this, MuestrasActivity.class);
            i.putExtra("poblacionId",item.id);
            startActivity(i);
        }
        if(action.equals("GRAB")) {
//            Dialog d = new Dialog(this);
//            d.setTitle("Poblacion: " + item.id);
//            TextView nuevita = new TextView(this);
//            nuevita.setText(item.especie + "-" + item.estanque);
//            d.setContentView(nuevita);
//            d.show();
            Intent i = new Intent(this, RecordActivity.class);
            i.putExtra("poblacionId",item.id);
            startActivity(i);
        }

        if(action.equals("MUESTRA")) {
            AddMuestraDialogFragment d = AddMuestraDialogFragment.newInstance(item.id,item.tamaño, 1000);
            d.show(getFragmentManager(),"Dialogo");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ALGUITO","req: "+requestCode+" res:"+resultCode);
        if(requestCode==1231)
        {
            if(resultCode==123)
            {
                moveToAgregar();
            }else if(resultCode==456){
                moveToPoblaciones();
            }
            else if(resultCode==789){
                moveToIndic();
            }
        }
    }

    private void moveToIndic() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.main_fragment_container, IndicadoresFragment.newInstance("",""), "KPI")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.kpi_name);
    }

    private void moveToPoblaciones() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.main_fragment_container, PoblacionFragment.newInstance(1), "List")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.list_name);
    }

    private void moveToAgregar() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.main_fragment_container, AddPopulationFragment.newInstance(""), "Add")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.add_name);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.nav_signout)
        {
            mAuth.signOut();
        }
    }
}
