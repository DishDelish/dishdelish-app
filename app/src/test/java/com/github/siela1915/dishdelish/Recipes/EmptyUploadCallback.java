package com.github.siela1915.dishdelish.Recipes;

import com.github.siela1915.bootcamp.AutocompleteApi.UploadCallback;

public class EmptyUploadCallback implements UploadCallback {
    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(String err) {
        System.out.println(err);
    }
}
