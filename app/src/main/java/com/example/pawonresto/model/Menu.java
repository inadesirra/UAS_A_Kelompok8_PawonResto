package com.example.pawonresto.model;

public class Menu{
    private Long id;
    private String imgURL;
    private String nama_makanan;
    private double harga_makanan;

    public Menu(String imgURL, String nama_makanan, double harga_makanan) {
        this.imgURL = imgURL;
        this.nama_makanan = nama_makanan;
        this.harga_makanan = harga_makanan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getNama_makanan() {
        return nama_makanan;
    }

    public void setNama_makanan(String nama_makanan) {
        this.nama_makanan = nama_makanan;
    }
    
    public double getHarga_makanan() {
        return harga_makanan;
    }

    public void setHarga_makanan(double harga_makanan) {
        this.harga_makanan = harga_makanan;
    }
}