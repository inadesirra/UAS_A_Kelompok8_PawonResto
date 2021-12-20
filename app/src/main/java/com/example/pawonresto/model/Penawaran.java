package com.example.pawonresto.model;

import androidx.databinding.BaseObservable;

import java.io.Serializable;

public class Penawaran extends BaseObservable implements Serializable {
    private Long id;
    public String imgURL;
    private String judul;
    private String deskripsi;

    public Penawaran(String judul, String deskripsi, String imgURL) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.imgURL = imgURL;
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

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
