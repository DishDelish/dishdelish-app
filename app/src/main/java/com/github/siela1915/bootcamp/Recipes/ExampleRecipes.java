package com.github.siela1915.bootcamp.Recipes;

import static com.github.siela1915.bootcamp.Labelling.AllergyType.EGGS;
import static com.github.siela1915.bootcamp.Labelling.AllergyType.SESAME;
import static com.github.siela1915.bootcamp.Labelling.CuisineType.AMERICAN;
import static com.github.siela1915.bootcamp.Labelling.CuisineType.ASIAN;
import static com.github.siela1915.bootcamp.Labelling.CuisineType.FRENCH;
import static com.github.siela1915.bootcamp.Labelling.DietType.DAIRY;
import static com.github.siela1915.bootcamp.Labelling.DietType.VEGAN;
import static com.github.siela1915.bootcamp.Labelling.DietType.VEGETARIAN;

import com.github.siela1915.bootcamp.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExampleRecipes {
    public static List<Comment> commentsList = Arrays.asList(new Comment("Hey, this was pretty good :)"), new Comment("Awesome!"), new Comment("Will definitely make again!"), new Comment("Needs more salt :("), new Comment("Tastes horrible!!!"));
    public static List<Recipe> recipes = Arrays.asList(
            new Recipe(Integer.toString(R.drawable.omelette_pic), "Omelette", "John Johnathan",
                    R.drawable.pfp, 5., 5, 5, 1, new Utensils(Collections.singletonList("Pan")),
                    Collections.singletonList(FRENCH.ordinal()), Collections.singletonList(EGGS.ordinal()), Collections.singletonList(DAIRY.ordinal()),
                    Arrays.asList(new Ingredient("eggs", new Unit(5, "pieces")), new Ingredient("oil", new Unit(5, "g")), new Ingredient("butter", new Unit(5, "g"))),
                    Arrays.asList("Season the beaten eggs well with salt and pepper. Heat the oil and butter in a non-stick frying pan over a medium-low heat until the butter has melted and is foaming.",
                            "Pour the eggs into the pan, tilt the pan ever so slightly from one side to another to allow the eggs to swirl and cover the surface of the pan completely. Let the mixture cook for about 20 seconds then scrape a line through the middle with a spatula.",
                            "Tilt the pan again to allow it to fill back up with the runny egg. Repeat once or twice more until the egg has just set.",
                            "At this point you can fill the omelette with whatever you like – some grated cheese, sliced ham, fresh herbs, sautéed mushrooms or smoked salmon all work well. Scatter the filling over the top of the omelette and fold gently in half with the spatula. Slide onto a plate to serve."
                    ),
                    commentsList, 12
            ),

            new Recipe(Integer.toString(R.drawable.cauli), "Cauliflower Rice", "Money Bob",
                       R.drawable.pfp, 3.4, 3, 7, 4, new Utensils(Collections.singletonList("Pan")),
                    Collections.singletonList(ASIAN.ordinal()), Collections.emptyList(), Collections.emptyList(),
                    Arrays.asList(new Ingredient("cauliflower", new Unit(1, "piece")), new Ingredient("coriander", new Unit(3, "g"))),
                    Collections.singletonList("Cut the hard core and stalks from the cauliflower and pulse the rest in a food processor to make grains the size of rice. Tip into a heatproof bowl, cover with cling film, then pierce and microwave for 7 mins on high – there is no need to add any water. Stir in the coriander. For spicier rice, add some toasted cumin seeds."
                    ),
                    commentsList, 456
                    ),

            new Recipe(Integer.toString(R.drawable.lemon_pudding), "Lemon Pudding", "Smaug the Golden",
                       R.drawable.pfp, 5., 5, 4, 4, new Utensils(Collections.singletonList("Pan")),
                    Collections.singletonList(FRENCH.ordinal()), Collections.singletonList(EGGS.ordinal()), Collections.singletonList(DAIRY.ordinal()),
                    Arrays.asList(new Ingredient("lemon", new Unit(1, "piece")), new Ingredient("sugar", new Unit(100, "g")), new Ingredient("butter", new Unit(100, "g")), new Ingredient("flour", new Unit(100, "g")), new Ingredient("eggs", new Unit(5, "pieces"))),
                    Arrays.asList("Mix the sugar, butter, flour, eggs, lemon zest and vanilla together until creamy, then spoon into a medium microwave-proof baking dish. Microwave on High for 3 mins, turning halfway through cooking, until risen and set all the way through. Leave to stand for 1 min.",
                               "Meanwhile, heat the lemon curd for 30 secs in the microwave and stir until smooth. Pour all over the top of the pudding and serve with a dollop of crème fraîche or scoops of ice cream."
                              ),
                    commentsList, 1
            ),

            new Recipe(Integer.toString(R.drawable.krabby_patty), "Krabby Patty", "Spongebob Squarepants",
                       R.drawable.pfp, 5., 20, 30, 1, new Utensils(Arrays.asList("Pan", "Spatula")),
                    Collections.singletonList(AMERICAN.ordinal()), Arrays.asList(SESAME.ordinal(), EGGS.ordinal()), Arrays.asList(VEGAN.ordinal(), VEGETARIAN.ordinal()),
                    Arrays.asList(new Ingredient("burger bun", new Unit(1, "piece")), new Ingredient("sea lettuce", new Unit(1, "piece")), new Ingredient("sea pickles", new Unit(2, "pieces")), new Ingredient("sea ketchup", new Unit(1, "bottle")), new Ingredient("sea patty", new Unit(1, "piece")), new Ingredient("sea tomato", new Unit(1, "slice")), new Ingredient(" sea cheese", new Unit(1, "piece")), new Ingredient("sea onions", new Unit(2, "slices"))),
                    Collections.singletonList("Obtain the Secret Krabby Patty formula, located in a safe inside the Krusty Krab." +
                            "Do NOT, under any circumstances, allow yourself to be seen with the formula. Failing to do so may have fatal consequences" +
                            "Follow the formula, and remember, it's not a Krabby Patty if it's not made with love \u2764"),
                    commentsList, 5000
            )
    );
}
