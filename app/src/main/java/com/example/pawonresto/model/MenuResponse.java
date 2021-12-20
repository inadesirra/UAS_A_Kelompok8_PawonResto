package com.example.pawonresto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MenuResponse {
    @SerializedName("message")
    private String message = null;

    @SerializedName("data")
    private List<Menu> data = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Menu> getMenu() {
        return data;
    }

    public void setMenu(List<Menu> data) {
        this.data = data;
    }

}
