package com.example.pawonresto.ui.menu;

import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.GET;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pawonresto.Api.ApiMenu;
import com.example.pawonresto.BuildConfig;
import com.example.pawonresto.MainActivity;
import com.example.pawonresto.R;
import com.example.pawonresto.adapter.AdapterMenu;
import com.example.pawonresto.model.Menu;
import com.example.pawonresto.model.MenuResponse;
import com.example.pawonresto.ui.penawaran.ViewPenawaran;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewMenu extends AppCompatActivity {
    public static final int LAUNCH_ADD_ACTIVITY = 123;

    private SwipeRefreshLayout srMenu;
    private AdapterMenu adapter;
    private SearchView svMenu;
    private LinearLayout layoutLoading;
    private RequestQueue queue;
    FloatingActionButton fab_back;
    ImageView ivGambar;

    private FloatingActionButton btnPrint;
    private AlertDialog.Builder builder;
    private static final String TAG = "PdfCreatorActivity";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private File pdfFile;
    private PdfWriter writer;
    List<Menu> menuList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_menu);

        // Pendeklarasian request queue  -> Volley pake ini
        queue = Volley.newRequestQueue(this);

        layoutLoading = findViewById(R.id.layout_loading);
        srMenu = findViewById(R.id.sr_menu);
        svMenu = findViewById(R.id.sv_menu);

        fab_back = findViewById(R.id.fab_maps_back);
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewMenu.this, MainActivity.class));
                finish();
            }
        });

        btnPrint = findViewById(R.id.fab_print);
        btnPrint.setOnClickListener(v -> {
                try {
                    cetakPdf();
                } catch (FileNotFoundException | DocumentException e) {
                    e.printStackTrace();
                }
        });


        srMenu.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllMenu();
            }
        });

        svMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                Intent i = new Intent(ViewMenu.this, AddEditMenu.class);
                startActivityForResult(i, LAUNCH_ADD_ACTIVITY);
            }
        });

        RecyclerView rvMenu = findViewById(R.id.rv_menu);
        adapter = new AdapterMenu(new ArrayList<>(), this);

        int orientation = getResources().getConfiguration().orientation;

        int spanCount = orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        rvMenu.setLayoutManager(new GridLayoutManager(ViewMenu.this, spanCount));


        rvMenu.setAdapter(adapter);

        getAllMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ADD_ACTIVITY && resultCode == Activity.RESULT_OK)
            getAllMenu();
    }


    private void getAllMenu() {
        srMenu.setRefreshing(true);

        final StringRequest stringRequest = new StringRequest(GET, ApiMenu.GET_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                MenuResponse menuResponse =
                        gson.fromJson(response, MenuResponse.class);

                menuList = menuResponse.getMenu();
                adapter.setMenuList(menuResponse.getMenu());
                adapter.getFilter().filter(svMenu.getQuery());


                Toast.makeText(ViewMenu.this,
                        menuResponse.getMessage(), Toast.LENGTH_SHORT).show();
                srMenu.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srMenu.setRefreshing(false);
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject errors = new JSONObject(responseBody);
                    Toast.makeText(ViewMenu.this,
                            errors.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ViewMenu.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        })

        { // Menambahkan header pada request
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

    public void deleteMenu(long id) {
        setLoading(true);
        final StringRequest stringRequest = new StringRequest(DELETE, ApiMenu.DELETE_URL + id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                MenuResponse menuResponse =
                        gson.fromJson(response, MenuResponse.class);
                setLoading(false);
                Toast.makeText(ViewMenu.this,
                        menuResponse.getMessage(), Toast.LENGTH_SHORT).show();
                getAllMenu();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                setLoading(false);
                try {
                    String responseBody = new String(error.networkResponse.data,
                            StandardCharsets.UTF_8);
                    JSONObject errors = new JSONObject(responseBody);
                    Toast.makeText(ViewMenu.this,
                            errors.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ViewMenu.this, e.getMessage(),
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

    //PDFFF
    private void cetakPdf() throws FileNotFoundException, DocumentException {
        File folder = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        if (!folder.exists()) {
            folder.mkdir();
        }

        Date currentTime = Calendar.getInstance().getTime();
        String pdfName = currentTime.getTime() + ".pdf";

        File pdfFile = new File(folder.getAbsolutePath(), pdfName);
        OutputStream outputStream = new FileOutputStream(pdfFile);

        com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Header
        Paragraph judul = new Paragraph("DAFTAR MENU PAWON RESTO \n\n",
                new com.itextpdf.text.Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD, BaseColor.BLACK));

        judul.setAlignment(Element.ALIGN_CENTER);
        document.add(judul);

        // Buat tabel
        PdfPTable tables = new PdfPTable(new float[]{16, 8});

        // Setting ukuran tabel
        tables.getDefaultCell().setFixedHeight(50);
        tables.setTotalWidth(PageSize.A4.getWidth());
        tables.setWidthPercentage(100);
        tables.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell cellSupplier = new PdfPCell();
        cellSupplier.setPaddingLeft(20);
        cellSupplier.setPaddingBottom(10);
        cellSupplier.setBorder(Rectangle.NO_BORDER);


        Paragraph NomorTanggal = new Paragraph("No : " + "123456789" + "\n\n" +
                "Tanggal : " + new SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault()).format(currentTime) + "\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                        com.itextpdf.text.Font.NORMAL, BaseColor.BLACK));

        NomorTanggal.setPaddingTop(5);
        tables.addCell(NomorTanggal);
        document.add(tables);

        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,
                10, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);

        Paragraph Pembuka = new Paragraph("\nBerikut merupakan daftar menu pawon resto: \n\n", f);
        Pembuka.setIndentationLeft(20);
        document.add(Pembuka);
        PdfPTable tableHeader = new PdfPTable(new float[]{5, 5, 5});

        tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeader.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        tableHeader.getDefaultCell().setFixedHeight(30);
        tableHeader.setTotalWidth(PageSize.A4.getWidth());
        tableHeader.setWidthPercentage(100);

        // Setup Column
        PdfPCell h1 = new PdfPCell(new Phrase("No."));
        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
        h1.setPaddingBottom(5);
        PdfPCell h2 = new PdfPCell(new Phrase("Nama Menu"));
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        h2.setPaddingBottom(5);
        PdfPCell h3 = new PdfPCell(new Phrase("Harga Menu"));
        h3.setHorizontalAlignment(Element.ALIGN_CENTER);
        h3.setPaddingBottom(5);


        tableHeader.addCell(h1);
        tableHeader.addCell(h2);
        tableHeader.addCell(h3);

        PdfPCell[] cells = tableHeader.getRow(0).getCells();
        // Beri warna untuk kolumn
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(new BaseColor(212, 170, 125));
        }

        document.add(tableHeader);
        PdfPTable tableData = new PdfPTable(new float[]{5, 5, 5});

        tableData.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableData.getDefaultCell().setFixedHeight(30);
        tableData.setTotalWidth(PageSize.A4.getWidth());
        tableData.setWidthPercentage(100);
        tableData.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);


        int arrLength = menuList.size();

        for(int x=0; x<arrLength; x++){
            for(int i=0;i<cells.length;i++){
                if(i==0){
                    tableData.addCell(String.valueOf(x+1));
                }
                else if(i==1){
                    tableData.addCell(menuList.get(x).getNama_makanan());
                }
                else{
                    tableData.addCell(String.valueOf(menuList.get(x).getHarga_makanan()));
                }
            }
        }

        document.add(tableData);

        com.itextpdf.text.Font h = new
                com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                com.itextpdf.text.Font.NORMAL);

        String tglDicetak = currentTime.toLocaleString();
        Paragraph P = new Paragraph("\nDicetak tanggal " + tglDicetak, h);
        P.setAlignment(Element.ALIGN_RIGHT);
        document.add(P);
        document.close();
        previewPdf(pdfFile);
        Toast.makeText(this, "PDF berhasil dibuat", Toast.LENGTH_SHORT).show();

    }

    private void previewPdf(File pdfFile) {
        PackageManager packageManager = this.getPackageManager();
        Intent cekIntent = new Intent(Intent.ACTION_VIEW);
        cekIntent.setType("application/pdf");
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(cekIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() > 0) {
            Uri uri;
            uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(uri, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            this.grantUriPermission(this.getPackageName(), uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(pdfIntent);
        }
    }
}
