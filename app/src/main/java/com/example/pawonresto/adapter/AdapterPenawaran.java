package com.example.pawonresto.adapter;

import static com.android.volley.Request.Method.DELETE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pawonresto.Api.ApiPenawaran;
import com.example.pawonresto.R;
import com.example.pawonresto.model.Menu;
import com.example.pawonresto.model.Penawaran;
import com.example.pawonresto.ui.menu.AddEditMenu;
import com.example.pawonresto.ui.menu.ViewMenu;
import com.example.pawonresto.ui.penawaran.AddEditPenawaran;
import com.example.pawonresto.ui.penawaran.ViewPenawaran;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterPenawaran extends RecyclerView.Adapter<AdapterPenawaran.PenawaranViewHolder> implements Filterable {

    private final Context context;
    private List<Penawaran> penawaranList, filteredPenawaranList;

    public AdapterPenawaran( List<Penawaran> penawaranList, Context context) {
        this.penawaranList = penawaranList;
        this.context = context;
        filteredPenawaranList= new ArrayList<>(penawaranList);
    }

    @NonNull
    @Override
    public AdapterPenawaran.PenawaranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_penawaran, parent, false);

        return new AdapterPenawaran.PenawaranViewHolder(view);
    }

    //konversi base64 ke bitmap
    private Bitmap base64ToBitmap(String image)
    {
        byte[] decodedString = Base64.decode(image,Base64.DEFAULT);
        Bitmap decoded = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
        return decoded;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPenawaran.PenawaranViewHolder holder, int position) {
        Penawaran penawaran= filteredPenawaranList.get(position);

        holder.tvJudul.setText(penawaran.getJudul());
        holder.tvDeskripsi.setText(penawaran.getDeskripsi());

        if(penawaran.getImgURL()!= null)
            holder.ivPenawaran.setImageBitmap(base64ToBitmap(penawaran.getImgURL()));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder =
                        new MaterialAlertDialogBuilder(context);
                materialAlertDialogBuilder.setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menghapus data penawaran ini?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (context instanceof ViewPenawaran)
                                    ((ViewPenawaran) context).deletePenawaran(penawaran.getId());
                            }
                        })
                        .show();
            }
        });

        holder.cvPenawaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddEditPenawaran.class);
                i.putExtra("id", penawaran.getId());

                if (context instanceof ViewPenawaran)
                    ((ViewPenawaran) context).startActivityForResult(i,
                            ViewPenawaran.LAUNCH_ADD_ACTIVITY);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredPenawaranList.size();
    }

    public void setPenawaranList(List<Penawaran> penawaranList) {
        this.penawaranList = penawaranList;
        filteredPenawaranList = new ArrayList<>(penawaranList);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charSequenceString = charSequence.toString();
                List<Penawaran> filtered = new ArrayList<>();

                if (charSequenceString.isEmpty()) {
                    filtered.addAll(penawaranList);
                } else {
                    for (Penawaran penawaran : penawaranList) {
                        if (penawaran.getJudul().toLowerCase()
                                .contains(charSequenceString.toLowerCase()))
                            filtered.add(penawaran);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredPenawaranList.clear();
                filteredPenawaranList.addAll((List<Penawaran>) filterResults.values);
                notifyDataSetChanged();
            }
        };

    }

    public class PenawaranViewHolder extends RecyclerView.ViewHolder{
        TextView tvJudul, tvDeskripsi;
        ImageView ivPenawaran;
        ImageButton btnDelete;
        CardView cvPenawaran;

        public PenawaranViewHolder(@NonNull View itemView) {
            super(itemView);

            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            ivPenawaran = itemView.findViewById(R.id.ivPenawaran);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            cvPenawaran = itemView.findViewById(R.id.cvPenawaran);
        }
    }
}