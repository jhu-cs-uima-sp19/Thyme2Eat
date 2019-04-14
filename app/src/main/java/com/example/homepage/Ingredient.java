package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {



    public class Fraction {

        private int numerator, denominator;

        public Fraction(double decimal) {

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
            return String.valueOf(numerator) + "/" + String.valueOf(denominator);
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

    }

    public String toString() {
        String amount;
        if (Double.valueOf(this.amount) %1 !=0)
            amount = new Fraction(Double.valueOf(this.amount)).toString();
        else
            amount = String.valueOf(Math.round(Double.valueOf(this.amount)));
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

}