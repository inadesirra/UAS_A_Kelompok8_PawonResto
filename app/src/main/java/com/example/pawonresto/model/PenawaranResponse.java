package com.example.pawonresto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PenawaranResponse {
    @SerializedName("message")
    private String message = null;

    @SerializedName("data")
    private List<Penawaran> data = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Penawaran> getPenawaran() {
        return data;
    }

    public void setPenawaran(List<Penawaran> data) {
        this.data = data;
    }
}
