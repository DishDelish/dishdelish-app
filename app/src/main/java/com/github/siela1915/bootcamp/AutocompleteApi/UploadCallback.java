package com.github.siela1915.bootcamp.AutocompleteApi;

/**
 * Callback interface used to upload the recipe when
 */
public interface UploadCallback {
    void onSuccess();
    void onError(String err);
}
