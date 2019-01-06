package com.example.george.betamdl;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    /*Variables pour le "Navigation Drawer"*/
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    /*--------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Espace Administrateur");

        // Menu de Navigation
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation2);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.addEvent:
                Intent addEventAct = new Intent(AdminActivity.this,AddEventActivity.class);
                this.startActivity(addEventAct);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
