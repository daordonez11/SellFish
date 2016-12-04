package com.l08.sellfish.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.l08.sellfish.Fragments.PoblacionFragment.OnListFragmentInteractionListener;
import com.l08.sellfish.Models.Pez;
import com.l08.sellfish.Models.PezCloud;
import com.l08.sellfish.Models.Poblacion;
import com.l08.sellfish.R;

import java.util.List;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Poblacion} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PezRecyclerViewAdapter extends RecyclerView.Adapter<PezRecyclerViewAdapter.ViewHolder> {

    private final List<PezCloud> mValues;
    private Context context;
    public PezRecyclerViewAdapter(List<PezCloud> items, Context context) {
        mValues = items;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mini_pez, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mLength.setText("Longitud: "+mValues.get(position).longitud+" cm");
        holder.mNumber.setText("Pez "+(position+1));
        holder.mSemana.setText("Semana: "+mValues.get(position).semana);
      //  holder.mPeso.setText("Peso: "+mValues.get(position).peso+" g");
        if(mValues.get(position).urlImagen!=null) {

            Ion.with(holder.fishImage)
                    .load(mValues.get(position).urlImagen);
        }
        if(mValues.get(position).grabacion!=null)
        {
            holder.btnGrabar.setText("Escuchar Grabacion");
            holder.btnGrabar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                MediaPlayer player = new MediaPlayer();
                                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                player.setDataSource(mValues.get(position).grabacion);
                                player.prepare();
                                player.start();

                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            return null;
                        }
                    }.execute();

                }
            });
        }else{
        holder.btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MuestrasActivity)context).setActualFishId(mValues.get(position).id);
                String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
                int color = context.getResources().getColor(R.color.colorPrimaryDark);
                int requestCode = 0;
                AndroidAudioRecorder.with((MuestrasActivity)context)
                        // Required
                        .setFilePath(filePath)
                        .setColor(color)
                        .setRequestCode(requestCode)

                        // Optional
                        .setSource(AudioSource.MIC)
                        .setChannel(AudioChannel.STEREO)
                        .setSampleRate(AudioSampleRate.HZ_48000)
                        .setAutoStart(true)
                        .setKeepDisplayOn(true)

                        // Start recording
                        .record();
            }
        });}
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView fishImage;
        public final Button btnGrabar;
        public final TextView mLength, mNumber, mSemana, mPeso;
        public PezCloud mItem;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            fishImage = (ImageView) view.findViewById(R.id.fishImage);
            mLength = (TextView) view.findViewById(R.id.fishLength);
            mSemana = (TextView) view.findViewById(R.id.fishWeek);
            mPeso = (TextView) view.findViewById(R.id.fishWeight) ;
            mNumber=(TextView)view.findViewById(R.id.fishNumber);
            btnGrabar=(Button)view.findViewById(R.id.btnGrabaciones);
            }

        @Override
        public String toString() {
            return super.toString() + " '" + mLength.getText() + "'";
        }
    }
}
