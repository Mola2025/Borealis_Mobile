package com.example.borealis_mobile.core.engine;

import com.example.borealis_mobile.model.BaseElement;
import com.example.borealis_mobile.model.Class;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterSheetEngine {
    public static int calculateModifier(int score) {
        return (int) Math.floor((score - 10) / 2.0);
    }

    public static String formatStatDisplay(String scoreText) {
        try {
            int score = Integer.parseInt(scoreText.trim());
            int mod = calculateModifier(score);
            String sign = (mod >= 0) ? "+" : "";
            return String.format(Locale.US, "%d (%s%d)", score, sign, mod);
        } catch (NumberFormatException e) {
            return "10 (+0)";
        }
    }

    public static String calculateBasicAC(String dexText) {
        try {
            int dexScore = Integer.parseInt(dexText.trim());
            int dexMod = calculateModifier(dexScore);
            return String.valueOf(10 + dexMod);
        } catch (NumberFormatException e) {
            return "10";
        }
    }

    public static String calculateMaxHP(String className, String conText, String levelText, Map<String, BaseElement> catalogue) {
        int conMod = 0;
        try {
            int conScore = Integer.parseInt(conText.trim());
            conMod = calculateModifier(conScore);
        } catch (NumberFormatException ignored) {
        }

        int hitDieMax = 8;

        if (className != null && !className.isEmpty() && catalogue != null) {
            for (BaseElement element : catalogue.values()) {
                if ("Class".equals(element.getType()) && element.getName().equalsIgnoreCase(className.trim())) {
                    Class c = (Class) element;

                    String hdString = c.getHitDice();
                    if (hdString != null) {
                        hitDieMax = parseHitDie(hdString);
                    }
                    break;
                }
            }
        }
        int hp = (hitDieMax + conMod) + (mean(new double[]{(double) hitDieMax}) + conMod) * (Integer.parseInt(levelText.trim()) - 1);
        if (hp < 1) hp = 1;
        return String.valueOf(hp);
    }
    private static int parseHitDie(String hitDiceString) {
        try {
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(hitDiceString);

            int maxVal = 8;
            while(m.find()) {
                int val = Integer.parseInt(m.group());
                if (val > maxVal) maxVal = val;
            }
            return maxVal;
        } catch (Exception e) {
            return 8;
        }
    }
    public static int mean(double[] m) {
        double sum = 0;
        for (double v : m) {
            sum += v;
        }
        return Math.toIntExact(Math.round(sum / m.length));
    }
}
