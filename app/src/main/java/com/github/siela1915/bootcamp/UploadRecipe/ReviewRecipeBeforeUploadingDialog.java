package com.github.siela1915.bootcamp.UploadRecipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.github.siela1915.bootcamp.R;

import java.io.IOException;

public class ReviewRecipeBeforeUploadingDialog extends DialogFragment
{
    private OnMyDialogResult mDialogResult;

    public ReviewRecipeBeforeUploadingDialog() {}

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View reviewDialogView = inflater.inflate(R.layout.dialog_review_recipe, null);
        TextView recipeName = reviewDialogView.findViewById(R.id.reviewRecipeName);
        TextView cookTime = reviewDialogView.findViewById(R.id.reviewRecipeCookTime);
        TextView prepTime = reviewDialogView.findViewById(R.id.reviewRecipePrepTime);
        TextView servings = reviewDialogView.findViewById(R.id.reviewRecipeServings);
        TextView utensils = reviewDialogView.findViewById(R.id.reviewRecipeUtensils);
        TextView ingredientList = reviewDialogView.findViewById(R.id.reviewRecipeIngredients);
        TextView cuisineTypes = reviewDialogView.findViewById(R.id.reviewRecipeCuisineTypes);
        TextView allergyTypes = reviewDialogView.findViewById(R.id.reviewRecipeAllergyTypes);
        TextView dietTypes = reviewDialogView.findViewById(R.id.reviewRecipeDietTypes);
        TextView steps = reviewDialogView.findViewById(R.id.reviewRecipeSteps);
        ImageView image = reviewDialogView.findViewById(R.id.reviewRecipeImage);

        // display image if provided
        Bitmap bitmap = null;
        try {
            if (getArguments().getString("image") != null) {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(getArguments().getString("image")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        image.setImageBitmap(bitmap);

        recipeName.setText(getArguments().getString("recipeName"));
        cookTime.setText(getArguments().getString("cookTime"));
        prepTime.setText(getArguments().getString("prepTime"));
        servings.setText(getArguments().getString("servings"));
        utensils.setText(getArguments().getString("utensils"));
        ingredientList.setText(getArguments().getString("ingredientList"));
        cuisineTypes.setText(getArguments().getString("cuisineTypes"));
        allergyTypes.setText(getArguments().getString("allergyTypes"));
        dietTypes.setText(getArguments().getString("dietTypes"));
        steps.setText(getArguments().getString("steps"));

        // Inflate and set the layout for the dialog
        builder.setView(reviewDialogView)
                .setPositiveButton(R.string.confirm_uploading, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mDialogResult.finish();
                    }
                })
                .setNegativeButton(R.string.cancel_uploading, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ReviewRecipeBeforeUploadingDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
