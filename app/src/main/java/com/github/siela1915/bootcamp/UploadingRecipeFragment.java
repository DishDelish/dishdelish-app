package com.github.siela1915.bootcamp;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.siela1915.bootcamp.AutocompleteApi.ApiResponse;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.AutocompleteApi.UploadCallback;
import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.github.siela1915.bootcamp.Recipes.Utensils;
import com.github.siela1915.bootcamp.firebase.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class UploadingRecipeFragment extends Fragment {
    private final String storagePath = "gs://dish-delish-recipes.appspot.com";
    private final String[] timeUnits = new String[]{"mins", "hours", "days"};
    private final int PICK_IMAGE_REQUEST = 111;
    private View view;
    private ImageView imgView;
    private LinearLayout stepListLinearLayout, ingredientLinearLayout;
    private Uri filePath;
    private ProgressDialog pd;
    private IngredientAutocomplete apiService = new IngredientAutocomplete();
    //map of Ingredient IDs, will be used when uploading a recipe to get nutritional values
    Map<String, Integer> idMap = new HashMap<>();

    //creating reference to firebase storage
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReferenceFromUrl(storagePath);
    private final Database database = new Database(firebaseDatabase);
    // the below line will keep commented until some form of auth guard is implemented
    // i.e. user is logged in before accessing this page
    // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String userId = "1234";

    CuisineType[] cuisineTypeValues;
    AllergyType[] allergyTypeValues;
    DietType[] dietTypeValues;

    public UploadingRecipeFragment() {
        // Required empty public constructor
    }

    public static UploadingRecipeFragment newInstance() {
        return new UploadingRecipeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upload_recipes, container, false);

        // get view elements
        Button uploadImg = (Button) view.findViewById(R.id.recipeUploadButton);
        imgView = (ImageView) view.findViewById(R.id.recipeImageContent);
        Button addIngredient = (Button) view.findViewById(R.id.addIngredientButton);
        Button addStep = (Button) view.findViewById(R.id.addStepButton);
        stepListLinearLayout = (LinearLayout) view.findViewById(R.id.stepGroup);
        ingredientLinearLayout = (LinearLayout) view.findViewById(R.id.ingredientsGroup);
        AutoCompleteTextView prepTimeUnitAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.prepTimeUnitAutoComplete);
        AutoCompleteTextView cookTimeUnitAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.cookTimeUnitAutoComplete);

        // set up dropdown content for the unit of prepTime and cookTime
        ArrayAdapter<String> recipeTimeAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, timeUnits);
        prepTimeUnitAutoComplete.setAdapter(recipeTimeAdapter);
        prepTimeUnitAutoComplete.setText(recipeTimeAdapter.getItem(0), false); // select default time unit
        cookTimeUnitAutoComplete.setAdapter(recipeTimeAdapter);
        cookTimeUnitAutoComplete.setText(recipeTimeAdapter.getItem(0), false); // select default time unit

        // cache the values of enum types
        cuisineTypeValues = CuisineType.values();
        allergyTypeValues = AllergyType.values();
        dietTypeValues = DietType.values();

        // set up autocomplete for cuisineTypes, allergyTypes, and dietTypes
        String[] cuisineTypes = Stream.of(cuisineTypeValues).map(CuisineType::name).toArray(String[]::new);
        String[] allergyTypes = Stream.of(allergyTypeValues).map(AllergyType::name).toArray(String[]::new);
        String[] dietTypes = Stream.of(dietTypeValues).map(DietType::name).toArray(String[]::new);
        ArrayAdapter<String> cuisineTypesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, cuisineTypes);
        ArrayAdapter<String> allergyTypesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, allergyTypes);
        ArrayAdapter<String> dietTypesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, dietTypes);

        AutoCompleteTextView cuisineTypesAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.cuisineTypesAutoComplete);
        cuisineTypesAutoComplete.setThreshold(1); //will start working from first character
        cuisineTypesAutoComplete.setAdapter(cuisineTypesAdapter);
        AutoCompleteTextView allergyTypesAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.allergyTypesAutoComplete);
        allergyTypesAutoComplete.setThreshold(1); //will start working from first character
        allergyTypesAutoComplete.setAdapter(allergyTypesAdapter);
        AutoCompleteTextView dietTypesAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.dietTypesAutoComplete);



        //ingredient autocompletion
        //IngredientAutocomplete apiService = new IngredientAutocomplete();
        AutoCompleteTextView ingredientAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.ingredientAutoComplete);
        setupIngredientAutocomplete(ingredientAutoComplete, idMap, apiService);





        dietTypesAutoComplete.setThreshold(1); //will start working from first character
        dietTypesAutoComplete.setAdapter(dietTypesAdapter);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading....");

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecipeReviewDialog();
            }
        });

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient(idMap, apiService);
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


    public void setupIngredientAutocomplete(AutoCompleteTextView ingredientAutoComplete, Map<String, Integer> idMap, IngredientAutocomplete apiService){
        ingredientAutoComplete.setThreshold(1);
        ingredientAutoComplete.addTextChangedListener(new TextWatcher() {
            String prevString;
            boolean isTyping = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            private Timer timer = new Timer();
            private final long DELAY = 1000; // milliseconds
            @Override
            public void afterTextChanged(final Editable s) {
                //TODO add handler for timer,
                //doesn't consider defocusing and refocusing the text field as typing
                if(!s.toString().equals(prevString)){
                    if (!isTyping) {
                        // Send notification for start typing event
                        ingredientAutoComplete.dismissDropDown();
                        isTyping = true;
                    }
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    isTyping = false;
                                    prevString = s.toString();
                                    //send notification for stopped typing event
                                    apiService.completeSearchNames(s.toString(), ingredientAutoComplete, idMap);
                                }
                            },
                            DELAY
                    );
                }
            }
        });
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

        // We are taking the filepath as storagePath + firebaseUser.getUid()+".png"
        String recipeImageStoragePath = "recipes_image/";
        String filePathName = recipeImageStoragePath + "_" + userId + ".png";
        storageRef.child(filePathName).putFile(recipeImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;

                // get the url of the recipe image using uritask
                final Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {
                    try {
                        database.set(getRecipe(downloadUri));

                        pd.dismiss();
                        Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_LONG).show();

                        // close this fragment and return to the previous page
                        requireActivity().getSupportFragmentManager().beginTransaction().remove(UploadingRecipeFragment.this).commit();
                    } catch (ExecutionException | InterruptedException e) {
                        // if uploading failed
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Error Uploading ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Error Fetching Image", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Error Uploading Image", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Recipe getRecipe(Uri downloadUri) {
        TextInputLayout recipeName = view.findViewById(R.id.recipeNameContent);
        TextInputLayout cookTime = view.findViewById(R.id.cookTimeContent);
        TextInputLayout prepTime = view.findViewById(R.id.prepTimeContent);
        TextInputLayout servings = view.findViewById(R.id.servingsContent);
        TextInputLayout utensils = view.findViewById(R.id.utensilsContent);

        Recipe recipe = new Recipe();
        recipe.setRecipeName(recipeName.getEditText().getText().toString());
        if (downloadUri != null) recipe.setImage(downloadUri.toString());
        else recipe.setImage(null);
        recipe.setCookTime(Integer.parseInt(cookTime.getEditText().getText().toString()));
        recipe.setPrepTime(Integer.parseInt(prepTime.getEditText().getText().toString()));
        recipe.setServings(Integer.parseInt(servings.getEditText().getText().toString()));
        recipe.setUtensils(new Utensils(Arrays.asList(utensils.getEditText().getText().toString())));
        recipe.setIngredientList(getIngredients());
        recipe.setCuisineTypes(getCuisineTypes());
        recipe.setAllergyTypes(getAllergyTypes());
        recipe.setDietTypes(getDietTypes());
        recipe.setSteps(getSteps());

        return recipe;
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void addIngredient(Map<String, Integer> idMap, IngredientAutocomplete apiService) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final View ingredient = getLayoutInflater().inflate(R.layout.recipe_ingredient_edittext, null, false);
            ImageView removeIngredient = (ImageView) ingredient.findViewById(R.id.removeIngredient);

            removeIngredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeIngredient(ingredient);
                }
            });

            ingredientLinearLayout.addView(ingredient);
            AutoCompleteTextView ingredientAutoComplete = (AutoCompleteTextView) ingredient.findViewById(R.id.ingredientAutoComplete);
            setupIngredientAutocomplete(ingredientAutoComplete, idMap, apiService);
        }
    }

    private void removeIngredient(View ingredient) {
        ingredientLinearLayout.removeView(ingredient);
    }

    private List<Ingredient> getIngredients() {
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (int i = 0; i < ingredientLinearLayout.getChildCount(); i++) {
            if (ingredientLinearLayout.getChildAt(i) instanceof ConstraintLayout) {
                ConstraintLayout step = (ConstraintLayout) ingredientLinearLayout.getChildAt(i);
                if (step.getChildAt(0) instanceof TextInputLayout && step.getChildAt(1) instanceof TextInputLayout && step.getChildAt(2) instanceof TextInputLayout) {
                    String ingredientName = ((TextInputLayout) step.getChildAt(2)).getEditText().getText().toString();
                    String ingredientUnit = ((TextInputLayout) step.getChildAt(1)).getEditText().getText().toString();
                    int ingredientAmount = Integer.parseInt(((TextInputLayout) step.getChildAt(0)).getEditText().getText().toString());
                    ingredients.add(new Ingredient(ingredientName, new Unit(ingredientAmount, ingredientUnit)));
                }
            }
        }
        return ingredients;
    }

    private void addStep() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final View step = getLayoutInflater().inflate(R.layout.recipe_step_edittext, null, false);
            ImageView removeStep = (ImageView) step.findViewById(R.id.removeStep);
            TextInputLayout stepContent = (TextInputLayout) step.findViewById(R.id.stepContent);

            stepContent.setHint("Step " + String.valueOf(stepListLinearLayout.getChildCount()));

            removeStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeStep(step);
                }
            });

            stepListLinearLayout.addView(step);
        }
    }

    private void removeStep(View step) {
        stepListLinearLayout.removeView(step);
    }

    private List<String> getSteps() {
        ArrayList<String> steps = new ArrayList<String>();
        for (int i = 0; i < stepListLinearLayout.getChildCount(); i++) {
            if (stepListLinearLayout.getChildAt(i) instanceof ConstraintLayout) {
                ConstraintLayout step = (ConstraintLayout) stepListLinearLayout.getChildAt(i);
                if (step.getChildAt(0) instanceof TextInputLayout) {
                    steps.add(((TextInputLayout) step.getChildAt(0)).getEditText().getText().toString());
                }
            }
        }
        return steps;
    }

    private void openRecipeReviewDialog() {
        Recipe recipe = getRecipe(filePath);
        ReviewRecipeBeforeUploadingDialog reviewRecipeDialog = new ReviewRecipeBeforeUploadingDialog();
        reviewRecipeDialog.setArguments(getBundleForReview(recipe));
        reviewRecipeDialog.setDialogResult(() -> {
            pd.show();
            UploadCallback uploadRecipeCallback = new UploadCallback() {
                @Override
                public void onSuccess() {
                    uploadRecipe(filePath);
                }
                @Override
                public void onError(String err) {
                    //show a error to the user? use toast?
                }
            };
            // TODO apiService
            apiService.getNutritionFromRecipe(recipe, idMap, uploadRecipeCallback);

            //uploadRecipe(filePath);
        });
        String reviewPageTag = "review_recipe_dialog";
        reviewRecipeDialog.show(getActivity().getSupportFragmentManager(), reviewPageTag);
    }

    private Bundle getBundleForReview(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putString("recipeName", recipe.getRecipeName());
        bundle.putString("image", uriToString(filePath));
        bundle.putString("cookTime", String.valueOf(recipe.getCookTime()));
        bundle.putString("prepTime", String.valueOf(recipe.getPrepTime()));
        bundle.putString("servings", String.valueOf(recipe.getServings()));
        bundle.putString("utensils", recipe.getUtensils().toString());
        bundle.putString("ingredientList", recipe.getIngredientList().toString());
        bundle.putString("cuisineTypes", recipe.getCuisineTypes().toString());
        bundle.putString("allergyTypes", recipe.getAllergyTypes().toString());
        bundle.putString("dietTypes", recipe.getDietTypes().toString());
        bundle.putString("steps", recipe.getSteps().toString());
        return bundle;
    }

    private String uriToString(Uri uri) {
        if (uri != null) return uri.toString();
        return null;
    }

    // TODO
    private List<Integer> getCuisineTypes() {
        return Arrays.asList(0);
    }

    // TODO
    private List<Integer> getAllergyTypes() {
        return Arrays.asList(0);
    }

    // TODO
    private List<Integer> getDietTypes() {
        return Arrays.asList(0);
    }
}