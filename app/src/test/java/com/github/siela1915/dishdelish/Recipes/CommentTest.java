package com.github.siela1915.dishdelish.Recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Recipes.Comment;

import net.bytebuddy.dynamic.scaffold.MethodGraph;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class CommentTest {

    @Test
    public void getAfterSetReturnsLikes() {
        Comment comment = new Comment(12, "Bad recipe!");
        comment.setLikes(16);
        assertEquals(comment.getLikes(), 16);
    }

    @Test
    public void getAfterSetReturnsContent() {
        Comment comment = new Comment("Bad recipe!");
        comment.setContent("Good recipe.");
        assertEquals(comment.getContent(), "Good recipe.");
    }

    @Test
    public void getAfterSetReturnsReplies() {
        String[] cs = new String[] {"What is this?", "Idk."};
        LinkedList<Comment> ls = new LinkedList<>();
        for (String s : cs) {
            ls.add(new Comment(s));
        }
        Comment comment = new Comment();
        comment.setReplies(ls);
        assertEquals(comment.getReplies(), ls);
    }

    @Test
    public void increaseLikesAddsOne() {
        Comment comment = new Comment(12, "Bad");
        comment.increaseLikes();
        assertEquals(comment.getLikes(), 13);
    }

    @Test
    public void decreaseLikesSubtractsOne() {
        Comment comment = new Comment(12, "Bad");
        comment.decreaseLikes();
        assertEquals(comment.getLikes(), 11);
    }

    @Test
    public void addReplyAddsNoLikesComment() {
        Comment comment = createComposeComment();
        Comment reply = new Comment("Hello there!");
        comment.addReply(reply.getContent());
        List<Comment> replies = comment.getReplies();
        assertEquals(replies.get(replies.size() - 1), reply);
    }

    @Test
    public void removeReplyDecreasesSizeOfReplies() {
        Comment comment = createComposeComment();
        Comment reply = new Comment("Sure.");
        comment.removeReply(reply);
        List<Comment> replies = comment.getReplies();
        assertEquals(replies.size(), 2);
        assertFalse(replies.contains(reply));
    }

    @Test
    public void toStringReturnsContentOnly() {
        Comment comment = createComposeComment();
        assertEquals(comment.toString(), comment.getContent());
    }

    @Test
    public void equalsReturnsFalseForDifferentRepliesSameNumberOfReplies() {
        Comment c1 = createComposeComment();
        Comment c2 = createComposeComment2();
        assertNotEquals(c1, c2);   //Implicit call to equals method.
    }

    @Test
    public void equalsReturnsFalseForDifferentOrderSameNumberOfReplies() {
        Comment c1 = createComposeComment2();
        Comment c2 = createComposeComment3();
        assertNotEquals(c1, c2);   //Implicit call to equals method.
    }

    @Test
    public void equalsReturnsFalseForDifferentNumberOfReplies() {
        Comment c1 = createComposeComment3();
        Comment c2 = createComposeComment4();
        assertNotEquals(c1, c2);   //Implicit call to equals method.
    }

    @Test
    public void equalsReturnsTrueForSameComment() {
        Comment c1 = createComposeComment();
        Comment c2 = createComposeComment();
        assertEquals(c1, c2);   //Implicit call to equals method.
    }

    private Comment createComposeComment() {
        String[] cs = new String[]{"I hate it too.", "Nah it's not too bad.", "Sure."};
        LinkedList<Comment> ls = new LinkedList<>();
        for (String s : cs) {
            ls.add(new Comment(s));
        }
        return new Comment(12, "Bad recipe!", ls);
    }

    private Comment createComposeComment2() {
        String[] cs = new String[]{"Love it!", "Nah not me.", "What???"};
        LinkedList<Comment> ls = new LinkedList<>();
        for (String s : cs) {
            ls.add(new Comment(s));
        }
        return new Comment(12, "Bad recipe!", ls);
    }

    private Comment createComposeComment3() {
        String[] cs = new String[]{"Love it!", "What???", "Nah not me."};
        LinkedList<Comment> ls = new LinkedList<>();
        for (String s : cs) {
            ls.add(new Comment(s));
        }
        return new Comment(12, "Bad recipe!", ls);
    }

    private Comment createComposeComment4() {
        String[] cs = new String[]{"Love it!", "What???"};
        LinkedList<Comment> ls = new LinkedList<>();
        for (String s : cs) {
            ls.add(new Comment(s));
        }
        return new Comment(12, "Bad recipe!", ls);
    }


}
