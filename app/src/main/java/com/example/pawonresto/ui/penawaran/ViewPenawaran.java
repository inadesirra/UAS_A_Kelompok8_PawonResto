package com.example.pawonresto.ui.penawaran;

import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.GET;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pawonresto.Api.ApiMenu;
import com.example.pawonresto.Api.ApiPenawaran;
import com.example.pawonresto.MainActivity;
import com.example.pawonresto.R;
import com.example.pawonresto.adapter.AdapterMenu;
import com.example.pawonresto.adapter.AdapterPenawaran;
import com.example.pawonresto.model.Menu;
import com.example.pawonresto.model.MenuResponse;
import com.example.pawonresto.model.Penawaran;
import com.example.pawonresto.model.PenawaranResponse;
import com.example.pawonresto.ui.home.HomeFragment;
import com.example.pawonresto.ui.maps.Maps;
import com.example.pawonresto.ui.menu.AddEditMenu;
import com.example.pawonresto.ui.menu.ViewMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPenawaran extends AppCompatActivity {
    public static final int LAUNCH_ADD_ACTIVITY = 123;

    private SwipeRefreshLayout srPenawaran;
    private AdapterPenawaran adapter;
    private SearchView svPenawaran;
    private LinearLayout layoutLoading;
    private RequestQueue queue;
    private FloatingActionButton fab_back;

    ImageView ivGambar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_penawaran);

        // Pendeklarasian request queue  -> Volley pake ini
        queue = Volley.newRequestQueue(this);

        layoutLoading = findViewById(R.id.layout_loading);
        srPenawaran = findViewById(R.id.sr_penawaran);
        svPenawaran = findViewById(R.id.sv_penawaran);

        fab_back = findViewById(R.id.fab_maps_back);
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewPenawaran.this, MainActivity.class));
                finish();
            }
        });

        srPenawaran.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPenawaran();
            }
        });

        svPenawaran.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewPenawaran.this, AddEditPenawaran.class);
                startActivityForResult(i, LAUNCH_ADD_ACTIVITY);
            }
        });

        RecyclerView rvPenawaran = findViewById(R.id.rv_penawaran);
        adapter = new AdapterPenawaran(new ArrayList<>(), this);

        int orientation = getResources().getConfiguration().orientation;

        int spanCount = orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        rvPenawaran.setLayoutManager(new GridLayoutManager(ViewPenawaran.this, spanCount));


        rvPenawaran.setAdapter(adapter);

        getAllPenawaran();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ADD_ACTIVITY && resultCode == Activity.RESULT_OK)
            getAllPenawaran();
    }


    private void getAllPenawaran() {
        srPenawaran.setRefreshing(true);

        final StringRequest stringRequest = new StringRequest(GET, ApiPenawaran.GET_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                PenawaranResponse penawaranResponse =
                        gson.fromJson(response, PenawaranResponse.class);
                Penawaran penawaran = penawaranResponse.getPenawaran().get(0);
                adapter.setPenawaranList(penawaranResponse.getPenawaran());
                adapter.getFilter().filter(svPenawaran.getQuery());

                Toast.makeText(ViewPenawaran.this,
                        penawaranResponse.getMessage(), Toast.LENGTH_SHORT).show();
                srPenawaran.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srPenawaran.setRefreshing(false);
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject errors = new JSONObject(responseBody);
                    Toast.makeText(ViewPenawaran.this,
                            errors.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ViewPenawaran.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        })

        {
            // Menambahkan header pada request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Menambahkan request ke request queue
        queue.add(stringRequest);
    }

    public void deletePenawaran(long id) {
        setLoading(true);
        final StringRequest stringRequest = new StringRequest(DELETE, ApiPenawaran.DELETE_URL + id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                MenuResponse menuResponse =
                        gson.fromJson(response, MenuResponse.class);
                setLoading(false);
                Toast.makeText(ViewPenawaran.this,
                        menuResponse.getMessage(), Toast.LENGTH_SHORT).show();
                getAllPenawaran();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                setLoading(false);
                try {
                    String responseBody = new String(error.networkResponse.data,
                            StandardCharsets.UTF_8);
                    JSONObject errors = new JSONObject(responseBody);
                    Toast.makeText(ViewPenawaran.this,
                            errors.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ViewPenawaran.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        })
        {
            // Menambahkan header pada request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Menambahkan request ke request queue
        queue.add(stringRequest);
    }

    // Fungsi ini digunakan menampilkan layout loading
    private void setLoading(boolean isLoading) {
        if (isLoading) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            layoutLoading.setVisibility(View.VISIBLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            layoutLoading.setVisibility(View.GONE);
        }
    }
}