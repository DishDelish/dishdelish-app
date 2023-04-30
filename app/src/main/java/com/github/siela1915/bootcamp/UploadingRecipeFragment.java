package com.github.siela1915.bootcamp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UploadingRecipeFragment extends Fragment {
    private final String storagePath = "gs://dish-delish-recipes.appspot.com";
    private final String[] timeUnits = new String[]{"mins", "hours", "days"};
    private static final int CHOOSE_IMAGE_FROM_GALLERY_REQUEST = 111;
    private static final int TAKE_PHOTO_REQUEST = 222;
    private View view;
    private ImageView imgView;
    private LinearLayout stepListLinearLayout, ingredientLinearLayout;
    private Uri filePath;
    private ProgressDialog pd;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReferenceFromUrl(storagePath);
    private final Database database = new Database(firebaseDatabase);
    private String userId;
    CuisineType[] cuisineTypeValues;
    AllergyType[] allergyTypeValues;
    DietType[] dietTypeValues;
    private boolean isRecipeNameValid = false;
    private boolean isCookTimeValid = false;
    private boolean isPrepTimeValid = false;
    private boolean isServingsValid = false;
    private final ActivityResultLauncher<String> requireAccessToCamera = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    takeAPhoto();
                } else {
                    // if permission is not granted
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Require Access to Camera", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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

        if (!isUserLoggedIn()) {
            showLoginAlert();
            return view;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        String[] cuisineTypes = Stream.of(cuisineTypeValues).map(CuisineType::toString).toArray(String[]::new);
        String[] allergyTypes = Stream.of(allergyTypeValues).map(AllergyType::toString).toArray(String[]::new);
        String[] dietTypes = Stream.of(dietTypeValues).map(DietType::toString).toArray(String[]::new);
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
        IngredientAutocomplete apiService = new IngredientAutocomplete();
        AutoCompleteTextView ingredientAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.ingredientAutoComplete);
        //map of Ingredient IDs, will be used when uploading a recipe to get nutritional values
        Map<String, Integer> idMap = new HashMap<>();
        setupIngredientAutocomplete(ingredientAutoComplete, idMap, apiService);


        dietTypesAutoComplete.setThreshold(1); //will start working from first character
        dietTypesAutoComplete.setAdapter(dietTypesAdapter);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading....");

        addListeners(view);
        addIngredient.setOnClickListener(v -> addIngredient(idMap, apiService));

        return view;
    }

    private void addListeners(View view) {
        Button uploadImg = (Button) view.findViewById(R.id.recipeUploadButton);
        imgView = (ImageView) view.findViewById(R.id.recipeImageContent);
        Button addIngredient = (Button) view.findViewById(R.id.addIngredientButton);
        Button addStep = (Button) view.findViewById(R.id.addStepButton);
        TextInputLayout recipeNameLayout = view.findViewById(R.id.recipeNameContent);
        EditText recipeName = recipeNameLayout.getEditText();
        TextInputLayout cookTimeLayout = view.findViewById(R.id.cookTimeContent);
        EditText cookTime = cookTimeLayout.getEditText();
        TextInputLayout prepTimeLayout = view.findViewById(R.id.prepTimeContent);
        EditText prepTime = prepTimeLayout.getEditText();
        TextInputLayout servingsLayout = view.findViewById(R.id.servingsContent);
        EditText servings = servingsLayout.getEditText();
        TextInputLayout ingredientsAmountLayout = view.findViewById(R.id.ingredientsAmount);
        EditText ingredientsAmount = ingredientsAmountLayout.getEditText();
        TextInputLayout ingredientsUnitLayout = view.findViewById(R.id.ingredientsUnit);
        EditText ingredientsUnit = ingredientsUnitLayout.getEditText();
        TextInputLayout ingredientsNameLayout = view.findViewById(R.id.ingredientsName);
        EditText ingredientsName = ingredientsNameLayout.getEditText();
        TextInputLayout stepContentLayout = view.findViewById(R.id.stepContent);
        EditText stepContent = stepContentLayout.getEditText();
        Button addCuisineType = (Button) view.findViewById(R.id.addCuisineTypeButton);
        Button addAllergyType = (Button) view.findViewById(R.id.addAllergyTypeButton);
        Button addDietType = (Button) view.findViewById(R.id.addDietTypeButton);

        imgView.setOnClickListener(v -> {
            chooseImage();
        });

        uploadImg.setOnClickListener(v -> {
            if (isInputValid()) openRecipeReviewDialog();
            else
                Toast.makeText(getActivity(), "Please fill required fields before uploading", Toast.LENGTH_LONG).show();
        });

        addStep.setOnClickListener(v -> addStep());

        recipeName.addTextChangedListener(new TextValidator(recipeName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Recipe name is required!");
                isRecipeNameValid = isTextValid(text);
            }
        });

        cookTime.addTextChangedListener(new TextValidator(cookTime) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Cooking time is required!");
                else if (!isNumberPositive(text))
                    textView.setError("Cooking time must be positive!");
                isCookTimeValid = isTextValid(text) && isNumberPositive(text);
            }
        });

        prepTime.addTextChangedListener(new TextValidator(prepTime) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Preparation time is required!");
                else if (!isNumberPositive(text))
                    textView.setError("Preparation time must be positive!");
                isPrepTimeValid = isTextValid(text) && isNumberPositive(text);
            }
        });

        servings.addTextChangedListener(new TextValidator(servings) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Number of serving is required!");
                else if (!isNumberPositive(text))
                    textView.setError("Number of serving must be positive!");
                isServingsValid = isTextValid(text) && isNumberPositive(text);
            }
        });

        addIngredientValidators(ingredientsAmount, ingredientsUnit, ingredientsName);
        addStepValidators(stepContent);

        addCuisineType.setOnClickListener(v -> addTypeListener(view, R.id.cuisineTypesAutoComplete, R.id.cuisineTypeGroup));

        addAllergyType.setOnClickListener(v -> addTypeListener(view, R.id.allergyTypesAutoComplete, R.id.allergyTypeGroup));

        addDietType.setOnClickListener(v -> addTypeListener(view, R.id.dietTypesAutoComplete, R.id.dietTypeGroup));
    }

    private void addTypeListener (View view, int autoCompleteTextViewId, int linearLayoutId) {
        AutoCompleteTextView autoCompleteTextView = view.findViewById(autoCompleteTextViewId);
        String text = autoCompleteTextView.getText().toString();
        autoCompleteTextView.getText().clear();
        if (isTextValid(text)) {
            LinearLayout linearLayout = view.findViewById(linearLayoutId);
            addType(linearLayout, text);
        }
    }

    private boolean isTextValid(String text) {
        return text != null && !text.isEmpty();
    }

    private boolean isNumberPositive(String text) {
        boolean isValid = false;
        try {
            isValid = Integer.parseInt(text) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
        return isValid;
    }

    private void addStepValidators(EditText stepContent) {
        stepContent.addTextChangedListener(new TextValidator(stepContent) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Step is required!");
            }
        });
    }

    private void addIngredientValidators(EditText ingredientsAmount, EditText ingredientsUnit, EditText ingredientsName) {
        ingredientsAmount.addTextChangedListener(new TextValidator(ingredientsAmount) {
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Ingredient amount is required!");
                else if (!isNumberPositive(text))
                    textView.setError("Ingredient amount must be positive!");
            }
        });

        ingredientsUnit.addTextChangedListener(new TextValidator(ingredientsUnit) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Ingredient unit is required!");
            }
        });

        ingredientsName.addTextChangedListener(new TextValidator(ingredientsName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError("Ingredient name is required!");
            }
        });
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
                //couldn't figure out a way to test an api inside UI classes
