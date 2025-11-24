package com.example.borealis_mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.borealis_mobile.model.Character;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private final List<Character> characters;
    private final OnCharacterClickListener clickListener;
    private final OnCharacterDeleteListener deleteListener;

    public interface OnCharacterClickListener {
        void onCharacterClick(Character character);
    }
    public interface OnCharacterDeleteListener {
        void onCharacterDelete(Character character);
    }
    public CharacterAdapter(List<Character> characters, OnCharacterClickListener clickListener, OnCharacterDeleteListener deleteListener) {
        this.characters = characters;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_character_card, parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characters.get(position);

        holder.charCardName.setText(character.getCharName());
        holder.charCardRace.setText(character.getRace());
        holder.charCardClass.setText(character.getClassTypeId());
        holder.charCardLevel.setText(character.getLevel());

        holder.itemView.setOnClickListener(v -> clickListener.onCharacterClick(character));
        holder.charCardDel.setOnClickListener(v -> deleteListener.onCharacterDelete(character));
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    public void updateCharacters(List<Character> newCharacters) {
        characters.clear();
        characters.addAll(newCharacters);
        notifyDataSetChanged();
    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {
        private final TextView charCardName, charCardRace, charCardClass, charCardLevel;
        private final ImageButton charCardDel;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);
            charCardName = itemView.findViewById(R.id.charCardName);
            charCardRace = itemView.findViewById(R.id.charCardRace);
            charCardClass = itemView.findViewById(R.id.charCardClass);
            charCardLevel = itemView.findViewById(R.id.charCardLevel);
            charCardDel = itemView.findViewById(R.id.charCardDel);
        }
    }
}
