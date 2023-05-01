package com.github.siela1915.dishdelish.Recipes;

import com.github.siela1915.bootcamp.AutocompleteApi.UploadCallback;

public class EmptyUploadCallback implements UploadCallback {
    public String errorMessage;
    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(String err) {
        errorMessage = err;
        System.out.println(err);
    }
}
