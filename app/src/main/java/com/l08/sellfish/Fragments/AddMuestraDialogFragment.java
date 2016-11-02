package com.l08.sellfish.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.l08.sellfish.Models.Pez;
import com.l08.sellfish.Persistance.PoblacionDatabaseHelper;
import com.l08.sellfish.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielordonez on 9/18/16.
 */
public class AddMuestraDialogFragment extends DialogFragment {
    private static final int REQUEST_TAKE_PHOTO = 111;
    private LinearLayout listaPeces;
    private EditText etSemana;
    private long poblacionId;
    private Pez pecesito;
    private int tamano;
    private Button guardar, cancelar;
    private ImageView fishIV;

    private PoblacionDatabaseHelper pdh;
    public AddMuestraDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poblacionId = getArguments().getLong("id");
        tamano = getArguments().getInt("tamano");


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_muestra, container);
        pdh = PoblacionDatabaseHelper.getInstance(getActivity().getApplicationContext());
        etSemana = (EditText)view.findViewById(R.id.editTextSemana);
        guardar = (Button)view.findViewById(R.id.btnAgregarMuestra);
        fishIV = (ImageView)view.findViewById(R.id.fishIV);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etSemana.getText().toString().equals("")) {
                    saveFishes();
                    Toast.makeText(getActivity(), "Fish saved correctly", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    Toast t = Toast.makeText(getActivity(),"Debe incluir la semana a la que perteneces",Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        cancelar = (Button)view.findViewById(R.id.btnCancelarDialogo);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle("Agregar Muestra");
        getDialog().setCanceledOnTouchOutside(false);
        int totalFishes = (int)(tamano*0.05);

            pecesito = new Pez();

            ((EditText)view.findViewById(R.id.fishLength)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!s.toString().equals("")) {
                        pecesito.longitud = Double.parseDouble(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ((Button)view.findViewById(R.id.btnSetPhoto)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePhotoForFish();
                }
            });
        return view;
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
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void saveFishes() {
        System.out.println(pecesito+"-"+poblacionId);
        pecesito.semana= Integer.parseInt(etSemana.getText().toString());
        pdh.addPez(pecesito,poblacionId);
        dismiss();
    }

    public static AddMuestraDialogFragment newInstance(long id, int tamano) {
        AddMuestraDialogFragment f = new AddMuestraDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putInt("tamano",tamano);
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
            System.out.println(data);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            pecesito.imagen=byteArray;
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            fishIV.setImageBitmap(bmp);

        } else {
            Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
