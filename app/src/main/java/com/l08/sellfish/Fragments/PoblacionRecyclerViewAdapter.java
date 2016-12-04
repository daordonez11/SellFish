package com.l08.sellfish.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.l08.sellfish.Fragments.PoblacionFragment.OnListFragmentInteractionListener;
import com.l08.sellfish.Models.Poblacion;
import com.l08.sellfish.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Poblacion} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PoblacionRecyclerViewAdapter extends RecyclerView.Adapter<PoblacionRecyclerViewAdapter.ViewHolder> {

    private final List<Poblacion> mValues;
    private final OnListFragmentInteractionListener mListener;

    public PoblacionRecyclerViewAdapter(List<Poblacion> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_poblacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("ID: "+mValues.get(position).id+"");
        holder.mContentView.setText(mValues.get(position).especie);
        holder.mTamano.setText("Tamaño: "+mValues.get(position).tamaño+"");
        holder.mEstanque.setText("Estanque: "+mValues.get(position).estanque);
        holder.mPeriodicidad.setText("Periodicidad: "+mValues.get(position).periodicidad);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, "DELETE");
                }
            }
        });
        holder.btnAddMuestra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, "MUESTRA");
                }
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, "CLICK");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView, mTamano, mEstanque, mPeriodicidad;
        public Poblacion mItem;
        public Button btnEdit, btnDelete, btnAddMuestra;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            btnDelete=(Button)view.findViewById(R.id.btnEliminar);
            btnAddMuestra=(Button)view.findViewById(R.id.btnAgregarMuestreo);
            mTamano=(TextView)view.findViewById(R.id.etTamano);
            mEstanque=(TextView)view.findViewById(R.id.estanque);
            mPeriodicidad=(TextView)view.findViewById(R.id.periodicidad);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
