package com.l08.sellfish.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.l08.sellfish.MeasureFish.DrawView;
import com.l08.sellfish.MeasureFish.ImageSurface;
import com.l08.sellfish.Models.Pez;
import com.l08.sellfish.Models.PezCloud;
import com.l08.sellfish.Persistance.PoblacionDatabaseHelper;
import com.l08.sellfish.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by danielordonez on 9/18/16.
 */
public class AddMuestraDialogFragment extends DialogFragment implements SensorEventListener {
    private static final int REQUEST_TAKE_PHOTO = 111;
    private LinearLayout listaPeces;
    private EditText etSemana;
    private String poblacionId;
    private Pez pecesito;
    private int tamano;
    private Button guardar, cancelar, analizar, limpiar;
    private ImageView fishIV;
    private double result;
    private PoblacionDatabaseHelper pdh;
    private FrameLayout preview;
    private FrameLayout preview2;
    private DrawView drawView;
    private File photoFile;
    private boolean puedeGuardar;
    private EditText etLongitud;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private float actualState;
    private boolean preguntando;
    public AddMuestraDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preguntando=false;
        poblacionId = getArguments().getString("id");
        tamano = getArguments().getInt("tamano");
        actualState = getArguments().getFloat("ilum");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    puedeGuardar =false;
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mLightSensor= mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLightSensor == null){
            Toast.makeText(getActivity(),
                    "No Light Sensor! quit-",
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_muestra, container);
        pdh = PoblacionDatabaseHelper.getInstance(getActivity().getApplicationContext());
        preview = (FrameLayout)view.findViewById(R.id.camera_preview);
        preview2 = (FrameLayout)view.findViewById(R.id.camera_preview2);

        etSemana = (EditText)view.findViewById(R.id.editTextSemana);
        guardar = (Button)view.findViewById(R.id.btnAgregarMuestra);
        analizar =(Button) view.findViewById(R.id.btnAnalizar);
        fishIV = (ImageView)view.findViewById(R.id.fishIV);
        limpiar=(Button)view.findViewById(R.id.btnLimpiar);
        analizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyze();
            }
        });
        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawView!=null){
                    drawView.clearCanvas();
                }else{
                    Toast t = Toast.makeText(getActivity(),"No has seleccionado imagen, no hay elementos para limpiar",Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(puedeGuardar)
                {

                if(!etSemana.getText().toString().equals("")) {
                    pecesito.semana= Integer.parseInt(etSemana.getText().toString());
                    pecesito.longitud = Double.parseDouble(etLongitud.getText().toString());

                    progressDialog = ProgressDialog.show(getActivity(), "Por favor espere...",
                            "Cargando información al servidor", true);
                    DatabaseReference fishRef = database.getReference(user.getUid()+"/poblaciones/"+poblacionId+"/peces/");

                    final String key = fishRef.push().getKey();
                    StorageReference fishStorRef = mStorageRef.child("images/"+key+".jpg");
                    final String poblacionIdFinal = poblacionId;
                    fishStorRef.putBytes(pecesito.imagen)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    PezCloud finalFish = new PezCloud();
                                    finalFish.id=key;
                                    finalFish.longitud=pecesito.longitud;
                                    finalFish.urlImagen=downloadUrl.toString();
                                    finalFish.semana=pecesito.semana;
                                    finalFish.peso=pecesito.peso;
                                    DatabaseReference myRef = database.getReference(user.getUid()+"/poblaciones/"+poblacionIdFinal+"/peces/"+key);
                                    myRef.setValue(finalFish);
                                    Toast.makeText(getActivity(), "Fish saved correctly", Toast.LENGTH_SHORT)
                                            .show();
                                    progressDialog.dismiss();
                                    changeToLight();
                                    dismiss();
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

                }
                else{
                    Toast t = Toast.makeText(getActivity(),"Debe incluir la semana a la que perteneces",Toast.LENGTH_SHORT);
                    t.show();
                }
                }else{
                    Toast t = Toast.makeText(getActivity(),"Espera, arreglando formato de la imagen",Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        cancelar = (Button)view.findViewById(R.id.btnCancelarDialogo);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeToLight();dismiss();
            }
        });

        getDialog().setTitle("Agregar Muestra");
        getDialog().setCanceledOnTouchOutside(false);
        int totalFishes = (int)(tamano*0.05);

            pecesito = new Pez();

            etLongitud=(EditText)view.findViewById(R.id.fishLength);
            ((Button)view.findViewById(R.id.btnSetPhoto)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePhotoForFish();
                }
            });
        return view;
    }

    private void analyze() {
        result = drawView.calculate(2.45, 1, 1);
        showResult();
    }

    private void showResult() {
        Toast.makeText(getActivity(), "Esta es la longitud encontrada: "+result, Toast.LENGTH_SHORT)
                .show();
        etLongitud.setText(result+"");

    }

    private void takePhotoForFish() {
        // Check if there is a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getActivity(), "Error creating image", Toast.LENGTH_SHORT);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    private void saveFishes() {
        System.out.println(pecesito+"-"+poblacionId);
        pdh.addPez(pecesito,poblacionId);
        dismiss();
    }

    public static AddMuestraDialogFragment newInstance(String id, int tamano, float ilum) {
        AddMuestraDialogFragment f = new AddMuestraDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putInt("tamano",tamano);
        args.putFloat("ilum", ilum);
        f.setArguments(args);

        return f;
    }
    /**
     * The activity returns with the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            preview.removeAllViews();
            drawView = new DrawView(getActivity());
            drawView.setZOrderOnTop(true);
            preview2.addView(drawView);
            ImageSurface image = new ImageSurface(getActivity(), photoFile);
            //image.setZOrderOnTop(true);
            preview.addView(image);
            ViewCompat.setTranslationZ(preview, 0);
            ViewCompat.setTranslationZ(preview2, 5);
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile.getPath());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            pecesito.imagen=byteArray;
                            //preview.addView(drawView);
                            puedeGuardar=true;
                            System.out.println("Save now :)");
                        }
                    }
            ).start();

//            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            fishIV.setImageBitmap(bmp);

        } else {
            Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name

        String imageFileName = "PezParaCloud";
        File storageDir = getActivity().getExternalFilesDir(null);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float ilu = event.values[0];
        Log.d("Sensor luz","Dato: "+ilu);
        if(!preguntando){
            preguntando=true;
        if(actualState>100&&ilu<=100)
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getResources().getString(R.string.accion))
                    .setMessage("Detectamos cambio en la luz, desea cambiar a modo nocturno?")
                    .setNegativeButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            actualState = ilu;
                            preguntando=false;
                           changeToDark();
                            AddMuestraDialogFragment.this.dismiss();
                            AddMuestraDialogFragment d = AddMuestraDialogFragment.newInstance(poblacionId,tamano,ilu);
                            d.show(getFragmentManager(),"Dialogo");
                        }
                    })
                    .create().show();
        }
        else if(actualState<=100&&ilu>100)
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getResources().getString(R.string.accion))
                    .setMessage("Detectamos cambio en la luz, desea cambiar a modo día?")
                    .setPositiveButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            actualState = ilu;
                            preguntando=false;
                            changeToLight();
                            AddMuestraDialogFragment.this.dismiss();
                            AddMuestraDialogFragment d = AddMuestraDialogFragment.newInstance(poblacionId,tamano,ilu);
                            d.show(getFragmentManager(),"Dialogo");
                        }
                    })
                    .create().show();
        }
            else{
            preguntando=false;
        }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void changeToLight(){
        getActivity().setTheme(R.style.AppTheme);
    }
    public void changeToDark(){

        getActivity().setTheme(R.style.AppTheme_Dark);

    }
}
