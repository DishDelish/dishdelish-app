package com.github.siela1915.bootcamp.Recipes;
import android.util.Pair;

import static com.github.siela1915.bootcamp.Labelling.AllergyType.EGGS;
import static com.github.siela1915.bootcamp.Labelling.AllergyType.SESAME;
import static com.github.siela1915.bootcamp.Labelling.CuisineType.*;
import static com.github.siela1915.bootcamp.Labelling.DietType.DAIRY;
import static com.github.siela1915.bootcamp.Labelling.DietType.VEGAN;
import static com.github.siela1915.bootcamp.Labelling.DietType.VEGETARIAN;
import static com.github.siela1915.bootcamp.Recipes.Ingredient.*;


import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;
import com.github.siela1915.bootcamp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExampleRecipes {
    public int recipeNumber = 5;
    public static List<String> commentsList = Arrays.asList("Hey, this was pretty good :)", "Awesome!", "Will definitely make again!", "Needs more salt :(", "Tastes horrible!!!");
    public static List<Recipe> recipes = Arrays.asList(
            new Recipe(R.drawable.omelette_pic, "Omelette", "John Johnathan",
                    R.drawable.pfp, 5., 5, 5, 1, new Utensils(Arrays.asList("Pan")),
                    Arrays.asList(FRENCH.ordinal()), Arrays.asList(EGGS.ordinal()), Arrays.asList(DAIRY.ordinal()),
                    Arrays.asList(new Ingredient("eggs", new Unit(5, "pieces")), new Ingredient("oil", new Unit(5, "g")), new Ingredient("butter", new Unit(5, "g"))),
                    Arrays.asList("STEP 1\nSeason the beaten eggs well with salt and pepper. Heat the oil and butter in a non-stick frying pan over a medium-low heat until the butter has melted and is foaming.",
                            "STEP 2\nPour the eggs into the pan, tilt the pan ever so slightly from one side to another to allow the eggs to swirl and cover the surface of the pan completely. Let the mixture cook for about 20 seconds then scrape a line through the middle with a spatula.",
                            "STEP 3\n" +
                                    "Tilt the pan again to allow it to fill back up with the runny egg. Repeat once or twice more until the egg has just set.",
                            "STEP 4\n" +
                                    "At this point you can fill the omelette with whatever you like – some grated cheese, sliced ham, fresh herbs, sautéed mushrooms or smoked salmon all work well. Scatter the filling over the top of the omelette and fold gently in half with the spatula. Slide onto a plate to serve."
                    ),
                    commentsList
            ),

            new Recipe(R.drawable.cauli, "Cauliflower Rice", "Money Bob",
                       R.drawable.pfp, 3.4, 3, 7, 4, new Utensils(Arrays.asList("Pan")),
                    Arrays.asList(ASIAN.ordinal()), Arrays.asList(), Arrays.asList(),
                    Arrays.asList(new Ingredient("cauliflower", new Unit(1, "piece")), new Ingredient("coriander", new Unit(3, "g"))),
                    Arrays.asList("STEP 1\n" +
                                    "Cut the hard core and stalks from the cauliflower and pulse the rest in a food processor to make grains the size of rice. Tip into a heatproof bowl, cover with cling film, then pierce and microwave for 7 mins on high – there is no need to add any water. Stir in the coriander. For spicier rice, add some toasted cumin seeds."
                            ),
                    commentsList
                    ),

            new Recipe(R.drawable.lemon_pudding, "Lemon Pudding", "Smaug the Golden",
                       R.drawable.pfp, 5., 5, 4, 4, new Utensils(Arrays.asList("Pan")),
                    Arrays.asList(FRENCH.ordinal()), Arrays.asList(EGGS.ordinal()), Arrays.asList(DAIRY.ordinal()),
                    Arrays.asList(new Ingredient("lemon", new Unit(1, "piece")), new Ingredient("sugar", new Unit(100, "g")), new Ingredient("butter", new Unit(100, "g")), new Ingredient("flour", new Unit(100, "g")), new Ingredient("eggs", new Unit(5, "pieces"))),
                    Arrays.asList("STEP 1\n" +
                            "Mix the sugar, butter, flour, eggs, lemon zest and vanilla together until creamy, then spoon into a medium microwave-proof baking dish. Microwave on High for 3 mins, turning halfway through cooking, until risen and set all the way through. Leave to stand for 1 min.",
                               "STEP 2\n" +
                            "Meanwhile, heat the lemon curd for 30 secs in the microwave and stir until smooth. Pour all over the top of the pudding and serve with a dollop of crème fraîche or scoops of ice cream."
                              ),
                    commentsList
            ),

            new Recipe(R.drawable.krabby_patty, "Krabby Patty", "Spongebob Squarepants",
                       R.drawable.pfp, 5., 20, 30, 1, new Utensils(Arrays.asList("Pan", "Spatula")),
                    Arrays.asList(AMERICAN.ordinal()), Arrays.asList(SESAME.ordinal(), EGGS.ordinal()), Arrays.asList(VEGAN.ordinal(), VEGETARIAN.ordinal()),
                    Arrays.asList(new Ingredient("burger bun", new Unit(1, "piece")), new Ingredient("sea lettuce", new Unit(1, "piece")), new Ingredient("sea pickles", new Unit(2, "pieces")), new Ingredient("sea ketchup", new Unit(1, "bottle")), new Ingredient("sea patty", new Unit(1, "piece")), new Ingredient("sea tomato", new Unit(1, "slice")), new Ingredient(" sea cheese", new Unit(1, "piece")), new Ingredient("sea onions", new Unit(2, "slices"))),
                    Arrays.asList("STEP 1\n" +
                            "Obtain the Secret Krabby Patty formula, located in a safe inside the Krusty Krab."+
                            "STEP 2\n" +
                            "Do NOT, under any circumstances, allow yourself to be seen with the formula. Failing to do so may have fatal consequences"+
                            "STEP 3\n" +
                            "Follow the formula, and remember, it's not a Krabby Patty if it's not made with love \u2764"),
                    commentsList
            )



    );




}
