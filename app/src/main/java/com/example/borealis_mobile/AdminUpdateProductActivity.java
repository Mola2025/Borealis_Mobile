package com.example.borealis_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.borealis_mobile.model.BaseElement;
import com.example.borealis_mobile.model.Class;
import com.example.borealis_mobile.model.Race;
import com.example.borealis_mobile.model.Item;
import com.example.borealis_mobile.model.Spell;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminUpdateProductActivity extends AppCompatActivity {

    EditText editTextName, editTextDesc, editTextPrice;

    ImageView imageView;
    ImageButton imageButtonPremium;
    private boolean isPremium = false;
    Button btnChooseImg, btnSaveProd;
    Spinner spinnerType;

    // Class Fields ↓↓
    EditText etHitDiceU, etStartingGoldU, etProficienciesU, etSavingThrowsU;

    // Race Fields ↓↓
    EditText etSizeU, etSpeedU, etStrU, etDexU, etConU, etIntU, etWisU, etChaU, etRacialTraitsU;

    // Item Fields ↓↓
    EditText etCategoryU, etSlotU, etCostU, etWeightU, etArmorClassU, etStrengthReqU;

    // Spell Fields ↓↓
    EditText etLevelU, etSchoolU, etCastTimeU, etRangeU, etComponentsU, etDurationU;

    // Layouts ↓↓
    View layoutClassUpdate, layoutRaceUpdate, layoutItemUpdate, layoutSpellUpdate;

    DatabaseReference databaseProduct;
    StorageReference storageReference;

    String productId, oldImageURL, productType;
    double oldPrice;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_update_product);

        editTextName = findViewById(R.id.etNameEp);
        editTextDesc = findViewById(R.id.etDescEp);
        editTextPrice = findViewById(R.id.etPriceEp);
        imageView = findViewById(R.id.imageViewProductEdit);
        imageButtonPremium = findViewById(R.id.imageButtonPremium);
        btnChooseImg = findViewById(R.id.btnchooseimage);
        btnSaveProd = findViewById(R.id.savechange);
        spinnerType = findViewById(R.id.spinnerTypeUpdate);

        // Layouts
        layoutClassUpdate = findViewById(R.id.layoutClassUpdate);
        layoutRaceUpdate = findViewById(R.id.layoutRaceUpdate);
        layoutItemUpdate = findViewById(R.id.layoutItemUpdate);
        layoutSpellUpdate = findViewById(R.id.layoutSpellUpdate);

        // Class Fields
        etHitDiceU = findViewById(R.id.etHitDiceU);
        etStartingGoldU = findViewById(R.id.etStartingGoldU);
        etProficienciesU = findViewById(R.id.etProficienciesU);
        etSavingThrowsU = findViewById(R.id.etSavingThrowsU);

        // Race Fields
        etSizeU = findViewById(R.id.etSizeU);
        etSpeedU = findViewById(R.id.etSpeedU);
        etStrU = findViewById(R.id.etStrU);
        etDexU = findViewById(R.id.etDexU);
        etConU = findViewById(R.id.etConU);
        etIntU = findViewById(R.id.etIntU);
        etWisU = findViewById(R.id.etWisU);
        etChaU = findViewById(R.id.etChaU);
