package com.example.borealis_mobile;

import android.net.Uri;
import android.os.Bundle;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.borealis_mobile.model.BaseElement;
import com.example.borealis_mobile.model.Class;
import com.example.borealis_mobile.model.Race;
import com.example.borealis_mobile.model.Item;
import com.example.borealis_mobile.model.Spell;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddProductActivity extends AppCompatActivity {

    EditText editTextName, editTextDescription, editTextPrice;
    //Class Fields ↓↓
    EditText etHitDice, etStartingGold, etProficiencies, etSavingThrows;

    //Race Fields ↓↓
    EditText etSize, etSpeed, etStr, etDex, etCon, etInt, etWis, etCha, etRacialTraits;

    //item Fields ↓↓
    EditText etCategory, etSlot, etCost, etWeight, etArmorClass, etStrengthReq;

    //Spell Fields ↓↓
    EditText etLevel, etSchool, etCastTime, etRange, etComponents, etDuration;

    //Layouts ↓↓
    View layoutClass, layoutRace, layoutItem, layoutSpell;
    Button buttonSave, buttonSelectImage;
    ImageView imageViewProduct;
    ImageButton imageButtonPremium;
    private boolean isPremium = false;
    Spinner spinnerType;
    DatabaseReference databaseProduct;
    StorageReference storageReference;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_product);

        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        editTextName = findViewById(R.id.editTextProductName);
        editTextDescription = findViewById(R.id.editTextProductDescription);
        editTextPrice = findViewById(R.id.editTextProductPrice);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        imageButtonPremium = findViewById(R.id.imageButtonPremium);

        //Layouts
        layoutClass = findViewById(R.id.layoutClass);
        layoutRace = findViewById(R.id.layoutRace);
        layoutItem = findViewById(R.id.layoutItem);
        layoutSpell = findViewById(R.id.layoutSpell);

        //Class Fields
        etHitDice = findViewById(R.id.etHitDice);
        etStartingGold = findViewById(R.id.etStartingGold);
        etProficiencies = findViewById(R.id.etProficiencies);
        etSavingThrows = findViewById(R.id.etSavingThrows);

        //Race Fields
        etSize = findViewById(R.id.etSize);
        etSpeed = findViewById(R.id.etSpeed);
        etStr = findViewById(R.id.etStr);
        etDex = findViewById(R.id.etDex);
        etCon = findViewById(R.id.etCon);
        etInt = findViewById(R.id.etInt);
        etWis = findViewById(R.id.etWis);
        etCha = findViewById(R.id.etCha);
