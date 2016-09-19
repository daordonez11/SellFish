package com.l08.sellfish.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.l08.sellfish.Models.Poblacion;
import com.l08.sellfish.Persistance.PoblacionDatabaseHelper;
import com.l08.sellfish.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPopulationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPopulationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPopulationFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btnAgregar;
    private Spinner spinnerEspecie;
    private EditText etTamano, etEstanque, etPeriodicidad;
    private OnFragmentInteractionListener mListener;
    PoblacionDatabaseHelper pbh;
    public AddPopulationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AddPopulationFragment.
     */
    public static AddPopulationFragment newInstance(String param1) {
        AddPopulationFragment fragment = new AddPopulationFragment();
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

        return inflater.inflate(R.layout.fragment_add_population, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAgregar = (Button)view.findViewById(R.id.btnAgregarPoblacion);
        btnAgregar.setOnClickListener(this);
        etTamano = (EditText) view.findViewById(R.id.editTextTamano);
        etEstanque = (EditText) view.findViewById(R.id.editTextEstanque);
        etPeriodicidad = (EditText)view.findViewById(R.id.editTextPeriodicidad);
        spinnerEspecie  =(Spinner)view.findViewById(R.id.spinnerEspecie);
        pbh = PoblacionDatabaseHelper.getInstance(getActivity().getApplicationContext());
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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnAgregarPoblacion)
        {
            agregarPoblacion();
        }
    }

    /**
     * Método encargado de cargar la poblaciona la persistencia
     */
    private void agregarPoblacion() {
        Poblacion pNueva = new Poblacion();
        if(!etTamano.getText().toString().equals("")&&!etEstanque.getText().toString().equals("")&&!etPeriodicidad.getText().toString().equals("")) {
            pNueva.especie = (String) spinnerEspecie.getSelectedItem();
            pNueva.tamaño = Integer.parseInt(etTamano.getText().toString());
            pNueva.estanque = etEstanque.getText().toString();
            pNueva.periodicidad = etPeriodicidad.getText().toString();
            long nuevoId = pbh.addPoblacion(pNueva);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getResources().getString(R.string.resultado))
                    .setMessage(String.format("Poblacion creada correctamente:\nId: %s\nEspecie: %s\nEstanque: %s", nuevoId + "", pNueva.especie, pNueva.estanque))
                    .setNegativeButton(getActivity().getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onButtonPressed(Uri.parse("Agregar Poblacion"));
                        }
                    })
                    .create().show();
        }
        else
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getResources().getString(R.string.resultado))
                    .setMessage(String.format("Error en los datos"))
                    .setNegativeButton(getActivity().getResources().getString(R.string.dismiss), null)
                    .create().show();
        }



    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
