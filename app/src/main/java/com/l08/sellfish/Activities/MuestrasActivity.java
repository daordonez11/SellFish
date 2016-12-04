package com.l08.sellfish.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.l08.sellfish.Fragments.PoblacionRecyclerViewAdapter;
import com.l08.sellfish.Models.PezCloud;
import com.l08.sellfish.Models.Poblacion;
import com.l08.sellfish.Persistance.PoblacionDatabaseHelper;
import com.l08.sellfish.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MuestrasActivity extends AppCompatActivity {

    private PoblacionDatabaseHelper pbh;
    private String poblacionId;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private FirebaseUser user;

    public String getActualFishId() {
        return actualFishId;
    }

    public void setActualFishId(String actualFishId) {
        this.actualFishId = actualFishId;
    }

    private String actualFishId;
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_muestras);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        poblacionId = extras.getString("poblacionId");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pbh = PoblacionDatabaseHelper.getInstance(getApplicationContext());
        // Set the adapter
        rv = (RecyclerView)findViewById(R.id.listMuestras);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getReference(user.getUid()+"/poblaciones/"+poblacionId+"/peces");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                List<PezCloud> list = new ArrayList<PezCloud>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    PezCloud pob = postSnapshot.getValue(PezCloud.class);
                    list.add(pob);
                }
                if(list.size()>0) {
                    rv.setAdapter(new PezRecyclerViewAdapter(list, MuestrasActivity.this));
                }else{
                    Toast t = Toast.makeText(getApplicationContext(), "La lista de de peces se encuentra vacia", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                progressDialog = ProgressDialog.show(this, "Por favor espere...",
                        "Cargando información al servidor", true);
                // Great! User has recorded and saved the audio file
                File audio = new File(  Environment.getExternalStorageDirectory() + "/recorded_audio.wav");
                StorageReference fishStorRef = mStorageRef.child("audios/"+actualFishId+".wav");
                final String poblacionIdFinal = poblacionId;
                fishStorRef.putFile(Uri.fromFile(audio))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                DatabaseReference myRef = database.getReference(user.getUid()+"/poblaciones/"+poblacionIdFinal+"/peces/"+actualFishId+"/grabacion");
                                myRef.setValue(downloadUrl.toString());
                                Toast.makeText(MuestrasActivity.this, "Grabacion guardada correctamente", Toast.LENGTH_SHORT)
                                        .show();
                                progressDialog.dismiss();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                exception.printStackTrace();
                            }
                        });
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
    }
}
