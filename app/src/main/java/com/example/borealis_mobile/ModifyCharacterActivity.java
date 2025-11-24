package com.example.borealis_mobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.borealis_mobile.core.engine.CharacterSheetEngine;
import com.example.borealis_mobile.core.util.DataHolder;
import com.example.borealis_mobile.data.CharacterRepository;
import com.example.borealis_mobile.model.BaseElement;
import com.example.borealis_mobile.model.Character;

import com.example.borealis_mobile.model.Spell;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModifyCharacterActivity extends AppCompatActivity {

    // 1. Header
    private TextView tvCharacterHeader;

    // 2. Containers
    private LinearLayout contentSectionsContainer;

    // 3. BUILD
    private RelativeLayout headerBuild;
    private LinearLayout contentBuild;
    private ImageView indicatorBuild;
    private AutoCompleteTextView autoCompleteRace;
    private AutoCompleteTextView autoCompleteClass;
    private AutoCompleteTextView autoCompleteStr;
    private AutoCompleteTextView autoCompleteDex;
    private AutoCompleteTextView autoCompleteCon;
    private AutoCompleteTextView autoCompleteInt;
    private AutoCompleteTextView autoCompleteWis;
    private AutoCompleteTextView autoCompleteCha;

    // 4. MAGIC
    private RelativeLayout headerMagic;
    private LinearLayout contentMagic;
    private ImageView indicatorMagic;
    private ImageView disabledMagic;
    private RelativeLayout headerSpells;
    private LinearLayout contentSpells;
    private ImageView indicatorSpells;
    private AutoCompleteTextView autoCompleteSpell1;
    private AutoCompleteTextView autoCompleteSpell2;
    private AutoCompleteTextView autoCompleteSpell3;

    // 5. EQUIPMENT
    private RelativeLayout headerEquipment;
    private LinearLayout contentEquipment;
    private ImageView indicatorEquipment;
    private AutoCompleteTextView autoCompleteEquipment;
    private LinearLayout inventoryItemsContainer;

    // 6. MANAGE
    private RelativeLayout headerManage;
    private LinearLayout contentManage;
    private ImageView indicatorManage;
    private TextInputEditText etCharacterName;
    private TextInputEditText etCharacterLevel;
    private LinearLayout featuresTraitsContainer;

    // 7. CHARACTER SHEET
    private RelativeLayout headerSheet;
    private LinearLayout contentSheet;
    private ImageView indicatorSheet;
    private TextView tvSheetRace;
    private TextView tvSheetClass;
    private TextView tvSheetAC;
    private TextView tvSheetHP;
    private TextView tvSheetSTR;
    private TextView tvSheetDEX;
    private TextView tvSheetCON;
    private TextView tvSheetINT;
    private TextView tvSheetWIS;
    private TextView tvSheetCHA;
    private LinearLayout inventorySheetContainer;

    private Button btnSaveCharacter;

    private List<BaseElement> items = new ArrayList<>();
    private List<BaseElement> spells = new ArrayList<>();
    private CharacterRepository charRepo;
    private Character currentChar;
    private static final List<String> MAGIC_CLASSES = List
            .of("Bard", "Cleric", "Druid", "Paladin", "Ranger", "Sorcerer", "Warlock", "Wizard");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_character);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Modify Character");
        }
        charRepo = new CharacterRepository(getFilesDir());

        initializeViews();
        initializeCatalogueData();

        if (getIntent().hasExtra("character")) {
            currentChar = getIntent().getParcelableExtra("character");
            if (currentChar == null)
                currentChar = new Character();
        } else {
            currentChar = new Character();
        }

        setupTextWatchers();
        loadCharacterData(currentChar);
        setupCollapsibleSections();
        checkMagicClass(currentChar.getClassTypeId());
        populateDropdowns();

        btnSaveCharacter.setOnClickListener(v -> saveCharacter());
    }
    private void saveCharacter() {
        String name = etCharacterName.getText().toString().trim();
        String levelStr = etCharacterLevel.getText().toString().trim();

        if (name.isEmpty()) {
            etCharacterName.setError("Name is required");
            return;
        }

        currentChar.setCharName(name);
        currentChar.setLevel(levelStr.isEmpty() ? "1" : levelStr);

        currentChar.setRace(autoCompleteRace.getText().toString());
        currentChar.setClassTypeId(autoCompleteClass.getText().toString());

        currentChar.setStats(new HashMap<>());
        currentChar.getStats().put("STR", Integer.parseInt(autoCompleteStr.getText().toString()));
        currentChar.getStats().put("DEX", Integer.parseInt(autoCompleteDex.getText().toString()));
        currentChar.getStats().put("CON", Integer.parseInt(autoCompleteCon.getText().toString()));
        currentChar.getStats().put("INT", Integer.parseInt(autoCompleteInt.getText().toString()));
        currentChar.getStats().put("WIS", Integer.parseInt(autoCompleteWis.getText().toString()));
        currentChar.getStats().put("CHA", Integer.parseInt(autoCompleteCha.getText().toString()));

        currentChar.setSpells(new ArrayList<>());
        currentChar.getSpells().add(autoCompleteSpell1.getText().toString());
        currentChar.getSpells().add(autoCompleteSpell2.getText().toString());
        currentChar.getSpells().add(autoCompleteSpell3.getText().toString());

        currentChar.setInventory(new ArrayList<>());
        for (int i = 0; i < inventoryItemsContainer.getChildCount(); i++) {
            LinearLayout subCont = (LinearLayout) inventoryItemsContainer.getChildAt(i);
            View v = subCont.getChildAt(1);
            if (v instanceof TextView) {
                currentChar.getInventory().add(((TextView) v).getText().toString());
            }
        }

        try {
            List<Character> allCharacters = charRepo.loadCharacters();

            boolean found = false;
            for (int i = 0; i < allCharacters.size(); i++) {
                if (allCharacters.get(i).getCharName().equals(currentChar.getCharName())) {
                    allCharacters.set(i, currentChar);
                    found = true;
                    break;
                }
            }

            if (!found) {
                allCharacters.add(currentChar);
            }

            charRepo.saveCharacters(allCharacters);

            Toast.makeText(this, "Character Saved!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving character", Toast.LENGTH_SHORT).show();
        }
    }
    private void initializeCatalogueData() {
        Map<String, BaseElement> catalogue = DataHolder.masterCat;
        if (catalogue == null || catalogue.isEmpty()) {
            Toast.makeText(this, "Error: Catalogue data is empty.", Toast.LENGTH_LONG).show();
            return;
        }

        items.clear();
        spells.clear();

        for (BaseElement element : catalogue.values()) {
            if (element.getType() == null) continue;

            String type = element.getType();

            if (type.equals("Armor") || type.equals("Weapon") || type.equals("Magic Item") || type.equals("Item")) {
                items.add(element);
            }
            else if (type.equals("Spell")) {
                spells.add(element);
            }
        }
    }

    private void initializeViews() {
        tvCharacterHeader = findViewById(R.id.tvCharacterHeader);
        contentSectionsContainer = findViewById(R.id.contentSectionsContainer);
        btnSaveCharacter = findViewById(R.id.btnSaveCharacter);

        // BUILD
        headerBuild = findViewById(R.id.headerBuild);
        contentBuild = findViewById(R.id.contentBuild);
        indicatorBuild = findViewById(R.id.indicatorBuild);
        autoCompleteRace = findViewById(R.id.autoCompleteRace);
        autoCompleteClass = findViewById(R.id.autoCompleteClass);
        autoCompleteStr = findViewById(R.id.auto_complete_str);
        autoCompleteDex = findViewById(R.id.auto_complete_dex);
        autoCompleteCon = findViewById(R.id.auto_complete_con);
        autoCompleteInt = findViewById(R.id.auto_complete_int);
        autoCompleteWis = findViewById(R.id.auto_complete_wis);
        autoCompleteCha = findViewById(R.id.auto_complete_cha);

        // MAGIC
        headerMagic = findViewById(R.id.headerMagic);
        contentMagic = findViewById(R.id.contentMagic);
        indicatorMagic = findViewById(R.id.indicatorMagic);
        disabledMagic = findViewById(R.id.disabledMagic);
        headerSpells = findViewById(R.id.header_spells);
        contentSpells = findViewById(R.id.content_spells);
        indicatorSpells = findViewById(R.id.indicator_spells);
        autoCompleteSpell1 = findViewById(R.id.auto_complete_spell_1);
        autoCompleteSpell2 = findViewById(R.id.auto_complete_spell_2);
        autoCompleteSpell3 = findViewById(R.id.auto_complete_spell_3);

        // EQUIPMENT
        headerEquipment = findViewById(R.id.headerEquipment);
        contentEquipment = findViewById(R.id.contentEquipment);
        indicatorEquipment = findViewById(R.id.indicatorEquipment);
        autoCompleteEquipment = findViewById(R.id.autoCompleteEquipment);
        inventoryItemsContainer = findViewById(R.id.inventoryItemsContainer);

        // MANAGE
        headerManage = findViewById(R.id.headerManage);
        contentManage = findViewById(R.id.contentManage);
        indicatorManage = findViewById(R.id.indicatorManage);
        etCharacterName = findViewById(R.id.etCharacterName);
        etCharacterLevel = findViewById(R.id.etCharacterLevel);
        featuresTraitsContainer = findViewById(R.id.featuresTraitsContainer);

        // CHARACTER SHEET
        headerSheet = findViewById(R.id.headerSheet);
        contentSheet = findViewById(R.id.contentSheet);
        indicatorSheet = findViewById(R.id.indicatorSheet);
        tvSheetRace = findViewById(R.id.tvSheetRace);
        tvSheetClass = findViewById(R.id.tvSheetClass);
        tvSheetAC = findViewById(R.id.tvSheetAC);
        tvSheetHP = findViewById(R.id.tvSheetHP);
        tvSheetSTR = findViewById(R.id.tvSheetSTR);
        tvSheetDEX = findViewById(R.id.tvSheetDEX);
        tvSheetCON = findViewById(R.id.tvSheetCON);
        tvSheetINT = findViewById(R.id.tvSheetINT);
        tvSheetWIS = findViewById(R.id.tvSheetWIS);
        tvSheetCHA = findViewById(R.id.tvSheetCHA);
        inventorySheetContainer = findViewById(R.id.inventorySheetContainer);
    }
    private void setupTextWatchers() {
        TextWatcher headerUpdaterWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateCharacterHeader();
            }
        };

        etCharacterName.addTextChangedListener(headerUpdaterWatcher);
        etCharacterLevel.addTextChangedListener(headerUpdaterWatcher);
    }
    private void updateCharacterHeader() {
        String name = Objects.requireNonNull(etCharacterName.getText()).toString().trim();
        String levelString = Objects.requireNonNull(etCharacterLevel.getText()).toString().trim();

        if (name.isEmpty()) {
            name = "Unnamed Character";
        }
        if (levelString.isEmpty() || !levelString.matches("\\d+")) {
            levelString = "0";
        }

        tvCharacterHeader.setText(name + " (Level " + levelString + ")");
    }
    private void updateCharacterSheet() {
        String race = autoCompleteRace.getText().toString();
        String charClass = autoCompleteClass.getText().toString();
        String level = etCharacterLevel.getText().toString();

        tvSheetRace.setText(race.isEmpty() ? "Unknown Race" : race);
        tvSheetClass.setText((charClass.isEmpty() ? "Commoner" : charClass) + " (Lvl " + level + ")");

        tvSheetSTR.setText(CharacterSheetEngine.formatStatDisplay(autoCompleteStr.getText().toString()));
        tvSheetDEX.setText(CharacterSheetEngine.formatStatDisplay(autoCompleteDex.getText().toString()));
        tvSheetCON.setText(CharacterSheetEngine.formatStatDisplay(autoCompleteCon.getText().toString()));
        tvSheetINT.setText(CharacterSheetEngine.formatStatDisplay(autoCompleteInt.getText().toString()));
        tvSheetWIS.setText(CharacterSheetEngine.formatStatDisplay(autoCompleteWis.getText().toString()));
        tvSheetCHA.setText(CharacterSheetEngine.formatStatDisplay(autoCompleteCha.getText().toString()));

        tvSheetAC.setText(CharacterSheetEngine.calculateBasicAC(autoCompleteDex.getText().toString()));
        tvSheetHP.setText(CharacterSheetEngine.calculateMaxHP(charClass, autoCompleteCon.getText().toString(), level, DataHolder.masterCat));
        inventorySheetContainer.removeAllViews();

        addSectionHeaderToSheet("Spells Known");
        addTextToSheetIfNotEmpty(autoCompleteSpell1.getText().toString());
        addTextToSheetIfNotEmpty(autoCompleteSpell2.getText().toString());
        addTextToSheetIfNotEmpty(autoCompleteSpell3.getText().toString());

        addSectionHeaderToSheet("Equipment");
        int itemCount = inventoryItemsContainer.getChildCount();
        if (itemCount == 0) {
            TextView empty = new TextView(this);
            empty.setText("(Empty Bag)");
            empty.setTextSize(14f);
            inventorySheetContainer.addView(empty);
        } else {
            for (int i = 0; i < itemCount; i++) {
                LinearLayout subCont = (LinearLayout) inventoryItemsContainer.getChildAt(i);
                if (subCont.getChildAt(1) instanceof TextView) {
                    View v = subCont.getChildAt(1);
                    String itemText = ((TextView) v).getText().toString();
                    addTextToSheetIfNotEmpty(itemText.trim());
                }
            }
        }
    }
    private void addSectionHeaderToSheet(String title) {
        TextView header = new TextView(this);
        header.setText(title);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, 16, 0, 4);
        inventorySheetContainer.addView(header);
    }
    private void addTextToSheetIfNotEmpty(String text) {
        if (text != null && !text.trim().isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(text.startsWith("•") ? "   " + text : "   • " + text);
            tv.setTextSize(14f);
            inventorySheetContainer.addView(tv);
        }
    }
    private void addItemToInventoryList(String itemName) {
        LinearLayout inventorySubContainer = new LinearLayout(this);
        inventorySubContainer.setOrientation(LinearLayout.HORIZONTAL);
        inventorySubContainer.setPadding(8, 8, 8, 8);

        TextView itemView = new TextView(this);
        TextView itemDot = new TextView(this);
        itemDot.setText("•");
        itemDot.setTextSize(16f);
        itemView.setText(itemName);
        itemView.setTextSize(16f);

        inventorySubContainer.setOnClickListener(v -> {
            inventoryItemsContainer.removeView(v);
            Toast.makeText(this, "Item removed from void", Toast.LENGTH_SHORT).show();
        });
        inventoryItemsContainer.addView(inventorySubContainer);
        inventorySubContainer.addView(itemDot);
        inventorySubContainer.addView(itemView);
    }
    private void setupCollapsibleSections() {
        // BUILD
        setupCollapsibleSection(headerBuild, contentBuild, indicatorBuild, false, null);

        // MAGIC
        setupCollapsibleSection(headerMagic, contentMagic, indicatorMagic, true, disabledMagic);
        setupCollapsibleSection(headerSpells, contentSpells, indicatorSpells, false, null);

        // EQUIPMENT
        setupCollapsibleSection(headerEquipment, contentEquipment, indicatorEquipment, false, null);

        // MANAGE
        setupCollapsibleSection(headerManage, contentManage, indicatorManage, false, null);

        // CHARACTER SHEET
        setupCharacterSheetSection(headerSheet, contentSheet, indicatorSheet);
    }

    private void populateDropdowns() {
        Map<String, BaseElement> catalogue = DataHolder.masterCat;
        if (catalogue.isEmpty()) {
            Toast.makeText(this, "Error: Catalogue not loaded.", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> raceNames = new ArrayList<>();
        List<String> classNames = new ArrayList<>();

        for (BaseElement element : catalogue.values()) {
            if (element.getType() != null) {
                if (element.getType().equals("Race")) {
                    raceNames.add(element.getName());
                } else if (element.getType().equals("Class")) {
                    classNames.add(element.getName());
                }
            }
        }

        List<String> spellNames = new ArrayList<>();
        for (BaseElement element : catalogue.values()) {
            if ("Spell".equals(element.getType())) {
                spellNames.add(element.getName());
            }
        }
        ArrayAdapter<String> spellAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, spellNames);
        if (autoCompleteSpell1 != null) autoCompleteSpell1.setAdapter(spellAdapter);
        if (autoCompleteSpell2 != null) autoCompleteSpell2.setAdapter(spellAdapter);
        if (autoCompleteSpell3 != null) autoCompleteSpell3.setAdapter(spellAdapter);

        List<String> equipmentNames = new ArrayList<>();
        for (BaseElement element : catalogue.values()) {
            String t = element.getType();
            if ("Item".equals(t) || "Armor".equals(t) || "Weapon".equals(t) || "Magic Item".equals(t)) {
                equipmentNames.add(element.getName());
            }
        }
        autoCompleteEquipment.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            addItemToInventoryList(selectedItem);
            autoCompleteEquipment.setText("");
        });

        String[] standardArray = {"15", "14", "13", "12", "10", "8"};
        ArrayAdapter<String> statAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, standardArray);
        autoCompleteStr.setAdapter(statAdapter);
        autoCompleteDex.setAdapter(statAdapter);
        autoCompleteCon.setAdapter(statAdapter);
        autoCompleteInt.setAdapter(statAdapter);
        autoCompleteWis.setAdapter(statAdapter);
        autoCompleteCha.setAdapter(statAdapter);

        ArrayAdapter<String> equipmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, equipmentNames);
        if (autoCompleteEquipment != null) {
            autoCompleteEquipment.setAdapter(equipmentAdapter);
        }

        ArrayAdapter<String> raceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                raceNames
        );
        autoCompleteRace.setAdapter(raceAdapter);

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                classNames
        );
        autoCompleteClass.setAdapter(classAdapter);
        autoCompleteClass.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String className = (String) parent.getItemAtPosition(position);
                    checkMagicClass(className);
        });
    }
    private void loadCharacterData(Character c) {
        if (c == null) return;

        tvCharacterHeader.setText(c.getCharName() + " (Level " + c.getLevel() + ")");
        etCharacterName.setText(c.getCharName());
        etCharacterLevel.setText(String.valueOf(c.getLevel()));
        autoCompleteRace.setText(c.getRace(), false);
        autoCompleteClass.setText(c.getClassTypeId(), false);

        if (c.getStats() != null) {
            autoCompleteStr.setText(String.valueOf(c.getStats().getOrDefault("STR", 10)));
            autoCompleteDex.setText(String.valueOf(c.getStats().getOrDefault("DEX", 10)));
            autoCompleteCon.setText(String.valueOf(c.getStats().getOrDefault("CON", 10)));
            autoCompleteInt.setText(String.valueOf(c.getStats().getOrDefault("INT", 10)));
            autoCompleteWis.setText(String.valueOf(c.getStats().getOrDefault("WIS", 10)));
            autoCompleteCha.setText(String.valueOf(c.getStats().getOrDefault("CHA", 10)));
        }
        if (c.getInventory() != null) {
            for (String item : c.getInventory()) {
                addItemToInventoryList(item);
            }
        }
        if (c.getSpells() != null) {
            List<String> spells = c.getSpells();
            if (!spells.isEmpty()) autoCompleteSpell1.setText(spells.get(0), false);
            if (spells.size() > 1) autoCompleteSpell2.setText(spells.get(1), false);
            if (spells.size() > 2) autoCompleteSpell3.setText(spells.get(2), false);
        }
    }
    private void checkMagicClass(String className) {
        if (className == null || className.trim().isEmpty()) {
            enableSpellsSection(false);
            currentChar.setSpells(new ArrayList<>());
            return;
        }
        boolean isMagic = MAGIC_CLASSES.contains(className.trim());
        enableSpellsSection(isMagic);
    }
    private void enableSpellsSection(boolean enable) {
        setupCollapsibleSection(
                headerSpells,
                contentSpells,
                indicatorSpells,
                !enable,
                null
        );
        if (!enable) {
            contentSpells.setVisibility(View.GONE);
            indicatorSpells.setRotation(0f);
        } else {
            setupCollapsibleSection(headerMagic, contentMagic, indicatorMagic, false, null);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupCollapsibleSection(RelativeLayout header, final LinearLayout content, final ImageView indicator, boolean isDisabled, @Nullable ImageView disabledIndicator) {

        if (isDisabled) {
            header.setAlpha(0.5f);
            header.getChildAt(0).setAlpha(0.5f);
            header.setClickable(false);
            content.setVisibility(View.GONE);
            content.setActivated(false);
            if (disabledIndicator != null) {
                disabledIndicator.setVisibility(View.VISIBLE);
            }
            autoCompleteSpell1.setText("");
            autoCompleteSpell2.setText("");
            autoCompleteSpell3.setText("");
            return;
        } else {
            header.setAlpha(1f);
            header.getChildAt(0).setAlpha(1f);
            header.setClickable(true);
            content.setActivated(true);
            if (disabledIndicator != null) {
                disabledIndicator.setVisibility(View.GONE);
                disabledIndicator.setAlpha(0f);
            }
        }

        header.setOnClickListener(v -> toggleSection(content, indicator));
    }

    private void setupCharacterSheetSection(RelativeLayout header, final LinearLayout content, final ImageView indicator) {
        header.setOnClickListener(v -> {
            boolean isExpanded = content.getVisibility() == View.VISIBLE;

            if (!isExpanded) {
                updateCharacterSheet();
                contentSectionsContainer.setVisibility(View.GONE);
            } else {
                contentSectionsContainer.setVisibility(View.VISIBLE);
            }
            toggleSection(content, indicator);
        });
    }

    private void toggleSection(final LinearLayout content, final ImageView indicator) {
        if (content.getVisibility() == View.GONE) {
            content.setVisibility(View.VISIBLE);
            rotateIndicator(indicator, 0f, 180f);
        } else {
            content.setVisibility(View.GONE);
            rotateIndicator(indicator, 180f, 0f);
        }
    }

    private void rotateIndicator(ImageView indicator, float fromDegrees, float toDegrees) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        indicator.startAnimation(rotate);
    }
}