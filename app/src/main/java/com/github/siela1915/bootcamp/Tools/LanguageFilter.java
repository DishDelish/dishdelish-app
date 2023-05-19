package com.github.siela1915.bootcamp.Tools;

import com.github.siela1915.bootcamp.Recipes.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LanguageFilter {

    /*
        Hardcoding the list of bad words means we don't have to load it from a remote or local
        file when the app loads.
        Storing the words as a HashSet instead of a simple List means we can search for a word in
        O(1) instead of O(n), n being the size of the list.
     */
    private final static Set<String> badWords = new HashSet<>(Arrays.asList("2g1c",
            "2 girls 1 cup",
            "acrotomophilia",
            "alabama hot pocket",
            "alaskan pipeline",
            "anal",
            "anilingus",
            "anus",
            "apeshit",
            "arsehole",
            "ass",
            "asshole",
            "assmunch",
            "auto erotic",
            "autoerotic",
            "babeland",
            "baby batter",
            "baby juice",
            "ball gag",
            "ball gravy",
            "ball kicking",
            "ball licking",
            "ball sack",
            "ball sucking",
            "bangbros",
            "bareback",
            "barely legal",
            "barenaked",
            "bastard",
            "bastardo",
            "bastinado",
            "beaner",
            "beaners",
            "beaver cleaver",
            "beaver lips",
            "bestiality",
            "big black",
            "big breasts",
            "big knockers",
            "big tits",
            "bimbos",
            "birdlock",
            "bitches",
            "bitch",
            "black cock",
            "blonde action",
            "blonde on blonde action",
            "blowjob",
            "blow job",
            "blow your load",
            "blue waffle",
            "blumpkin",
            "bollocks",
            "bondage",
            "boner",
            "boob",
            "boobs",
            "booty call",
            "brown showers",
            "brunette action",
            "bukkake",
            "bulldyke",
            "bullet vibe",
            "bullshit",
            "bung hole",
            "bunghole",
            "busty",
            "buttcheeks",
            "butthole",
            "camel toe",
            "camgirl",
            "camslut",
            "camwhore",
            "carpet muncher",
            "carpetmuncher",
            "chocolate rosebuds",
            "circlejerk",
            "cleveland steamer",
            "clitoris",
            "clover clamps",
            "clusterfuck",
            "cock",
            "cocks",
            "coprolagnia",
            "coprophilia",
            "cornhole",
            "coon",
            "coons",
            "creampie",
            "cumming",
            "cunnilingus",
            "cunt",
            "darkie",
            "date rape",
            "daterape",
            "deep throat",
            "deepthroat",
            "dendrophilia",
            "dick",
            "dickhead",
            "dildo",
            "dingleberry",
            "dingleberries",
            "dirty pillows",
            "dirty sanchez",
            "doggie style",
            "doggiestyle",
            "doggy style",
            "doggystyle",
            "dog style",
            "dolcett",
            "domination",
            "dominatrix",
            "dommes",
            "donkey punch",
            "double dong",
            "double penetration",
            "dp action",
            "dry hump",
            "eat my ass",
            "ecchi",
            "ejaculation",
            "erotic",
            "erotism",
            "escort",
            "eunuch",
            "faggot",
            "fecal",
            "fellatio",
            "feltch",
            "female squirting",
            "femdom",
            "figging",
            "fingerbang",
            "fingering",
            "fisting",
            "foot fetish",
            "footjob",
            "frotting",
            "fuck",
            "fuck buttons",
            "fuckin",
            "fucking",
            "fucktards",
            "fudge packer",
            "fudgepacker",
            "futanari",
            "gang bang",
            "gay sex",
            "genitals",
            "giant cock",
            "girl on top",
            "girls gone wild",
            "goatcx",
            "goatse",
            "god damn",
            "gokkun",
            "golden shower",
            "goodpoop",
            "goo girl",
            "goregasm",
            "grope",
            "group sex",
            "g-spot",
            "hand job",
            "handjob",
            "hard core",
            "hardcore",
            "hentai",
            "homoerotic",
            "honkey",
            "hooker",
            "hot carl",
            "hot chick",
            "how to kill",
            "how to murder",
            "huge fat",
            "humping",
            "incest",
            "intercourse",
            "jack off",
            "jail bait",
            "jailbait",
            "jelly donut",
            "jerk off",
            "jigaboo",
            "jiggaboo",
            "jiggerboo",
            "jizz",
            "juggs",
            "kike",
            "kinbaku",
            "kinkster",
            "kinky",
            "knobbing",
            "leather restraint",
            "leather straight jacket",
            "lemon party",
            "lolita",
            "lovemaking",
            "make me come",
            "male squirting",
            "masturbate",
            "menage a trois",
            "milf",
            "missionary position",
            "motherfucker",
            "motherfucking",
            "mound of venus",
            "mr hands",
            "muff diver",
            "muffdiving",
            "nambla",
            "nawashi",
            "negro",
            "neonazi",
            "nigga",
            "nigger",
            "nig nog",
            "nimphomania",
            "nipple",
            "nipples",
            "nsfw images",
            "nude",
            "nudity",
            "nympho",
            "nymphomania",
            "octopussy",
            "omorashi",
            "one cup two girls",
            "one guy one jar",
            "orgasm",
            "orgy",
            "paedophile",
            "panties",
            "panty",
            "pedobear",
            "pedophile",
            "pegging",
            "penis",
            "phone sex",
            "piece of shit",
            "pissing",
            "piss pig",
            "pisspig",
            "playboy",
            "pleasure chest",
            "pole smoker",
            "ponyplay",
            "poontang",
            "punany",
            "poop chute",
            "poopchute",
            "porn",
            "porno",
            "pornography",
            "prince albert piercing",
            "pubes",
            "pussy",
            "queaf",
            "queef",
            "quim",
            "raghead",
            "raging boner",
            "raping",
            "rapist",
            "rectum",
            "reverse cowgirl",
            "rimjob",
            "rimming",
            "rosy palm",
            "rosy palm and her 5 sisters",
            "rusty trombone",
            "sadism",
            "santorum",
            "schlong",
            "scissoring",
            "semen",
            "shaved beaver",
            "shaved pussy",
            "shemale",
            "shibari",
            "shit",
            "shitblimp",
            "shitty",
            "shota",
            "shrimping",
            "skeet",
            "slanteye",
            "slut",
            "s&m",
            "smut",
            "snatch",
            "snowballing",
            "sodomize",
            "sodomy",
            "son of a bitch",
            "splooge",
            "splooge moose",
            "spooge",
            "spread legs",
            "spunk",
            "strap on",
            "strapon",
            "strappado",
            "strip club",
            "style doggy",
            "suck",
            "suck my balls",
            "sucks",
            "suicide girls",
            "sultry women",
            "swastika",
            "swinger",
            "tainted love",
            "taste my",
            "tea bagging",
            "threesome",
            "throating",
            "tied up",
            "tight white",
            "tits",
            "titties",
            "titty",
            "tongue in a",
            "topless",
            "tosser",
            "towelhead",
            "tranny",
            "tribadism",
            "tub girl",
            "tubgirl",
            "tushy",
            "twat",
            "twink",
            "twinkie",
            "two girls one cup",
            "undressing",
            "upskirt",
            "urethra play",
            "urophilia",
            "vagina",
            "venus mound",
            "vibrator",
            "violet wand",
            "vorarephilia",
            "voyeur",
            "vulva",
            "wank",
            "wetback",
            "wet dream",
            "white power",
            "wrapping men",
            "wrinkled starfish",
            "yaoi",
            "yellow showers",
            "yiffy",
            "zoophilia")
    );
    private final static int maxBadWordLength = 27;

    /**
     * Filters out rude words from a string. Replaces any rude words with stars ****
     * @param input the string to filter.
     * @return the filtered string.
     * For a more extensive filter, see filterLangugage().
     */
    public static String filterSingleWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        //Don't forget to filter out any punctuation or other symbols
        String s = filterLeetSpeach(input).toLowerCase().replaceAll("[^a-z]", " ");
        Set<String> words = new HashSet<>(Arrays.asList(
                s.split(" ")
        ));
        for (String word : words) {
            if (badWords.contains(word)) {
                String stars = stars(word.length());
                // Add (?i) to remove case sensitivity.
                for (String option : leetOptions(word)) {
                    input = input.replaceAll("(?i)" + option, stars);
                }
            }
        }
        return input;
    }

    /**
     * Filters out rude words and combinations of words that are considered rude and replaces
     * the whole combination with stars ****
     * @param input the string to filter.
     * @return the filtered string.
     * For simple rude word filtering see filterSingleWords().
     */
    public static String filterLanguage(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String s = filterLeetSpeach(input).toLowerCase().replaceAll("[^a-z]", " ");
        for(int i = 0; i < s.length(); ++i) {
            // from each letter, keep going to find bad words until either the end of the sentence
            // is reached, or the max word length is reached.
            for (int j = 1; j < (s.length() + 1 - i) && j < maxBadWordLength; ++j) {
                String substring = s.substring(i, i + j);
                if (badWords.contains(substring)) {
                    for (String option : leetOptions(substring)) {
                        input = input.replaceAll("(?i)" + option, stars(option.length()));
                    }
                }
            }
        }
        return input;
    }

    /**
     * Applies a profanity filter on the given comment and its replies.
     * @param comment the comment to filter.
     * @param filterLanguage filters certain expressions and phrases in addition to words when true,
     *                       only filters words when false.
     * @return the filtered comment and filtered replies.
     */
    public static Comment filterComment(Comment comment, boolean filterLanguage) {
        if (filterLanguage) {
            String filtered = filterLanguage(comment.getContent());
            if (comment.getReplies().size() == 0) {
                return new Comment(comment.getLikes(), filtered, new LinkedList<>());
            }
            LinkedList<Comment> replies = new LinkedList<>();
            for (Comment c : comment.getReplies()) {
                replies.add(filterComment(c, true));
            }
            return new Comment(comment.getLikes(), filtered, replies);
        } else {
            String filtered = filterSingleWords(comment.getContent());
            if (comment.getReplies().size() == 0) {
                return new Comment(comment.getLikes(), filtered, new LinkedList<>());
            }
            LinkedList<Comment> replies = new LinkedList<>();
            for (Comment c : comment.getReplies()) {
                replies.add(filterComment(c, false));
            }
            return new Comment(comment.getLikes(), filtered, replies);
        }
    }

    /**
     * Applies a profanity filter on a list of comments and their replies.
     * @param comments the list of comments to filter
     * @param filterLanguage filters certain expressions and phrases in addition to words when true,
     *                       only filters words when false.
     * @return a list of the fitlered comments and their replies.
     */
    public static List<Comment> filterComments(List<Comment> comments, boolean filterLanguage) {
        if (comments == null) {
            throw new IllegalArgumentException("Cannot filter a null list.");
        }
        List<Comment> ls = new ArrayList<>();
        for (Comment c : comments) {
            ls.add(filterComment(c, filterLanguage));
        }
        return ls;
    }

    private static String stars(int length) {
        char[] charsStars = new char[length];
        Arrays.fill(charsStars, '*');
        return new String(charsStars);
    }

    private final static String[] leetSymbols = new String[]{"1", "!", "3", "4", "@", "5", "7", "0", "9"};
    private final static String[] leetReplacements = new String[]{"i", "i", "e", "a", "a", "s", "t", "o", "g"};

    private static String filterLeetSpeach(String input) {
        for (int i = 0; i < leetSymbols.length; ++i) {
            input = input.replaceAll(leetSymbols[i], leetReplacements[i]);
        }
        return input;
    }

    private static List<String> leetOptions(String s) {
        List<String> options = new ArrayList<>();
        options.add(s);
        for (int i = 0; i < leetSymbols.length; ++i) {
            if (s.contains(leetReplacements[i])) {
                options.add(s.replaceAll(leetReplacements[i], leetSymbols[i]));
            }
        }
        return options;
    }



}
