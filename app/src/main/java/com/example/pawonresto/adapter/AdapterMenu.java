package com.example.pawonresto.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawonresto.R;
import com.example.pawonresto.model.Menu;
import com.example.pawonresto.ui.menu.AddEditMenu;
import com.example.pawonresto.ui.menu.ViewMenu;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.MenuViewHolder> implements Filterable {

    private final Context context;
    private List<Menu> menuList, filteredMenuList;

    public AdapterMenu( List<Menu> menuList, Context context) {
        this.menuList = menuList;
        this.context = context;
        filteredMenuList= new ArrayList<>(menuList);
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_menu, parent, false);

        return new MenuViewHolder(view);
    }

    //Konversi base64 ke bitmap
    private Bitmap base64ToBitmap(String image)
    {
        byte[] decodedString = Base64.decode(image,Base64.DEFAULT);
        Bitmap decoded = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
        return decoded;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu= filteredMenuList.get(position);

        holder.tvNamaMakanan.setText(menu.getNama_makanan());
        DecimalFormat rupiahFormat = (DecimalFormat) DecimalFormat
                .getCurrencyInstance(new Locale("in", "ID"));
        holder.tvHargaMakanan.setText(rupiahFormat.format(menu.getHarga_makanan()));

        if(menu.getImgURL()!= null)
            holder.ivMenu.setImageBitmap(base64ToBitmap(menu.getImgURL()));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder =
                        new MaterialAlertDialogBuilder(context);
                materialAlertDialogBuilder.setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menghapus data menu ini?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (context instanceof ViewMenu)
                                    ((ViewMenu) context).deleteMenu(menu.getId());
                            }
                        })
                        .show();
            }
        });

        holder.cvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddEditMenu.class);
                i.putExtra("id", menu.getId());

                if (context instanceof ViewMenu)
                    ((ViewMenu) context).startActivityForResult(i,
                            ViewMenu.LAUNCH_ADD_ACTIVITY);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredMenuList.size();
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
        filteredMenuList = new ArrayList<>(menuList);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charSequenceString = charSequence.toString();
                List<Menu> filtered = new ArrayList<>();

                if (charSequenceString.isEmpty()) {
                    filtered.addAll(menuList);
                } else {
                    for (Menu menu : menuList) {
                        if (menu.getNama_makanan().toLowerCase()
                                .contains(charSequenceString.toLowerCase()))
                            filtered.add(menu);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredMenuList.clear();
                filteredMenuList.addAll((List<Menu>) filterResults.values);
                notifyDataSetChanged();
            }
        };

    }


    public class MenuViewHolder extends RecyclerView.ViewHolder{
        TextView tvNamaMakanan, tvHargaMakanan;
        ImageView ivMenu;
        ImageButton btnDelete;
        CardView cvMenu;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHargaMakanan = itemView.findViewById(R.id.tvHargaMakanan);
            tvNamaMakanan = itemView.findViewById(R.id.tvNamaMakanan);
            ivMenu = itemView.findViewById(R.id.ivMenu);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            cvMenu = itemView.findViewById(R.id.cvMenu);
        }
    }
}