package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {



    public class Fraction {

        private int numerator, denominator, whole;

        public Fraction(double decimal) {

            whole = (int) decimal;
            decimal = decimal - whole;
            decimal = Math.floor(decimal * 100) / 100;
            String stringNumber = String.valueOf(decimal);
            int numberDigitsDecimals = stringNumber.length() - 1 - stringNumber.indexOf('.');
            int denominator = 1;
            for (int i = 0; i < numberDigitsDecimals; i++) {
                decimal *= 10;
                denominator *= 10;
            }

            int numerator = (int) Math.round(decimal);
            int greatestCommonFactor = greatestCommonFactor(numerator, denominator);
            this.numerator = numerator / greatestCommonFactor;
            this.denominator = denominator / greatestCommonFactor;
        }

        public String toString() {
            if (whole == 0)
                return String.valueOf(numerator) + "/" + String.valueOf(denominator);
            else
                return String.valueOf(whole) + " " + String.valueOf(numerator) + "/" + String.valueOf(denominator);
        }

        public int greatestCommonFactor(int num, int denom) {
            if (denom == 0) {
                return num;
            }
            return greatestCommonFactor(denom, num % denom);
        }
    }

    public Ingredient(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public Ingredient() {
        this.unit = "";
    }

    public String toString() {
        String amount;
        if (Double.valueOf(this.amount) %1 !=0)
            amount = new Fraction(Double.valueOf(this.amount)).toString();
        else
            amount = String.valueOf(Math.round(Double.valueOf(this.amount)));
        if (amount.equals("333/1000"))
            amount = "1/3";
        else if (amount.equals("666/1000"))
            amount = "2/3";
        return "\u2022 " + amount + " " + this.unit + " " + this.name + "\n\n";
    }

    //ingredient id (may be useful for something but for now not used)
    public int id;

    //ingredient name
    public String name;

    //ingredient category
    public String aisle;

    //quantity needed for a recipe OR how much you need to buy in shoppinglist
    public double amount;

    //the unit the item is measured in (ie tablespoon)
    public String unit;

    //the unit the item is measured in plural (ie tablespoons)
    public String unitLong;

    //the unit the item is measured in abbreviated (ie tbs)
    public String unitShort;

    //image URL of the ingredient
    public String image;

    public String originalString;

    //any other information that is needed for this ingredient
    public String[] metaInformation;

    public boolean isChecked = false;

}