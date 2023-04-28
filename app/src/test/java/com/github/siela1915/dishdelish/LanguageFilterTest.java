package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.github.siela1915.bootcamp.Recipes.Comment;
import com.github.siela1915.bootcamp.Tools.LanguageFilter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LanguageFilterTest {

    @Test
    public void filterSingleWordsFiltersOutSingleWordCurse() {
        String badString = "This fucking comment talks about anal and shit.";
        String goodString = "This ******* comment talks about **** and ****.";
        assertEquals(goodString, LanguageFilter.filterSingleWords(badString));
    }

    @Test
    public void filterLanguageFiltersOutMultipleWordCurse() {
        String badString = "That motherfucking son of a bitch!";
        String goodString = "That ************* **************!";
        assertEquals(goodString, LanguageFilter.filterLanguage(badString));
    }

    @Test
    public void filterSingleWordsFiltersOutLeetSpeak() {
        String badString = "Sh!t that m0therfucker will 3@t my @ss";
        String goodString = "**** that ************ will 3@t my ***";
        assertEquals(goodString, LanguageFilter.filterSingleWords(badString));
    }

    @Test
    public void filterCommentWithWordsOnlyFiltersRepliesToo() {
        Comment comment = new Comment(100,
                "Fuck this son of a bitch and his home-cooking.",
                new LinkedList<>(Arrays.asList(
                        new Comment(13, "What a cunt!"),
                        new Comment(22, "nah he's got a point, the cooking sucks"))
                ));
        Comment filtered = new Comment(100,
                "**** this son of a ***** and his home-cooking.",
                new LinkedList<>(Arrays.asList(
                        new Comment(13, "What a ****!"),
                        new Comment(22, "nah he's got a point, the cooking *****"))
                ));
        assertEquals(filtered, LanguageFilter.filterComment(comment, false));
    }

    @Test
    public void filterCommentWithLanguageEnabledFiltersRepliesToo() {
        Comment comment = new Comment(100,
                "Fuck this son of a bitch and his home-cooking.",
                new LinkedList<>(Arrays.asList(
                        new Comment(13, "Suck my balls"),
                        new Comment(22, "nah he's got a point, the cooking sucks"))
                ));
        Comment filtered = new Comment(100,
                "**** this ************** and his home-cooking.",
                new LinkedList<>(Arrays.asList(
                        new Comment(13, "*************"),
                        new Comment(22, "nah he's got a point, the cooking *****"))
                ));
        assertEquals(filtered, LanguageFilter.filterComment(comment, true));
    }

    @Test
    public void filterListOfCommentsFiltersAllComments() {
        List<Comment> ls = Arrays.asList(
                new Comment(12, "Rude fucking shit"),
                new Comment(112938477, "I love titties!"),
                new Comment(1, "suck mine then"));
        List<Comment> clean = Arrays.asList(
                new Comment(12, "Rude ******* ****"),
                new Comment(112938477, "I love *******!"),
                new Comment(1, "**** mine then"));
        assertEquals(clean, LanguageFilter.filterComments(ls, false));
    }

    @Test
    public void filterListOfCommentsThrowsExceptionWhenArgumentIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> LanguageFilter.filterComments(null, true));
    }

}
