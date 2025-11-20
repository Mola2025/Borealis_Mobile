package com.example.borealis_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.borealis_mobile.data.ContentRepository;
import com.example.borealis_mobile.model.BaseElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashScreen extends AppCompatActivity {

    private static final String CORE_INDEX_URL = "https://raw.githubusercontent.com/AuroraLegacy/elements/master/core.index";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ContentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        File filesDir = getFilesDir();
        repository = new ContentRepository(filesDir);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }

    private void loadDataAndStart() {
        executor.execute(() -> {
            Map<String, BaseElement> catalogue = null;

            try {
                catalogue = repository.loadCatalogue();

                if (catalogue == null || catalogue.isEmpty()) {
                    catalogue = repository.loadContentFromIndex(CORE_INDEX_URL);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}