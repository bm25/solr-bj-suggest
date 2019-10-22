package com.gd.bjq.suggest.search;

import com.google.common.base.Joiner;
import com.google.common.collect.AbstractIterator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.UnicodeWhitespaceAnalyzer;
import org.apache.lucene.analysis.core.UnicodeWhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * This class consists exclusively of static methods that operate on or return
 * {@code tokenStreams}. It contains polymorphic algorithms that operate on
 * {@code tokenStreams}, "wrappers", and a few other odds and ends.
 * <p/>
 * The methods of this class all throw a {@code NullPointerException}
 * if the parameters provided to them are null.
 * <p/>
 * Hides implementation issues associated with obtaining a {@link TokenStream} for use
 * with the apollo.
 *
 * @author imamontov
 * @version 0.1
 * @since apollo-solr-extensions 0.1.0
 */
public class TokenStreams {

    /**
     * Default string joiner for token sequence.
     *
     * @see #join(org.apache.lucene.analysis.TokenStream)
     */
    public static final Joiner DEFAULT_JOINER = Joiner.on(' ');

    /**
     * {@link UnicodeWhitespaceAnalyzer} that is reused
     */
    private static final UnicodeWhitespaceAnalyzer UNICODE_WHITESPACE_ANALYZER = new UnicodeWhitespaceAnalyzer();

    /**
     * Reusable analyzer that tokenizes with {@link UnicodeWhitespaceTokenizer}
     * and then filters with {@link ASCIIFoldingFilter}.
     */
    private static final Analyzer ASCII_FOLDING_ANALYZER = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            Tokenizer source = new UnicodeWhitespaceTokenizer();
            TokenStream sink = new ASCIIFoldingFilter(source, false);
            return new TokenStreamComponents(source, sink);
        }
    };

    /**
     * Creates a TokenStream from {@code analyzer} that is allowed to be re-used
     * from the previous time that the same thread called this method.
     * <p/>
     * Callers that do not need to use more than one TokenStream at the same time from this
     * analyzer should use this method for better performance.
     *
     * @param analyzer  builds {@code TokenStreams}, which analyze {@code source}.
     * @param fieldName name of concrete schema field, can not be {@code null}.
     * @param source    string providing the character stream.
     * @return {@link TokenStream} which breaks the stream of characters from the {@code source} into raw tokens.
     */
    public static TokenStream getTokenStream(Analyzer analyzer, String fieldName, String source) {
        try {
            TokenStream tokenStream = analyzer.tokenStream(fieldName, new StringReader(source));
            return tokenStream;
        } catch (Exception e) {
            throw new RuntimeException("Can't create reusable token stream from source " + source + " field " + fieldName, e);
        }
    }

    /**
     * Returns an iterator that applies {@link Character#isWhitespace(char)} to each element of {@code input}.
     *
     * @param input the input to split up into tokens.
     * @return lazy evaluated iterator.
     */
    public static Iterator<String> tokenizeWhitespace(String input) {
        return getTokens(getWhitespaceTokenStream(input));
    }

    public static TokenStream getWhitespaceTokenStream(String input) {
        return UNICODE_WHITESPACE_ANALYZER.tokenStream("fieldNameDoesNotMatterHere", input);
    }

    public static Iterator<String> tokenizeAndFoldToASCII(String input) {
        return getTokens(getASCIIFoldedTokenStream(input));
    }

    public static TokenStream getASCIIFoldedTokenStream(String input) {
        return ASCII_FOLDING_ANALYZER.tokenStream("fieldNameDoesNotMatterHere", input);
    }

    /**
     * Util function for counting number of words in input string.
     * Words can be separated by any number of whitespaces. Leading and trailing whitespace should be omitted.
     *
     * @param str phrase with words
     * @return number of words in string
     */
    public static int numberOfWords(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int count = 0;
        boolean newWordStarted = true;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                newWordStarted = true;
            } else {
                if (newWordStarted) {
                    count++;
                    newWordStarted = false;
                }
            }
        }
        return count;
    }

    /**
     * Returns an iterator that applies {@link TokenStream#incrementToken()} to each element of {@code stream}
     * element, which may be lazily evaluated.
     *
     * @param stream token stream.
     * @return lazy evaluated iterator.
     */
    public static Iterator<String> getTokens(final TokenStream stream) {
        try {
            stream.reset();
        } catch (IOException e) {
            throw new RuntimeException("Reset token stream failed " + stream, e);
        }

        final CharTermAttribute attribute = stream.getAttribute(CharTermAttribute.class);

        return new AbstractIterator<String>() {
            @Override
            protected String computeNext() {
                try {
                    return stream.incrementToken() ? attribute.toString() : end();
                } catch (IOException e) {
                    throw new RuntimeException("Read token from stream failed " + stream, e);
                }
            }

            private String end() throws IOException {
                stream.end();
                stream.close();
                return endOfData();
            }
        };
    }

    /**
     * Returns a string containing the string representation of each {@code token} of {@code stream},
     * using default separator(' ') between each.
     *
     * @param stream token stream.
     * @return string as a result of analysis process.
     */
    public static String join(TokenStream stream) {
        return DEFAULT_JOINER.skipNulls().join(getTokens(stream));
    }
}
