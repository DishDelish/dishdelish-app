package com.github.dishdelish;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.github.dishdelish.Recipes.Ingredient;
import com.github.dishdelish.Recipes.Recipe;
import com.github.dishdelish.Recipes.Unit;
import com.github.dishdelish.Recipes.Utensils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UploadingRecipeFragment extends Fragment {
    View view;
    Button chooseImg, uploadImg;
    ChipGroup chipGroup;
    ImageView imgView, addIngredient, addStep;
    LinearLayout stepList;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog pd;
    String storagepath = "Recipes_image/";

    List<Pair<Unit, Ingredient>> ingredientList = new ArrayList<Pair<Unit, Ingredient>>();

    //creating reference to firebase storage
    // temporarily commented out till integrated with firebase auth
//    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://dish-delish-recipes.appspot.com");
    DatabaseReference databaseReference = firebaseDatabase.getReference("Recipes");
    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload_recipes, container, false);

        chooseImg = (Button) view.findViewById(R.id.chooseImg);
        uploadImg = (Button) view.findViewById(R.id.uploadButton);
        imgView = (ImageView) view.findViewById(R.id.imgView);
        chipGroup = (ChipGroup) view.findViewById(R.id.chipGroup);
        addIngredient = (ImageView) view.findViewById(R.id.addIngredient);
        addStep = (ImageView) view.findViewById(R.id.addStep);
        stepList = (LinearLayout) view.findViewById(R.id.stepsGroup);
        EditText ingredientsAmount = (EditText) view.findViewById(R.id.ingredientsAmount);
        EditText ingredientsUnit = (EditText) view.findViewById(R.id.ingredientsUnit);
        EditText ingredientsName = (EditText) view.findViewById(R.id.ingredientsName);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading....");

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    pd.show();

                    uploadRecipe(filePath);
                } else {
                    Toast.makeText(getActivity(), "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ingredientsAmount.getText().toString().isEmpty()
                        && !ingredientsUnit.getText().toString().isEmpty()
                        && !ingredientsName.getText().toString().isEmpty()
                ) {
                    int amount = Integer.parseInt(ingredientsAmount.getText().toString());
                    String unit = ingredientsUnit.getText().toString();
                    String name = ingredientsName.getText().toString();
                    addChip(amount + " " + unit + " " + name);
                    ingredientList.add(new Pair<>(new Unit(amount, unit), Ingredient.valueOf(name.trim().toUpperCase())));
                    ingredientsAmount.setText("");
                    ingredientsUnit.setText("");
                    ingredientsName.setText("");
                }
            }
        });

        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStep();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Upload the recipe to Firebase.
    private void uploadRecipe(final Uri recipeImageUri) {
        pd.show();

        EditText recipeName = view.findViewById(R.id.recipeName);
        EditText cookTime = view.findViewById(R.id.cookTime);
        EditText prepTime = view.findViewById(R.id.prepTime);
        EditText servings = view.findViewById(R.id.servings);
        EditText utensils = view.findViewById(R.id.utensils);

//        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = "ZHANG CHi"; // should be firebaseUser.getUid()

        // We are taking the filepath as storagepath + firebaseUser.getUid()+".png"
        String filepathname = storagepath + "_" + userId;
        StorageReference storageReference1 = storageRef.child(filepathname);
        storageReference1.putFile(recipeImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;

                // We will get the url of our image using uritask
                final Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {

                    // updating our image url into the realtime database
                    HashMap<String, Object> hashMap = new HashMap<>();

//                    Recipe recipe = new Recipe();
//                    recipe.setRecipeName(recipeName.getText().toString());
//                    recipe.setImage(downloadUri.toString());
//                    recipe.setCookTime(Integer.parseInt(cookTime.getText().toString()));
//                    recipe.setPrepTime(Integer.parseInt(prepTime.getText().toString()));
//                    recipe.setServings(Integer.parseInt(servings.getText().toString()));
//                    recipe.setUtensils(new Utensils(Arrays.asList(utensils.getText().toString())));
//                    recipe.setIngredientList(ingredientList);
//                    recipe.setSteps(getSteps());

                    // should be using Recipe object but as it is not finalized some temp hashmaps are used
                    HashMap<String, Object> recipe = new HashMap<>();
                    recipe.put("recipeName", recipeName.getText().toString());
                    recipe.put("image", downloadUri.toString());
                    recipe.put("cookTime", Integer.parseInt(cookTime.getText().toString()));
                    recipe.put("prepTime", Integer.parseInt(prepTime.getText().toString()));
                    recipe.put("servings", Integer.parseInt(servings.getText().toString()));
                    recipe.put("utensils", new Utensils(Arrays.asList(utensils.getText().toString())));
                    recipe.put("ingredientList", ingredientList);
                    recipe.put("steps", getSteps());
                    hashMap.put(recipeName.getText().toString(), recipe);

                    // should use firebaseUser.getUid()
                    databaseReference.child(userId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error Uploading ", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addChip(String text) {
        Chip chip = new Chip(getActivity());
        chip.setText(text);

        chip.setClickable(true);
        chip.setCheckable(true);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                // as chips are formed in the format of 'anount + " " + unit + " " + name"
                // so that the name of the ingredient to be removed is chip.getText().toString().split(" ")[1]
                // and it can be filtered out by its name
                ingredientList.removeIf(ingredient -> ingredient.second.toString().equals(chip.getText().toString().split(" ")[-1])
                );
            }
        });

        chipGroup.addView(chip);
    }

    private void addStep() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final View step = getLayoutInflater().inflate(R.layout.cancelable_edittext, null, false);
            ImageView removeStep = (ImageView) step.findViewById(R.id.remove);
            EditText stepContent = (EditText) step.findViewById(R.id.stepContent);

            stepContent.setHint("Step " + String.valueOf(stepList.getChildCount() + 1));

            removeStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeStep(step);
                }
            });

            stepList.addView(step);
        }
    }

    private void removeStep(View step) {
        stepList.removeView(step);
    }

    private List<String> getSteps() {
        ArrayList<String> steps = new ArrayList<String>();
        for (int i = 0; i < stepList.getChildCount(); i++) {
            if (stepList.getChildAt(i) instanceof LinearLayoutCompat) {
                LinearLayoutCompat ll = (LinearLayoutCompat) stepList.getChildAt(i);
                for (int j = 0; j < ll.getChildCount(); j++) {
                    if (ll.getChildAt(j) instanceof EditText) {
                        steps.add(((EditText) ll.getChildAt(j)).getText().toString());
                    }
                }
            }
        }
        return steps;
    }

}
