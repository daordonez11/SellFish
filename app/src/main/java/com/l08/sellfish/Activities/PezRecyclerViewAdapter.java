package com.l08.sellfish.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.l08.sellfish.Fragments.PoblacionFragment.OnListFragmentInteractionListener;
import com.l08.sellfish.Models.Pez;
import com.l08.sellfish.Models.Poblacion;
import com.l08.sellfish.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Poblacion} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PezRecyclerViewAdapter extends RecyclerView.Adapter<PezRecyclerViewAdapter.ViewHolder> {

    private final List<Pez> mValues;

    public PezRecyclerViewAdapter(List<Pez> items) {
        mValues = items;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mini_pez, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLength.setText("Longitud: "+mValues.get(position).longitud+" cm");
        holder.mNumber.setText("Pez "+(position+1));
        holder.mSemana.setText("Semana: "+mValues.get(position).semana);
      //  holder.mPeso.setText("Peso: "+mValues.get(position).peso+" g");
        if(mValues.get(position).imagen!=null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(mValues.get(position).imagen, 0, mValues.get(position).imagen.length);
        holder.fishImage.setImageBitmap(bmp);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView fishImage;
        public final TextView mLength, mNumber, mSemana, mPeso;
        public Pez mItem;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            fishImage = (ImageView) view.findViewById(R.id.fishImage);
            mLength = (TextView) view.findViewById(R.id.fishLength);
            mSemana = (TextView) view.findViewById(R.id.fishWeek);
            mPeso = (TextView) view.findViewById(R.id.fishWeight) ;
            mNumber=(TextView)view.findViewById(R.id.fishNumber);
            }

        @Override
        public String toString() {
            return super.toString() + " '" + mLength.getText() + "'";
        }
    }
}