//        etRacialTraitsU = findViewById(R.id.etRacialTraitsU);

        // Item Fields
        etCategoryU = findViewById(R.id.etCategoryU);
        etSlotU = findViewById(R.id.etSlotU);
        etCostU = findViewById(R.id.etCostU);
        etWeightU = findViewById(R.id.etWeightU);
        etArmorClassU = findViewById(R.id.etArmorClassU);
        etStrengthReqU = findViewById(R.id.etStrengthReqU);

        // Spell Fields
        etLevelU = findViewById(R.id.etLevelU);
        etSchoolU = findViewById(R.id.etSchoolU);
        etCastTimeU = findViewById(R.id.etCastTimeU);
        etRangeU = findViewById(R.id.etRangeU);
        etComponentsU = findViewById(R.id.etComponentsU);
        etDurationU = findViewById(R.id.etDurationU);



        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        // Get data from Intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("productID");
        oldImageURL = intent.getStringExtra("productImageURL");
        oldPrice = intent.getDoubleExtra("productPrice", 0.0);

        editTextName.setText(intent.getStringExtra("productName"));
        editTextDesc.setText(intent.getStringExtra("productDesc"));
        editTextPrice.setText(String.valueOf(oldPrice));


        // Premium Item State
        isPremium = intent.getBooleanExtra("productPremium", false);

        if (isPremium) {
            imageButtonPremium.setImageResource(R.drawable.ic_crown_on);
        } else {
            imageButtonPremium.setImageResource(R.drawable.ic_crown_off);
        }

        // Load the image
        if (oldImageURL != null && !oldImageURL.isEmpty()) {
            loadImageFromURL(oldImageURL);
        }

        btnChooseImg.setOnClickListener(v -> chooseImage());
        btnSaveProd.setOnClickListener(v -> updateProduct());

        imageButtonPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPremium = !isPremium;
                if (isPremium) {
                    imageButtonPremium.setImageResource(R.drawable.ic_crown_on);
                } else {
                    imageButtonPremium.setImageResource(R.drawable.ic_crown_off);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Select a Type", "Class", "Race", "Item", "Spell"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showTypeSection(spinnerType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Cargar datos completos desde Firebase
        loadProductFromFirebase();
    }

    private void showTypeSection(String type) {
        layoutClassUpdate.setVisibility(View.GONE);
        layoutRaceUpdate.setVisibility(View.GONE);
        layoutItemUpdate.setVisibility(View.GONE);
        layoutSpellUpdate.setVisibility(View.GONE);

        switch (type) {
            case "Class": layoutClassUpdate.setVisibility(View.VISIBLE); break;
            case "Race": layoutRaceUpdate.setVisibility(View.VISIBLE); break;
            case "Item": layoutItemUpdate.setVisibility(View.VISIBLE); break;
            case "Spell": layoutSpellUpdate.setVisibility(View.VISIBLE); break;
        }
    }

    private void setSpinnerSelection(String productType) {
        if (productType != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerType.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(productType)) {
                    spinnerType.setSelection(i);
                    showTypeSection(productType);
                    break;
                }
            }
        }
    }

    private void loadProductFromFirebase() {
        if (productId == null) {
            Toast.makeText(this, "Error: Product ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseProduct.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ShopItem shopItem = snapshot.getValue(ShopItem.class);
                    if (shopItem != null && shopItem.getBaseElement() != null) {
                        BaseElement baseElement = shopItem.getBaseElement();
                        String productType = baseElement.getType();

                        // Actualizar el spinner con el tipo correcto
                        setSpinnerSelection(productType);

                        // Cargar los datos específicos del tipo
                        loadTypeSpecificData(baseElement, productType);
                    }
                } else {
                    Toast.makeText(AdminUpdateProductActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUpdateProductActivity.this, "Error loading product: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTypeSpecificData(BaseElement baseElement, String productType) {
        switch (productType) {
            case "Class":
                if (baseElement instanceof Class) {
                    Class classItem = (Class) baseElement;
                    classItem.setId(productId);
                    etHitDiceU.setText(classItem.getHitDice());
                    etStartingGoldU.setText(classItem.getStartingGold());
                    etProficienciesU.setText(convertListToString(classItem.getProficiencies()));
                    etSavingThrowsU.setText(convertListToString(classItem.getSavingThrows()));
                }
                break;

            case "Race":
                if (baseElement instanceof Race) {
                    Race raceItem = (Race) baseElement;
                    raceItem.setId(productId);
                    etSizeU.setText(raceItem.getSize());
                    etSpeedU.setText(String.valueOf(raceItem.getSpeed()));

                    Map<String, Integer> abilityScores = raceItem.getAbilityScores();
                    if (abilityScores != null) {
                        etStrU.setText(String.valueOf(abilityScores.getOrDefault("STR", 0)));
                        etDexU.setText(String.valueOf(abilityScores.getOrDefault("DEX", 0)));
                        etConU.setText(String.valueOf(abilityScores.getOrDefault("CON", 0)));
                        etIntU.setText(String.valueOf(abilityScores.getOrDefault("INT", 0)));
                        etWisU.setText(String.valueOf(abilityScores.getOrDefault("WIS", 0)));
                        etChaU.setText(String.valueOf(abilityScores.getOrDefault("CHA", 0)));
                    }
                }
                break;

            case "Item":
                if (baseElement instanceof Item) {
                    Item item = (Item) baseElement;
                    item.setId(productId);
                    etCategoryU.setText(item.getCategory());
                    etSlotU.setText(item.getSlot());
                    etCostU.setText(item.getCost());
                    etWeightU.setText(item.getWeight());
                    etArmorClassU.setText(item.getArmorClass());
                    etStrengthReqU.setText(item.getStrengthReq());
                }
                break;

            case "Spell":
                if (baseElement instanceof Spell) {
                    Spell spell = (Spell) baseElement;
                    spell.setId(productId);
                    etLevelU.setText(String.valueOf(spell.getLevel()));
                    etSchoolU.setText(spell.getSchool());
                    etCastTimeU.setText(spell.getCastTime());
                    etRangeU.setText(spell.getRange());
                    etComponentsU.setText(spell.getComponents());
                    etDurationU.setText(spell.getDuration());
                }
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Product Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    // Update
    private int safeInt(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }

    // Update
    private List<String> splitToList(String text) {
        List<String> list = new ArrayList<>();
        if (TextUtils.isEmpty(text)) return list;
        for (String s : text.split(",")) {
            if (!s.trim().isEmpty()) list.add(s.trim());
        }
        return list;
    }

    private String convertListToString(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private HashMap<String, Integer> getAbilityScoresMap() {
        HashMap<String, Integer> abilityScores = new HashMap<>();
        abilityScores.put("STR", safeInt(etStrU.getText().toString().trim()));
        abilityScores.put("DEX", safeInt(etDexU.getText().toString().trim()));
        abilityScores.put("CON", safeInt(etConU.getText().toString().trim()));
        abilityScores.put("INT", safeInt(etIntU.getText().toString().trim()));
        abilityScores.put("WIS", safeInt(etWisU.getText().toString().trim()));
        abilityScores.put("CHA", safeInt(etChaU.getText().toString().trim()));
        return abilityScores;
    }

    private void updateProduct() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDesc.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || "Select a Type".equals(type)) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        //Si quiero despues aca una validacion

        double price = Double.parseDouble(priceStr);


        if (selectedImageUri != null) {
            StorageReference fileRef = storageReference.child(UUID.randomUUID().toString());
            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        deleteOldImage();
                        updateProductInDatabase(name, description, price, uri.toString(), type);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            updateProductInDatabase(name, description, price, oldImageURL, type);
        }
    }

    private void updateProductInDatabase(String name, String description, double price, String imageUrl, String type) {

        BaseElement base;

        switch (type) {
            case "Class":
                Class classbase = new Class();
                classbase.setName(name);
                classbase.setDescription(description);
                classbase.setType(type);
                classbase.setSource("");
                classbase.setSetters(new HashMap<>());
                classbase.setRules(new ArrayList<>());
                classbase.setHitDice(etHitDiceU.getText().toString().trim());
                classbase.setStartingGold(etStartingGoldU.getText().toString().trim());
                classbase.setProficiencies(splitToList(etProficienciesU.getText().toString().trim()));
                classbase.setSavingThrows(splitToList(etSavingThrowsU.getText().toString().trim()));
                base = classbase;
                break;

            case "Race":
                Race racebase = new Race();
                racebase.setName(name);
                racebase.setDescription(description);
                racebase.setType(type);
                racebase.setSource("");
                racebase.setSetters(new HashMap<>());
                racebase.setRules(new ArrayList<>());
                racebase.setSize(etSizeU.getText().toString().trim());
                racebase.setSpeed(safeInt(etSpeedU.getText().toString().trim()));
                racebase.setAbilityScores(getAbilityScoresMap());
                racebase.setRacialTraits(new ArrayList<>());
                base = racebase;
                break;

            case "Item":
                Item itembase = new Item();
                itembase.setName(name);
                itembase.setDescription(description);
                itembase.setType(type);
                itembase.setSource("");
                itembase.setSetters(new HashMap<>());
                itembase.setRules(new ArrayList<>());
                itembase.setCategory(etCategoryU.getText().toString().trim());
                itembase.setSlot(etSlotU.getText().toString().trim());
                itembase.setCost(etCostU.getText().toString().trim());
                itembase.setWeight(etWeightU.getText().toString().trim());
                itembase.setArmorClass(etArmorClassU.getText().toString().trim());
                itembase.setStrengthReq(etStrengthReqU.getText().toString().trim());
                base = itembase;
                break;

            case "Spell":
                Spell spellbase = new Spell();
                spellbase.setName(name);
                spellbase.setDescription(description);
                spellbase.setType(type);
                spellbase.setSource("");
                spellbase.setSetters(new HashMap<>());
                spellbase.setRules(new ArrayList<>());
                spellbase.setLevel(safeInt(etLevelU.getText().toString().trim()));
                spellbase.setSchool(etSchoolU.getText().toString().trim());
                spellbase.setCastTime(etCastTimeU.getText().toString().trim());
                spellbase.setRange(etRangeU.getText().toString().trim());
                spellbase.setComponents(etComponentsU.getText().toString().trim());
                spellbase.setDuration(etDurationU.getText().toString().trim());
                base = spellbase;
                break;

            default:
                base = new BaseElement(name, type, productId, "", description, null, null);
                break;
        }

        ShopItem updatedProduct = new ShopItem(productId, base, price, imageUrl, isPremium);

        databaseProduct.child(productId).setValue(updatedProduct)
                .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Product updated!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, AdminHomePageActivity.class);
                        startActivity(intent);
                        finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    // 🔹 Load image from URL
    private void loadImageFromURL(String imageUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                // update UI on main thread
                new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(bitmap));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void deleteOldImage(){
        if(oldImageURL != null && !oldImageURL.isEmpty()){
            try{
                StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                oldImageRef.delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Old image deleted successfully", Toast.LENGTH_SHORT).show();
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}