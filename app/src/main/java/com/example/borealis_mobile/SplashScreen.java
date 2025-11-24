package com.example.borealis_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.borealis_mobile.core.util.DataHolder;
import com.example.borealis_mobile.data.ContentRepository;
import com.example.borealis_mobile.model.BaseElement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashScreen extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ContentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        File filesDir = getFilesDir();
        repository = new ContentRepository(filesDir);

        loadDataAndStart();
        //new Handler().postDelayed(() -> {
        //    Intent intent = new Intent(SplashScreen.this, AuthActivity.class);
        //    startActivity(intent);
        //    finish();
        //}, 3000);
    }

    private void loadDataAndStart() {
        executor.execute(() -> {
            Map<String, BaseElement> catalogue = null;

            try {
                catalogue = repository.loadFromAssets(this, "dnd_catalogue.json");

                if (catalogue == null || catalogue.isEmpty()) {
                    final Map<String, BaseElement> catToSave = catalogue;
                    executor.execute(() -> {
                        try {
                            repository.saveCatalogue(catToSave);
                        } catch (IOException ignored) { }
                    });
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            final Map<String, BaseElement> finalCatalogue = catalogue;
            handler.post(() -> onDataLoadComplete(finalCatalogue));
        });
    }
    private void onDataLoadComplete(Map<String, BaseElement> loadedCat) {
        if (loadedCat != null) {
            DataHolder.masterCat = loadedCat;
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, AuthActivity.class);
            startActivity(intent);

            finish();
        }, 3000);
    }
}