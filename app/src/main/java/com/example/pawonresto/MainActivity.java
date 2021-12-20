package com.example.pawonresto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pawonresto.ui.home.HomeFragment;
import com.example.pawonresto.ui.maps.Maps;
import com.example.pawonresto.ui.menu.ViewMenu;
import com.example.pawonresto.ui.penawaran.ViewPenawaran;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String name = getIntent().getStringExtra("name");
        loadFragment(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.home:
                fragment = new HomeFragment();
                return loadFragment(fragment);
            case R.id.menu:
                Intent menu = new Intent(MainActivity.this, ViewMenu.class);
                startActivity(menu);
                break;
            case R.id.notification:
                Intent penawaran = new Intent(MainActivity.this, ViewPenawaran.class);
                startActivity(penawaran);
                break;
            case R.id.location:
                Intent map = new Intent(MainActivity.this,Maps.class);
                startActivity(map);
                break;
        }
        return true;
    }
}