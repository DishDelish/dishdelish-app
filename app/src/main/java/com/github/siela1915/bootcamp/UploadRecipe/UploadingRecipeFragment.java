package com.github.siela1915.bootcamp.UploadRecipe;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.github.siela1915.bootcamp.AutocompleteApi.UploadCallback;
import com.github.siela1915.bootcamp.HomePageFragment;
import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.ProfileFragment;
import com.github.siela1915.bootcamp.R;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Recipe;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UploadingRecipeFragment extends Fragment {
    private final String storagePath = "gs://dish-delish-recipes.appspot.com";
    private final String[] timeUnits = new String[]{"mins", "hours", "days"};
    private static final int CHOOSE_IMAGE_FROM_GALLERY_REQUEST = 111;
    private static final int TAKE_PHOTO_REQUEST = 222;
    private View view;
    private ImageView imgView;
    private RecipeStepAndIngredientManager recipeStepAndIngredientManager;
    private Uri filePath;
    private ProgressDialog pd;
    private IngredientAutocomplete apiService = new IngredientAutocomplete();
    //map of Ingredient IDs, will be used when uploading a recipe to get nutritional values
    private Map<String, Integer> idMap = new HashMap<>();
    //global recipe variable so we only use one instance of a recipe
    private Recipe recipe;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReferenceFromUrl(storagePath);
    private final Database database = new Database(firebaseDatabase);
    private String userId;
    CuisineType[] cuisineTypeValues;
    AllergyType[] allergyTypeValues;
    DietType[] dietTypeValues;
    private boolean isRecipeNameValid = false;
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
        imgView = (ImageView) view.findViewById(R.id.recipeImageContent);
        Button addIngredient = (Button) view.findViewById(R.id.addIngredientButton);
        recipeStepAndIngredientManager = new RecipeStepAndIngredientManager(getActivity(), (LinearLayout) view.findViewById(R.id.stepGroup), (LinearLayout) view.findViewById(R.id.ingredientsGroup));
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
        AutoCompleteTextView ingredientAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.ingredientAutoComplete);
        recipeStepAndIngredientManager.setupIngredientAutocomplete(ingredientAutoComplete, idMap, apiService);


        dietTypesAutoComplete.setThreshold(1); //will start working from first character
        dietTypesAutoComplete.setAdapter(dietTypesAdapter);

        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.uploading_hint));

        addListeners(view);
        addIngredient.setOnClickListener(v -> recipeStepAndIngredientManager.addIngredient(idMap, apiService));

        return view;
    }

    private void addListeners(View view) {
        Button uploadImg = (Button) view.findViewById(R.id.recipeUploadButton);
        imgView = (ImageView) view.findViewById(R.id.recipeImageContent);
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
                Toast.makeText(getActivity(), R.string.fill_required_fields_popup_message, Toast.LENGTH_LONG).show();
        });

        addStep.setOnClickListener(v -> recipeStepAndIngredientManager.addStep());

        recipeName.addTextChangedListener(new AbstractTextValidator(recipeName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!TextValidator.isTextValid(text))
                    textView.setError(getString(R.string.recipeNameEmptyErrorMessage));
                isRecipeNameValid = TextValidator.isTextValid(text);
            }
        });

        setValidator(cookTime, getString(R.string.cookTimeEmptyErrorMessage), getString(R.string.cookTimeInvalidErrorMessage));
        setValidator(prepTime, getString(R.string.prepTimeEmptyErrorMessage), getString(R.string.prepTimeInvalidErrorMessage));
        setValidator(servings, getString(R.string.servingsEmptyErrorMessage), getString(R.string.servingsInvalidErrorMessage));


        recipeStepAndIngredientManager.addIngredientValidators(ingredientsAmount, ingredientsUnit, ingredientsName);
        recipeStepAndIngredientManager.addStepValidators(stepContent);

        addCuisineType.setOnClickListener(v -> addTypeListener(view, R.id.cuisineTypesAutoComplete, R.id.cuisineTypeGroup));

        addAllergyType.setOnClickListener(v -> addTypeListener(view, R.id.allergyTypesAutoComplete, R.id.allergyTypeGroup));

        addDietType.setOnClickListener(v -> addTypeListener(view, R.id.dietTypesAutoComplete, R.id.dietTypeGroup));
    }

    private void setValidator(TextView textView, String emptyErrorMessage, String invalidErrorMessage) {
        textView.addTextChangedListener(new AbstractTextValidator(textView) {
            @Override
            public void validate(TextView textView, String text) {
                if (!TextValidator.isTextValid(text)) textView.setError(emptyErrorMessage);
                else if (!TextValidator.isNumberPositive(text))
                    textView.setError(invalidErrorMessage);
            }
        });
    }


    private void addTypeListener(View view, int autoCompleteTextViewId, int linearLayoutId) {
        AutoCompleteTextView autoCompleteTextView = view.findViewById(autoCompleteTextViewId);
        String text = autoCompleteTextView.getText().toString();
        autoCompleteTextView.getText().clear();
        if (TextValidator.isTextValid(text)) {
            LinearLayout linearLayout = view.findViewById(linearLayoutId);
            addType(linearLayout, text);
        }
    }

    // Upload the recipe to Firebase
    private void uploadRecipe(final Uri recipeImageUri) {
        pd.show();

        OnSuccessListener<String> onUploadRecipeToDatabaseSuccessListener = s -> {
            pd.dismiss();
            Toast.makeText(getActivity(), R.string.upload_recipe_success_message, Toast.LENGTH_LONG).show();

            // close this fragment and return to the previous page
            requireActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), new HomePageFragment()).commit();
        };

        OnFailureListener onUploadRecipeToDatabaseFailureListener = e -> {
            pd.dismiss();
            // if uploading failed
            Toast.makeText(getActivity(), R.string.upload_recipe_error_message, Toast.LENGTH_LONG).show();
        };

        // If user has chosen a picture to upload
        if (recipeImageUri != null) {
            uploadImageToDatabase(recipeImageUri, taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                    .addOnSuccessListener(uri -> uploadRecipeToDatabase(uri, onUploadRecipeToDatabaseSuccessListener, onUploadRecipeToDatabaseFailureListener))
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(getActivity(), R.string.fetch_recipe_image_error_message, Toast.LENGTH_LONG).show();
                    }), e -> {
                pd.dismiss();
                Toast.makeText(getActivity(), R.string.upload_recipe_image_error_message, Toast.LENGTH_LONG).show();
            });
        } else {
            // When no picture is uploaded by the user
            uploadRecipeToDatabase(null, onUploadRecipeToDatabaseSuccessListener, onUploadRecipeToDatabaseFailureListener);
        }
    }

    private void uploadImageToDatabase(Uri recipeImageUri, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        // We are taking the filepath as storagePath + firebaseUser.getUid() + "/" + currentTime + ".png"
        final String recipeImageStoragePath = "recipes_image/";
        String filePathName = recipeImageStoragePath + userId + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
        storageRef.child(filePathName).putFile(recipeImageUri).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    private void uploadRecipeToDatabase(Uri downloadUri, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        // set image uri to its location in database
        recipe.setImage(downloadUri.toString());
        database.setAsync(recipe)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    private boolean isInputValid() {
        TextInputLayout cookTimeLayout = view.findViewById(R.id.cookTimeContent);
        EditText cookTime = cookTimeLayout.getEditText();
        TextInputLayout prepTimeLayout = view.findViewById(R.id.prepTimeContent);
        EditText prepTime = prepTimeLayout.getEditText();
        TextInputLayout servingsLayout = view.findViewById(R.id.servingsContent);
        EditText servings = servingsLayout.getEditText();

        for (TextView textView : new TextView[]{cookTime, prepTime, servings}) {
            String text = textView.getText().toString();
            if (!TextValidator.isTextValid(text) || !TextValidator.isNumberPositive(text)) {
                return false;
            }
        }

        return isRecipeNameValid && recipeStepAndIngredientManager.isIngredientValid() && recipeStepAndIngredientManager.isStepValid();
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
        recipe.setIngredientList(recipeStepAndIngredientManager.getIngredients());
        recipe.setCuisineTypes(getTypes(cuisineTypeGroup, cuisineTypeValues));
        recipe.setAllergyTypes(getTypes(allergyTypeGroup, allergyTypeValues));
        recipe.setDietTypes(getTypes(dietTypeGroup, dietTypeValues));
        recipe.setSteps(recipeStepAndIngredientManager.getSteps());
        recipe.setUserId(userId);

        return recipe;
    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage() {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme);
        // set the items in builder
        builder.setItems(optionsMenu, (dialogInterface, i) -> {
            if (optionsMenu[i].equals("Take Photo")) {
                if (isCameraAccessible()) {
                    takeAPhoto();
                } else {
                    requireAccessToCamera.launch(android.Manifest.permission.CAMERA);
                }
            } else if (optionsMenu[i].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CHOOSE_IMAGE_FROM_GALLERY_REQUEST);
            } else if (optionsMenu[i].equals("Exit")) {
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

    private void openRecipeReviewDialog() {
        recipe = getRecipe(filePath);
        ReviewRecipeBeforeUploadingDialog reviewRecipeDialog = new ReviewRecipeBeforeUploadingDialog();
        reviewRecipeDialog.setArguments(getBundleForReview(recipe));
        reviewRecipeDialog.setDialogResult(() -> {
            pd.show();
            UploadCallback uploadRecipeCallback = new UploadCallback() {
                @Override
                public void onSuccess() {
                    //sums up all the nutritional values from the ingredients once all the api calls are done
                    recipe.setProtein(recipe.ingredientList.stream().mapToDouble(Ingredient::getProtein).sum());
                    recipe.setCalories(recipe.ingredientList.stream().mapToDouble(Ingredient::getCalories).sum());
                    recipe.setCarbohydrates(recipe.ingredientList.stream().mapToDouble(Ingredient::getCarbs).sum());
                    recipe.setSugar(recipe.ingredientList.stream().mapToDouble(Ingredient::getSugar).sum());
                    recipe.setFat(recipe.ingredientList.stream().mapToDouble(Ingredient::getFat).sum());
                    uploadRecipe(filePath);
                }

                @Override
                public void onError(String err) {
                    Toast.makeText(getActivity(), err, Toast.LENGTH_LONG).show();
                }
            };

            apiService.getNutritionFromRecipe(recipe, idMap, uploadRecipeCallback);
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
        bundle.putString("cuisineTypes", truncateAndConvertEnumArray(recipe.getCuisineTypes(), cuisineTypeValues));
        bundle.putString("allergyTypes", truncateAndConvertEnumArray(recipe.getAllergyTypes(), allergyTypeValues));
        bundle.putString("dietTypes", truncateAndConvertEnumArray(recipe.getDietTypes(), dietTypeValues));
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

    private <T extends Enum<T>> String truncateAndConvertEnumArray(List<Integer> typeArray, T[] enumValues) {
        return truncateString(Arrays.toString(typeArray.stream().map(type -> enumValues[type].toString()).toArray(String[]::new)));
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
        AlertDialog dialog = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
                .setTitle(R.string.login_required_popup_title)
                .setMessage(R.string.login_required_popup_message)
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