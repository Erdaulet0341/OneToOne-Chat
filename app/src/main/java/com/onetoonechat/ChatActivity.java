package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    PageAdapterFragment pageAdapterFragment;
    ViewPager viewPager;
    TabItem chat, status, calls;
    TabLayout tabLayout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.fragmentContainer);
        chat = findViewById(R.id.chats);
        status = findViewById(R.id.status);
        calls = findViewById(R.id.calls);
        tabLayout = findViewById(R.id.tabLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the token
                    String token = task.getResult();
                    Log.d("tokennn", token);
                });
        setSupportActionBar(toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_more_vert_24);
        toolbar.setOverflowIcon(drawable);

        pageAdapterFragment = new PageAdapterFragment(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapterFragment);

        boolean isInternetAvailable = NetworkUtils.isNetworkAvailable(this);
        checkInternet(isInternetAvailable);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition()==0 || tab.getPosition() == 1 || tab.getPosition() == 2){
                    pageAdapterFragment.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void checkInternet(boolean isInternetAvailable) {
        if(!isInternetAvailable)  Toast.makeText(this, getString(R.string.check_interne), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String itemId, menuP, menuS, menuL;
        itemId = String.valueOf(item.getItemId());
        menuS = String.valueOf(R.id.menu_theme);
        menuP = String.valueOf(R.id.menu_profile);
        menuL = String.valueOf(R.id.menu_language);
        if(itemId.equals(menuP)){
            Intent intent = new Intent(ChatActivity.this, Profile.class);
            startActivity(intent);
        }
        else if(itemId.equals(menuS)) {

            final String[] themes = {getString(R.string.dark), getString(R.string.light)};
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.select_theme);
            dialog.setSingleChoiceItems(themes, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i == 0){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SharedPreferences.Editor editor = getSharedPreferences("theme_mode", MODE_PRIVATE).edit();
                        editor.putString("theme", "dark").apply();
                    }
                    else if(i==1){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SharedPreferences.Editor editor = getSharedPreferences("theme_mode", MODE_PRIVATE).edit();
                        editor.putString("theme", "light").apply();
                    }
                }
            });
            dialog.create();
            dialog.show();
        }
        else if(itemId.equals(menuL)){
            final String[] languages = {"English", "Руский", "Қазақ тілі"};
            AlertDialog.Builder diolag = new AlertDialog.Builder(this);
            diolag.setTitle(R.string.chosse);
            diolag.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i == 0){
                        setLocale("en");
                        recreate();
                    }
                    else if(i == 1){
                        setLocale("ru");
                        recreate();
                    }else if(i == 2){
                        setLocale("kk");
                        recreate();
                    }
                }
            });
            diolag.create();
            diolag.show();
        }
        return true;
    }

    void setLocale(String lan){
        Locale locale = new Locale(lan);
        Configuration configuration = new Configuration();
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Language_set", MODE_PRIVATE).edit();
        editor.putString("app_language", lan).apply();
    }

    void loadLocale(){
        SharedPreferences pre = getSharedPreferences("Language_set", Activity.MODE_PRIVATE);
        setLocale(pre.getString("app_language", Locale.getDefault().getLanguage()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_chat,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserStatusManager.getInstance().updateUserStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserStatusManager.getInstance().updateUserStatus("Offline");
    }
}