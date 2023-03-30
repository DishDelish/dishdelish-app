package com.github.siela1915.dishdelish.firebase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import com.github.siela1915.bootcamp.firebase.User;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UserTest {
    @Test
    public void defaultConstructorReturnsEmptyObject() {
        User user = new User();
        assertThat(user.getTokens().size(), is(0));
        assertThat(user.getId(), is(nullValue()));
        assertThat(user.getDisplayName(), is(nullValue()));
        assertThat(user.getEmail(), is(nullValue()));
        assertThat(user.getPhotoUrl(), is(nullValue()));
    }

    @Test
    public void idSetterModifiesValue() {
        User user = new User();
        user.setId("TestID");
        assertThat(user.getId(), is("TestID"));
    }

    @Test
    public void displayNameSetterModifiesValue() {
        User user = new User();
        user.setDisplayName("Test Example");
        assertThat(user.getDisplayName(), is("Test Example"));
    }

    @Test
    public void emailSetterModifiesValue() {
        User user = new User();
        user.setEmail("test@example.com");
        assertThat(user.getEmail(), is("test@example.com"));
    }

    @Test
    public void photoUrlSetterModifiesValue() {
        User user = new User();
        user.setPhotoUrl("https://test.com/test.jpg");
        assertThat(user.getPhotoUrl(), is("https://test.com/test.jpg"));
    }

    @Test
    public void tokenSetterModifiesValue() {
        User user = new User();
        Map<String, Long> map = new HashMap<>();
        map.put("TestToken1", System.currentTimeMillis());
        user.setTokens(map);
        assertThat(user.getTokens().keySet(), contains("TestToken1"));
    }

    @Test
    public void addTokenAddsWithoutDuplicates() {
        User user = new User();
        Map<String, Long> map = new HashMap<>();
        map.put("TestToken1", System.currentTimeMillis());
        user.setTokens(map);

        user.addToken("TestToken2");

        assertThat(user.getTokens().keySet(), containsInAnyOrder("TestToken1", "TestToken2"));

        user.addToken("TestToken2");

        assertThat(user.getTokens().keySet(), containsInAnyOrder("TestToken1", "TestToken2"));
    }

    @Test
    public void removeTokenRemovesAndReturnsCorrectBooleanValue() {
        User user = new User();
        Map<String, Long> map = new HashMap<>();
        map.put("TestToken1", System.currentTimeMillis());
        user.setTokens(map);

        assertThat(user.removeToken("TestToken2"), is(false));
        assertThat(user.getTokens().keySet(), containsInAnyOrder("TestToken1"));

        assertThat(user.removeToken("TestToken1"), is(true));
        assertThat(user.getTokens().keySet(), is(empty()));
    }
}