//                if(BuildConfig.DEBUG){
//                    ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<String>(ingredientAutoComplete.getContext(), android.R.layout.select_dialog_item, Arrays.asList("apple"));
//                    ingredientAutoComplete.setAdapter(ingredientAdapter);
//                    ingredientAutoComplete.showDropDown();
//                    return;
//                }
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

    // Upload the recipe to Firebase
    private void uploadRecipe(final Uri recipeImageUri) {
        pd.show();

        OnSuccessListener<String> onUploadRecipeToDatabaseSuccessListener = s -> {
            pd.dismiss();
            Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_LONG).show();

            // close this fragment and return to the previous page
            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), new HomePageFragment()).commit();
        };

        OnFailureListener onUploadRecipeToDatabaseFailureListener = e -> {
            pd.dismiss();
            // if uploading failed
            Toast.makeText(getActivity(), "Error Uploading ", Toast.LENGTH_LONG).show();
        };

        // If user has chosen a picture to upload
        if (recipeImageUri != null) {
            uploadImageToDatabase(recipeImageUri, taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                    .addOnSuccessListener(uri -> uploadRecipeToDatabase(uri, onUploadRecipeToDatabaseSuccessListener, onUploadRecipeToDatabaseFailureListener))
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Error Fetching Image", Toast.LENGTH_LONG).show();
                    }), e -> {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Error Uploading Image", Toast.LENGTH_LONG).show();
                    });
        } else {
            // When no picture is uploaded by the user
            uploadRecipeToDatabase(null, onUploadRecipeToDatabaseSuccessListener, onUploadRecipeToDatabaseFailureListener);
        }
    }

    private void uploadImageToDatabase(Uri recipeImageUri, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        // We are taking the filepath as storagePath + firebaseUser.getUid()+".png"
        String recipeImageStoragePath = "recipes_image/";
        String filePathName = recipeImageStoragePath + "_" + userId + ".png";
        storageRef.child(filePathName).putFile(recipeImageUri).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    private void uploadRecipeToDatabase(Uri downloadUri, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        database.setAsync(getRecipe(downloadUri))
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    private boolean isInputValid() {
        return isRecipeNameValid && isCookTimeValid && isPrepTimeValid && isServingsValid && isIngredientValid() && isStepValid();
    }

    private boolean isStepValid() {
        for (int i = 0; i < stepListLinearLayout.getChildCount(); i++) {
            if (stepListLinearLayout.getChildAt(i) instanceof ConstraintLayout) {
                ConstraintLayout step = (ConstraintLayout) stepListLinearLayout.getChildAt(i);
                if (step.getChildAt(0) instanceof TextInputLayout) {
                    if (((TextInputLayout) step.getChildAt(0)).getEditText() == null || !isTextValid((((TextInputLayout) step.getChildAt(0)).getEditText().getText().toString())))
                        return false;
                }
            }
        }
        return true;
    }

    private boolean isIngredientValid() {
        for (int i = 0; i < ingredientLinearLayout.getChildCount(); i++) {
            if (ingredientLinearLayout.getChildAt(i) instanceof ConstraintLayout) {
                ConstraintLayout step = (ConstraintLayout) ingredientLinearLayout.getChildAt(i);
                if (step.getChildAt(0) instanceof TextInputLayout && step.getChildAt(1) instanceof TextInputLayout && step.getChildAt(2) instanceof TextInputLayout) {
                    if (((TextInputLayout) step.getChildAt(2)).getEditText() == null
                            || ((TextInputLayout) step.getChildAt(1)).getEditText() == null
                            || ((TextInputLayout) step.getChildAt(1)).getEditText() == null)
                        return false;
                    String ingredientName = ((TextInputLayout) step.getChildAt(2)).getEditText().getText().toString();
                    String ingredientUnit = ((TextInputLayout) step.getChildAt(1)).getEditText().getText().toString();
                    String ingredientAmount = ((TextInputLayout) step.getChildAt(0)).getEditText().getText().toString();
                    if (!isTextValid(ingredientAmount) || !isTextValid(ingredientName) || !isTextValid(ingredientUnit) || !isNumberPositive(ingredientAmount))
                        return false;
                }
            }
        }
        return true;
    }

    private Recipe getRecipe(Uri localLocation) {
        TextInputLayout recipeName = view.findViewById(R.id.recipeNameContent);
        TextInputLayout cookTime = view.findViewById(R.id.cookTimeContent);
        TextInputLayout prepTime = view.findViewById(R.id.prepTimeContent);
        TextInputLayout servings = view.findViewById(R.id.servingsContent);
        TextInputLayout utensils = view.findViewById(R.id.utensilsContent);
        LinearLayout cuisineTypeGroup = view.findViewById(R.id.cuisineTypeGroup);
        LinearLayout allergyTypeGroup = view.findViewById(R.id.allergyTypeGroup);
        LinearLayout dietTypeGroup = view.findViewById(R.id.dietTypeGroup);

        Recipe recipe = new Recipe();
        recipe.setRecipeName(recipeName.getEditText().getText().toString());
        if (localLocation != null) recipe.setImage(localLocation.toString());
        else recipe.setImage(null);
        recipe.setCookTime(Integer.parseInt(cookTime.getEditText().getText().toString()));
        recipe.setPrepTime(Integer.parseInt(prepTime.getEditText().getText().toString()));
        recipe.setServings(Integer.parseInt(servings.getEditText().getText().toString()));
        recipe.setUtensils(new Utensils(Arrays.asList(utensils.getEditText().getText().toString())));
        recipe.setIngredientList(getIngredients());
        recipe.setCuisineTypes(getTypes(cuisineTypeGroup, cuisineTypeValues));
        recipe.setAllergyTypes(getTypes(allergyTypeGroup, allergyTypeValues));
        recipe.setDietTypes(getTypes(dietTypeGroup, dietTypeValues));
        recipe.setSteps(getSteps());

        return recipe;
    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage(){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // set the items in builder
        builder.setItems(optionsMenu, (dialogInterface, i) -> {
            if(optionsMenu[i].equals("Take Photo")){
                if (isCameraAccessible()) {
                    takeAPhoto();
                } else {
                    requireAccessToCamera.launch(android.Manifest.permission.CAMERA);
                }
            }
            else if(optionsMenu[i].equals("Choose from Gallery")){
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , CHOOSE_IMAGE_FROM_GALLERY_REQUEST);
            }
            else if (optionsMenu[i].equals("Exit")) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    // function to check permission
    public boolean isCameraAccessible() {
        int cameraPermission = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void takeAPhoto() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, TAKE_PHOTO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case CHOOSE_IMAGE_FROM_GALLERY_REQUEST:
                    getAndSetImageFromGallery(data);
                    break;
                case TAKE_PHOTO_REQUEST:
                    getAndSetImageFromTakingPhoto(data);
                    break;
            }
        }
    }

    private void getAndSetImageFromGallery(Intent data) {
        if (data.getData() != null) {
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

    private void getAndSetImageFromTakingPhoto(Intent data) {
        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
        imgView.setImageBitmap(selectedImage);
        filePath = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), selectedImage, null, null));
    }

    private void addIngredient(Map<String, Integer> idMap, IngredientAutocomplete apiService) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final View ingredient = getLayoutInflater().inflate(R.layout.recipe_ingredient_edittext, null, false);
            ImageView removeIngredient = (ImageView) ingredient.findViewById(R.id.removeIngredient);
            TextInputLayout ingredientsAmountLayout = ingredient.findViewById(R.id.ingredientsAmount);
            EditText ingredientsAmount = ingredientsAmountLayout.getEditText();
            TextInputLayout ingredientsUnitLayout = ingredient.findViewById(R.id.ingredientsUnit);
            EditText ingredientsUnit = ingredientsUnitLayout.getEditText();
            TextInputLayout ingredientsNameLayout = ingredient.findViewById(R.id.ingredientsName);
            EditText ingredientsName = ingredientsNameLayout.getEditText();

            addIngredientValidators(ingredientsAmount, ingredientsUnit, ingredientsName);

            removeIngredient.setOnClickListener(v -> removeIngredient(ingredient));

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
            EditText stepContentEditText = stepContent.getEditText();

            stepContent.setHint("Step " + String.valueOf(stepListLinearLayout.getChildCount()));
            addStepValidators(stepContentEditText);

            removeStep.setOnClickListener(v -> removeStep(step));

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
            uploadRecipe(filePath);
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
        Utensils utensils = recipe.getUtensils();
        String utensilsString = "";
        // as utensils are not mandatory to have, a protector condition is needed
        if (utensils != null) utensilsString = truncateString(utensils.toString());
        bundle.putString("utensils", utensilsString);
        bundle.putString("ingredientList", truncateString(recipe.getIngredientList().toString()));
        bundle.putString("cuisineTypes", truncateString(Arrays.toString(recipe.getCuisineTypes().stream().map(cuisineType -> cuisineTypeValues[cuisineType].toString()).toArray(String[]::new))));
        bundle.putString("allergyTypes", truncateString(Arrays.toString(recipe.getAllergyTypes().stream().map(allergyType -> allergyTypeValues[allergyType].toString()).toArray(String[]::new))));
        bundle.putString("dietTypes", truncateString(Arrays.toString(recipe.getDietTypes().stream().map(dietType -> dietTypeValues[dietType].toString()).toArray(String[]::new))));
        bundle.putString("steps", truncateString(recipe.getSteps().toString()));

        return bundle;
    }

    private String uriToString(Uri uri) {
        if (uri != null) return uri.toString();
        return null;
    }

    // Truncate a string by deleting its first and last char
    private String truncateString(String str) {
        return str.substring(1, str.length() - 1);
    }

    private void addType(LinearLayout typeLayout, String type) {
        final View typeView = getLayoutInflater().inflate(R.layout.recipe_type_item, null, false);
        ImageView removeType = (ImageView) typeView.findViewById(R.id.removeType);
        TextView typeContent = (TextView) typeView.findViewById(R.id.typeContent);

        typeContent.setText(type);

        removeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeType(typeLayout, typeView);
            }
        });

        typeLayout.addView(typeView);
    }

    private List<Integer> getTypes(LinearLayout typeLayout, Object[] typeValues) {
        ArrayList<Integer> types = new ArrayList<Integer>();
        for (int i = 0; i < typeLayout.getChildCount(); i++) {
            if (typeLayout.getChildAt(i) instanceof ConstraintLayout) {
                ConstraintLayout type = (ConstraintLayout) typeLayout.getChildAt(i);
                if (type.getChildAt(0) instanceof TextView) {
                    String text = ((TextView) type.getChildAt(0)).getText().toString();
                    types.add(IntStream.range(0, typeValues.length)
                            .filter(j -> text.equals(typeValues[j].toString()))
                            .findFirst()
                            .orElse(-1));
                }
            }
        }
        return types;
    }

    private void removeType(LinearLayout typeLayout, View type) {
        typeLayout.removeView(type);
    }

    private boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void showLoginAlert() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Login Required")
                .setMessage("Please login before uploading a recipe :)")
                .setPositiveButton(android.R.string.ok, null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.replace(R.id.fragContainer, ProfileFragment.class, null);
                fragmentTransaction.commit();
                dialog.dismiss();
            });
        });
        dialog.show();
    }
}