package com.github.siela1915.bootcamp.UploadRecipe;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.siela1915.bootcamp.AutocompleteApi.BooleanWrapper;
import com.github.siela1915.bootcamp.AutocompleteApi.IngredientAutocomplete;
import com.github.siela1915.bootcamp.R;
import com.github.siela1915.bootcamp.Recipes.Ingredient;
import com.github.siela1915.bootcamp.Recipes.Unit;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeStepAndIngredientManager {
    private final Context context;
    private final LinearLayout stepListLinearLayout;
    private final LinearLayout ingredientLinearLayout;
    private final LayoutInflater inflater;

    public RecipeStepAndIngredientManager(Context context, LinearLayout stepListLinearLayout, LinearLayout ingredientLinearLayout) {
        this.context = context;
        this.stepListLinearLayout = stepListLinearLayout;
        this.ingredientLinearLayout = ingredientLinearLayout;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean isIngredientValid() {
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

    public void addIngredient(Map<String, Integer> idMap, IngredientAutocomplete apiService) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final View ingredient = inflater.inflate(R.layout.recipe_ingredient_edittext, null, false);
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

    public void removeIngredient(View ingredient) {
        ingredientLinearLayout.removeView(ingredient);
    }

    public List<Ingredient> getIngredients() {
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

    public void addIngredientValidators(EditText ingredientsAmount, EditText ingredientsUnit, EditText ingredientsName) {
        ingredientsAmount.addTextChangedListener(new TextValidator(ingredientsAmount) {
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError(context.getResources().getString(R.string.ingredientsAmountEmptyErrorMessage));
                else if (!isNumberPositive(text))
                    textView.setError(context.getResources().getString(R.string.ingredientsAmountInvalidErrorMessage));
            }
        });

        ingredientsUnit.addTextChangedListener(new TextValidator(ingredientsUnit) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError(context.getResources().getString(R.string.ingredientsUnitEmptyErrorMessage));
            }
        });

        ingredientsName.addTextChangedListener(new TextValidator(ingredientsName) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError(context.getResources().getString(R.string.ingredientsNameEmptyErrorMessage));
            }
        });
    }

    public void setupIngredientAutocomplete(AutoCompleteTextView ingredientAutoComplete, Map<String, Integer> idMap, IngredientAutocomplete apiService){
        ingredientAutoComplete.setThreshold(1);

        final BooleanWrapper optionSelected = new BooleanWrapper(false);
        ingredientAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Clear the focus of the AutoCompleteTextView
                ingredientAutoComplete.clearFocus();
                optionSelected.setBool(true);
            }
        });
        ingredientAutoComplete.addTextChangedListener(new TextWatcher() {
            String prevString;
            boolean isTyping = false;
            private final Handler handler = new Handler();
            private final long DELAY = 500; // milliseconds


            //function to be called if the user stops typing
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(!optionSelected.getBool()) {
                        isTyping = false;
                        //send notification for stopped typing event
                        apiService.completeSearchNames(prevString, ingredientAutoComplete, idMap);
                    }
                }
            };
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //doesn't consider defocusing and refocusing the text field as typing
                if(!s.toString().equals(prevString)) {
                    if (!isTyping) {
                        // Send notification for start typing event
                        ingredientAutoComplete.dismissDropDown();
                        isTyping = true;
                        optionSelected.setBool(false);
                    }
                    handler.removeCallbacks(runnable);
                    prevString = s.toString();
                    handler.postDelayed(runnable, DELAY); // set delay to 2 seconds
                }
            }
            @Override
            public void afterTextChanged(final Editable s) {
            }
        });
    }

    public boolean isStepValid() {
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

    public void addStep() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final View step = inflater.inflate(R.layout.recipe_step_edittext, null, false);
            ImageView removeStep = (ImageView) step.findViewById(R.id.removeStep);
            TextInputLayout stepContent = (TextInputLayout) step.findViewById(R.id.stepContent);
            EditText stepContentEditText = stepContent.getEditText();

            stepContent.setHint("Step " + stepListLinearLayout.getChildCount());
            addStepValidators(stepContentEditText);

            removeStep.setOnClickListener(v -> removeStep(step));

            stepListLinearLayout.addView(step);
        }
    }

    private void removeStep(View step) {
        stepListLinearLayout.removeView(step);
    }

    public List<String> getSteps() {
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

    public void addStepValidators(EditText stepContent) {
        stepContent.addTextChangedListener(new TextValidator(stepContent) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isTextValid(text)) textView.setError(context.getResources().getString(R.string.stepsEmptyErrorMessage));
            }
        });
    }

    private boolean isTextValid(String text) {
        return text != null && !text.trim().isEmpty();
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
}