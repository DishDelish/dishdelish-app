package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;

import com.github.siela1915.bootcamp.Tools.LanguageFilter;

import org.junit.Test;

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
        String goodString = "That ************* *** ** * *****";
        assertEquals(goodString, LanguageFilter.filterLanguage(badString));
    }

    @Test
    public void filterSingleWordsFiltersOutLeetSpeak() {
        String badString = "Sh!t that m0therfucker will 3@t my @ss";
        String goodString = "**** that ************ will 3@t my ***";
        assertEquals(goodString, LanguageFilter.filterSingleWords(badString));
    }

}
