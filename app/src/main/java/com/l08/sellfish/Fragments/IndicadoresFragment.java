package com.l08.sellfish.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.l08.sellfish.Models.Pez;
import com.l08.sellfish.Models.Poblacion;
import com.l08.sellfish.Persistance.PoblacionDatabaseHelper;
import com.l08.sellfish.R;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IndicadoresFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IndicadoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndicadoresFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String total;
    private TextView tvEstimate;
    private int contador ;
    private int totalPob;
    private PoblacionDatabaseHelper pdh;
    private OnFragmentInteractionListener mListener;
    private LineChart lcEstimado;
    private Button sendSms;
private LineData lineData;
    public IndicadoresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IndicadoresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndicadoresFragment newInstance(String param1, String param2) {
        IndicadoresFragment fragment = new IndicadoresFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pdh = PoblacionDatabaseHelper.getInstance(getActivity().getApplicationContext());
        lcEstimado= (LineChart)view.findViewById(R.id.chartEstimado);
        lcEstimado.getAxisLeft();
        lcEstimado.setDescription("Estimación y cálculo de crecimiento por población");
        tvEstimate=(TextView)view.findViewById(R.id.tvEstimate);
        sendSms=(Button)view.findViewById(R.id.btnSendSMS);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarInfo();
            }
        });
        total="Para la realización de la estimación y los cálculos de crecimiento se utilizan: Ecuación de crecimiento por Von Bertalanffy, Estimación de parámetros de crecimiento Ford-Waldford\n";
        loadEstimado();
    }

    private void enviarInfo() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" +""));
        intent.putExtra("sms_body", total);
        startActivity(intent);
    }

    private void loadEstimado() {
         lineData = new LineData();
        contador=0;
        List<Poblacion> poblaciones = pdh.getAllPopulations();
        totalPob=poblaciones.size();
        for (int i=0;i<poblaciones.size();i++)
        {
            tasaCrecimiento(poblaciones.get(i).id);
        }
        //Cargo un listado de entradas
//        List<Entry> entries = new ArrayList<Entry>();
//        entries.add(new Entry(0, 0));
//        entries.add(new Entry(1, 3));
//        entries.add(new Entry(2, 6));
//        entries.add(new Entry(3, 4));

        //Agrego las entrada s aund ata set
//        LineDataSet dataSet = new LineDataSet(entries, "Población 1"); // add entries to dataset
        //dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        //Agrego el dataSet a los datos de la gráficas

//        lineData.addDataSet(dataSet);
//        lcEstimado.setData(lineData);
//        lcEstimado.invalidate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_indicadores, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
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






//Ecuación de crecimiento por Von Bertalanffy
//Estimación de parámetros de crecimiento Ford-Waldford

    public void tasaCrecimiento(long poblacionId) {

        // creating regression object, passing true to have intercept term
        SimpleRegression simpleRegression = new SimpleRegression(true);

        // passing data to the model
        // model will be fitted automatically by the class
        List<Pez> tam = pdh.getFishesFromPopulation(poblacionId);
//        //Cargo un listado de entradas
      List<Entry> entries = new ArrayList<Entry>();
        for (int i=0;i< tam.size()-1;i++)
        {
            entries.add(new Entry((float)tam.get(i).semana,(float)tam.get(i).longitud));
            simpleRegression.addData(tam.get(i).longitud,tam.get(i+1).longitud);
        }
        if(tam.size()>0) {
            entries.add(new Entry((float) tam.get(tam.size() - 1).semana, (float) tam.get(tam.size() - 1).longitud));
        }
        //Cargo en un data set
        LineDataSet dataSet = new LineDataSet(entries, "Población "+poblacionId);
        lineData.addDataSet(dataSet);
        double b = simpleRegression.getSlope();
        double a = simpleRegression.getIntercept();
System.out.println("Mira el A: "+a+", Mira el B: "+b);
        double k=-1*Math.log(b)/52;
        double lInf=a/(1-b);
            double t0 = 1 + (1 / k) + Math.log((lInf - tam.get(0).longitud) / lInf);
            System.out.println("Población " + poblacionId + ", K = " + k + ", L(Inf) = " + lInf + ", T0 = " + t0);
            total += "Población " + poblacionId + ", K = " + k + ", L(Inf) = " + lInf + "\n";


        double promedio = 0;
        for (int i=0;i< tam.size();i++)
        {
            if(tam.get(i).peso==0)
            {
                tam.get(i).peso=lInf*Math.pow(1-Math.exp(-1*k*(tam.get(i).semana-t0)),3);
                pdh.updatePesoPez(tam.get(i));
                System.out.println("MIRA EL PESOOOO:"+(lInf*Math.pow(1-Math.exp(-1*k*(tam.get(i).semana-t0)),3)));
            }
        }


         List <Pez> predict = new ArrayList<>();
        for(int i=1;i <9;i++)
        {
            Pez nuevo = new Pez();
            nuevo.longitud=lInf*(1-Math.exp(-1*k*(i-t0)));
            nuevo.semana=i;
            predict.add(nuevo);

        }
        contador++;
                if(contador==totalPob)
                {
                    lcEstimado.setData(lineData);
                    lcEstimado.invalidate();
                    tvEstimate.setText(total);
                }

    }
}