//        etRacialTraits = findViewById(R.id.etRacialTraits);

        //Item Fields
        etCategory = findViewById(R.id.etCategory);
        etSlot = findViewById(R.id.etSlot);
        etCost = findViewById(R.id.etCost);
        etWeight = findViewById(R.id.etWeight);
        etArmorClass = findViewById(R.id.etArmorClass);
        etStrengthReq = findViewById(R.id.etStrengthReq);

        //Spell Fields
        etLevel = findViewById(R.id.etLevel);
        etSchool = findViewById(R.id.etSchool);
        etCastTime = findViewById(R.id.etCastTime);
        etRange = findViewById(R.id.etRange);
        etComponents = findViewById(R.id.etComponents);
        etDuration = findViewById(R.id.etDuration);



        spinnerType = findViewById(R.id.spinnerType);

        buttonSave = findViewById(R.id.buttonSaveProduct);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);


        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageLauncher.launch("image/*");
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });

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

        // Spinner Type Config

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Select a Type","Class", "Race", "Item", "Spell"}
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


    }

    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imageViewProduct.setImageURI(uri);
                }
            });

    private void showTypeSection(String type) {
        layoutClass.setVisibility(View.GONE);
        layoutRace.setVisibility(View.GONE);
        layoutItem.setVisibility(View.GONE);
        layoutSpell.setVisibility(View.GONE);

        switch (type) {
            case "Class": layoutClass.setVisibility(View.VISIBLE); break;
            case "Race": layoutRace.setVisibility(View.VISIBLE); break;
            case "Item": layoutItem.setVisibility(View.VISIBLE); break;
            case "Spell": layoutSpell.setVisibility(View.VISIBLE); break;
        }
    }

    // ADD
    private int safeInt(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }

    // ADD
    private List<String> splitToList(String text) {
        List<String> list = new ArrayList<>();
        if (TextUtils.isEmpty(text)) return list;
        for (String s : text.split(",")) {
            if (!s.trim().isEmpty()) list.add(s.trim());
        }
        return list;
    }


    private void saveProduct() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr) || imageUri == null) {
            Toast.makeText(this, "All fields need to be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        String id = databaseProduct.push().getKey();//generate uniq id for you

        if (id == null) return;

        StorageReference fileRef = storageReference.child(id + ".jpg");
        UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnCompleteListener(task ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // ADD → Crear mapa para campos del tipo seleccionado

                    BaseElement base;
                    switch (type) {
                        case "Class":
                            Class classbase = new Class();
                            classbase.setId(id);
                            classbase.setName(name);
                            classbase.setDescription(description);
                            classbase.setType(type);
                            classbase.setSource("");
                            classbase.setSetters(new HashMap<>());
                            classbase.setRules(new ArrayList<>());
                            classbase.setHitDice(etHitDice.getText().toString().trim());
                            classbase.setStartingGold(etStartingGold.getText().toString().trim());
                            classbase.setProficiencies(splitToList(etProficiencies.getText().toString().trim()));
                            classbase.setSavingThrows(splitToList(etSavingThrows.getText().toString().trim()));
                            base = classbase;
                            break;

                        case "Race":
                            Race racebase = new Race();
                            racebase.setId(id);
                            racebase.setName(name);
                            racebase.setDescription(description);
                            racebase.setType(type);
                            racebase.setSource("");
                            racebase.setSetters(new HashMap<>());
                            racebase.setRules(new ArrayList<>());
                            racebase.setSpeed(safeInt(etSpeed.getText().toString().trim()));

                            HashMap<String, Integer> abilityScores = new HashMap<>();
                            abilityScores.put("STR", safeInt(etStr.getText().toString().trim()));
                            abilityScores.put("DEX", safeInt(etDex.getText().toString().trim()));
                            abilityScores.put("CON", safeInt(etCon.getText().toString().trim()));
                            abilityScores.put("INT", safeInt(etInt.getText().toString().trim()));
                            abilityScores.put("WIS", safeInt(etWis.getText().toString().trim()));
                            abilityScores.put("CHA", safeInt(etCha.getText().toString().trim()));
                            racebase.setAbilityScores(abilityScores);

                            racebase.setRacialTraits(new ArrayList<>());
                            base = racebase;
                            break;

                        case "Item":
                            Item itembase = new Item();
                            itembase.setId(id);
                            itembase.setName(name);
                            itembase.setDescription(description);
                            itembase.setType(type);
                            itembase.setSource("");
                            itembase.setSetters(new HashMap<>());
                            itembase.setRules(new ArrayList<>());
                            itembase.setCategory(etCategory.getText().toString().trim());
                            itembase.setSlot(etSlot.getText().toString().trim());
                            itembase.setCost(etCost.getText().toString().trim());
                            itembase.setWeight(etWeight.getText().toString().trim());
                            itembase.setArmorClass(etArmorClass.getText().toString().trim());
                            itembase.setStrengthReq(etStrengthReq.getText().toString().trim());
                            base = itembase;
                            break;

                        case "Spell":
                            Spell spellbase = new Spell();
                            spellbase.setId(id);
                            spellbase.setName(name);
                            spellbase.setDescription(description);
                            spellbase.setType(type);
                            spellbase.setSource("");
                            spellbase.setSetters(new HashMap<>());
                            spellbase.setRules(new ArrayList<>());
                            spellbase.setLevel(safeInt(etLevel.getText().toString().trim()));
                            spellbase.setSchool(etSchool.getText().toString().trim());
                            spellbase.setCastTime(etCastTime.getText().toString().trim());
                            spellbase.setRange(etRange.getText().toString().trim());
                            spellbase.setComponents(etComponents.getText().toString().trim());
                            spellbase.setDuration(etDuration.getText().toString().trim());
                            base = spellbase;

                            break;

                        default:
                            base = null;
                            break;
                    }
                    //create my object

                    // Type = selection del spinner

                    ShopItem product = new ShopItem(id, base, price, imageUrl, isPremium);
                    databaseProduct.child(id).setValue(product).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Success Product added", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })


        );

    }
}