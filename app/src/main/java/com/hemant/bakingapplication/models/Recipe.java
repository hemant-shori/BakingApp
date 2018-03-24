package com.hemant.bakingapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {
    private String name;
    private String serving;
    private String ingredients;
    private String steps;
    private String recipePosterURL;
    private byte[] recipePoster;

    public Recipe() {

    }

    private Recipe(Parcel in) {
        name = in.readString();
        serving = in.readString();
        ingredients = in.readString();
        steps = in.readString();
        recipePosterURL = in.readString();

//        Alternative solution to read a byte[]
        byte[] _byte = new byte[in.readInt()];
        in.readByteArray(_byte);
        setRecipePoster(_byte);
        //recipePoster = in.createByteArray();
    }

    @SuppressWarnings("unused")
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }

    public byte[] getRecipePoster() {
        return recipePoster;
    }

    private void setRecipePoster(byte[] recipePoster) {
        this.recipePoster = recipePoster;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(serving);
        dest.writeString(ingredients);
        dest.writeString(steps);
        dest.writeString(recipePosterURL);
//        Alternative solution to read a byte[]
        if (getRecipePoster() == null) {
            dest.writeInt(0);
            dest.writeByteArray(new byte[]{});
        } else {
            dest.writeInt(getRecipePoster().length);
            dest.writeByteArray(getRecipePoster());
        }
    }

    public String getRecipePosterURL() {
        return recipePosterURL;
    }

    public void setRecipePosterURL(String recipePosterURL) {
        this.recipePosterURL = recipePosterURL;
    }
}
