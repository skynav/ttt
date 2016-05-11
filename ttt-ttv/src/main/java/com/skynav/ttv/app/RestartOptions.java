/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY SKYNAV, INC. AND ITS CONTRIBUTORS “AS IS” AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SKYNAV, INC. OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.skynav.ttv.app;

import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import com.skynav.xml.helpers.XML;

public class RestartOptions {

    private List<Section> sections;

    private RestartOptions(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public Collection<Section> getSections() {
        return Collections.unmodifiableCollection(sections);
    }

    public static RestartOptions valueOf(String value) {
        if ((value == null) || value.isEmpty())
            throw new IllegalArgumentException();
        else {
            Parser p = new Parser();
            return p.parse(CharBuffer.wrap(value.trim()));
        }
    }

    public static class Section {

        private String name;
        private Map<String,Object> options;

        Section(String name, Map<String,Object> options) {
            assert name != null;
            this.name = name;
            this.options = Collections.unmodifiableMap(options);
        }

        public String getName() {
            return name;
        }

        public Map<String,Object> getOptions() {
            return options;
        }
        
    }

    private static class Parser {

        private State state;
        private boolean skipWhitespace;

        Parser() {
            this.state = new State();
            this.skipWhitespace = true;
        }

        RestartOptions parse(CharBuffer cb) {
            if (cb.hasRemaining()) {
                state.setInput(cb, skipWhitespace);
                parseSections();
                state.expect(Token.EOS);
                return new RestartOptions(state.popSections());
            } else
                throw new ParserException("empty options");
        }
                    
        private void parseSections() {
            Token t = state.next();
            while (t != Token.EOS) {
                if (t.isIdent()) {
                    parseSection();
                } else
                    break;
                t = state.next();
            }
        }

        private void parseSection() {
            Token t0 = state.next();
            if (t0 == null) {
                state.error();
            } else if (t0.isIdent()) {
                state.openSection(t0);
                state.consume();
                state.expect(Token.OPEN);
                parseOptions();
                state.expect(Token.CLOSE);
                state.closeSection();
            } else {
                state.error(Token.IDENT, t0);
            }
        }

        private void parseOptions() {
            Token t = state.next();
            while (t != Token.EOS) {
                if (t.isIdent()) {
                    parseOption();
                } else
                    break;
                t = state.next();
            }
        }

        private void parseOption() {
            Token t0 = state.next();
            Token t1 = null;
            if (t0 == null) {
                state.error();
            } else if (t0.isIdent()) {
                state.consume();
                Token s1 = state.next();
                if (s1 == Token.COLON) {
                    state.consume();
                    t1 = state.next();
                    if (t1.isLiteral()) {
                        state.consume();
                    } else
                        state.error(t1);
                } else if (s1 == Token.SEMICOLON) {
                    t1 = Token.UNDEFINED;
                } else
                    state.error(s1);
                Token s2 = state.next();
                if (s2 == Token.SEMICOLON) {
                    state.consume();
                    state.addOption(t0, t1);
                } else if (s2 == Token.CLOSE) {
                    state.addOption(t0, t1);
                } else {
                    state.error(s2);
                }
            } else {
                state.error(Token.IDENT, t0);
            }
        }

    }

    private static class State {

        Tokenizer tokenizer;            // input tokenizer
        Deque<Token> tokens;          // token list

        State() {
            this.tokens = new java.util.ArrayDeque<Token>();
        }

        Deque<Token> tokens() {
            return tokens;
        }

        void setInput(CharBuffer cb, boolean skipWhitespace) {
            this.tokenizer = new Tokenizer(cb, skipWhitespace);
            tokens().clear();
        }

        Token next() {
            assert tokenizer != null;
            return tokenizer.next();
        }

        void consume() {
            assert tokenizer != null;
            tokenizer.consume();
        }

        void expect(Token t) {
            assert tokenizer != null;
            tokenizer.expect(t);
        }

        void error() {
            error(null);
        }

        void error(Token actual) {
            error(null, actual);
        }

        void error(Token expected, Token actual) {
            tokenizer.error(expected, actual);
        }

        void openSection(Token t) {
            pushToken(t);
        }

        void addOption(Token n, Token v) {
            pushToken(n);
            pushToken(v);
        }

        void closeSection() {
            pushToken(Token.SENTINEL);
        }

        void pushToken(Token t) {
            tokens().add(t);
        }

        List<Section> popSections() {
            List<Section> sections = new java.util.ArrayList<Section>();
            while (tokens().peek() != null) {
                Token sectionName = tokens().remove();
                assert sectionName.isIdent();
                Map<String,Object> options = new java.util.HashMap<String,Object>();
                Token t = tokens().peek();
                while (t != null) {
                    if (t == Token.SENTINEL) {
                        tokens().remove();
                        break;
                    } else if (tokens().size() >= 3) {
                        Token optionName = tokens().remove();
                        assert optionName.isIdent();
                        Token optionValue = tokens().remove();
                        assert optionValue.isLiteral();
                        options.put(optionName.getValue(), optionValue.getLiteralValue());
                    } else
                        throw new IllegalStateException();
                    t = tokens.peek();
                }
                sections.add(new Section(sectionName.getValue(), options));
            }
            return sections;
        }

    }

    public static class Token {

        enum Type {

            BOOLEAN   ("#B"),           // boolean literal
            CLOSE     ("}" ),           // open group
            COLON     (":" ),           // name value separator
            EOS       ("#E"),           // end of stream literal
            IDENT     ("#I"),           // symbol (identifier) literal
            NUMERIC   ("#N"),           // numeric literal
            OPEN      ("{" ),           // open group
            SEMICOLON (";" ),           // name value separator
            SENTINEL  ("##"),           // sentinel
            SPACE     ("#W"),           // whitespace literal
            STRING    ("#S"),           // string literal
            UNDEFINED ("#U");           // undefined literal

            private String shorthand;

            private Type(String shorthand) {
                this.shorthand = shorthand;
            }

            String shorthand() {
                return shorthand;
            }

            boolean isLiteral() {
                return shorthand.charAt(0) == '#';
            }

            @Override
            public String toString() {
                return shorthand();
            }

            static Type valueOfShorthand(String shorthand) {
                if ((shorthand == null) || shorthand.isEmpty())
                    throw new IllegalArgumentException();
                for (Type v: values()) {
                    if (shorthand.equals(v.shorthand()))
                        return v;
                }
                throw new IllegalArgumentException();
            }

        }

        static final Token CLOSE        = new Token(Type.CLOSE);
        static final Token COLON        = new Token(Type.COLON);
        static final Token EOS          = new Token(Type.EOS);
        static final Token IDENT        = new Token(Type.IDENT);
        static final Token OPEN         = new Token(Type.OPEN);
        static final Token SEMICOLON    = new Token(Type.SEMICOLON);
        static final Token SENTINEL     = new Token(Type.SENTINEL);
        static final Token UNDEFINED    = new Token(Type.UNDEFINED);

        private Type type;
        private String value;

        Token(Type type) {
            this(type, null);
        }

        Token(String shorthand, String value) {
            this(Type.valueOfShorthand(shorthand), value);
        }

        Token(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        public Type getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public Object getLiteralValue() {
            assert isLiteral();
            if (type == Type.IDENT) {
                return value;
            } else if (type == Type.STRING) {
                return value;
            } else if (type == Type.NUMERIC) {
                try {
                    return Double.valueOf(value);
                } catch (NumberFormatException e) {
                    throw new IllegalStateException(e);
                }
            } else if (type == Type.BOOLEAN) {
                return Boolean.valueOf(value);
            } else if (type == Type.EOS) {
                return null;
            } else {
                return null;
            }
        }

        public int length() {
            if (isLiteral()) {
                assert value != null;
                return value.length();
            } else
                return getType().shorthand().length();
        }

        public boolean isLiteral() {
            return type.shorthand().charAt(0) == '#';
        }

        public boolean isIdent() {
            return isLiteral() && type.shorthand().charAt(1) == 'I';
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(getType().toString());
            if (isLiteral() && (getValue() != null)) {
                sb.append('(');
                sb.append(getValue());
                sb.append(')');
            }
            return sb.toString();
        }

    }

    private static class Tokenizer {

        private CharBuffer cb;
        private boolean skipWhitespace;

        Tokenizer(CharBuffer cb, boolean skipWhitespace) {
            this.cb = cb;
            this.skipWhitespace = skipWhitespace;
        }

        Token next() {
            return getToken(cb, false, false);
        }

        void consume() {
            getToken(cb, true, skipWhitespace);
        }

        void expect(Token token) {
            Token next = next();
            if ((next != null) && next.equals(token))
                consume();
            else
                error(token, next);
        }

        void error(Token expected, Token actual) {
            throw new UnexpectedTokenException(cb, expected, actual);
        }

        private static Token getToken(CharBuffer cb, boolean consume, boolean skipWhitespace) {
            Token t;
            int   p0 = cb.position();
            int   n  = cb.remaining();
            char  c1 = (n > 0) ? cb.charAt(0) : 0;
            // the following clauses are order dependent
            if (c1 == 0) {
                t = Token.EOS;
            } else if (c1 == '{') {
                t = Token.OPEN;
            } else if (c1 == '}') {
                t = Token.CLOSE;
            } else if (c1 == ':') {
                t = Token.COLON;
            } else if (c1 == ';') {
                t = Token.SEMICOLON;
            } else if (isString(cb)) {
                t = getString(cb);
            } else if (isNumeric(cb)) {
                t = getNumeric(cb);
            } else if (isBoolean(cb)) {
                t = getBoolean(cb);
            } else if (isUndefined(cb)) {
                t = getUndefined(cb);
            } else if (isIdent(cb)) {
                t = getIdent(cb);
            } else
                t = null;
            if (consume && (t != null)) {
                int p1 = p0;
                if (!t.isLiteral())
                    p1 += t.length();
                else
                    p1 = cb.position();
                cb.position(p1);
                if (skipWhitespace) {
                    if (isWhitespace(cb))
                        getWhitespace(cb);
                }
            } else
                cb.position(p0);
            return t;
        }

        private static boolean isWhitespace(CharBuffer cb) {
            return getWhitespace(cb, false) != null;
        }

        private static Token getWhitespace(CharBuffer cb) {
            return getWhitespace(cb, true);
        }

        private static Token getWhitespace(CharBuffer cb, boolean consume) {
            int i = 0;                      // index from cb[position]
            int n = cb.remaining();         // # indices remaining
            StringBuffer sb = new StringBuffer();
            while (i < n) {
                char c = cb.charAt(i);
                if (Character.isWhitespace(c)) {
                    sb.append(c);
                    ++i;
                } else
                    break;
            }
            if (sb.length() > 0) {
                if (consume)
                    cb.position(cb.position() + i);
                return new Token(Token.Type.SPACE, sb.toString());
            } else
                return null;
        }

        private static boolean isString(CharBuffer cb) {
            return getString(cb, false) != null;
        }

        private static Token getString(CharBuffer cb) {
            return getString(cb, true);
        }

        private static Token getString(CharBuffer cb, boolean consume) {
            int i = 0;                      // index from cb[position]
            int n = cb.remaining();         // # indices remaining
            char d;                         // string delimiter
            StringBuffer sb = new StringBuffer();
            // get string delimiter
            d = (i < n) ? cb.charAt(i) : 0;
            if ((d == '\'') || (d == '\"')) {
                ++i;
                // get delimited content, handling escapes
                char c = 0;
                while (i < n) {
                    c = (i < n) ? cb.charAt(i) : 0;
                    if (c == d) {
                        ++i;
                        break;
                    } else if (c == '\\') {
                        ++i;
                        if (i < n) {
                            c = (i < n) ? cb.charAt(i) : 0;
                            sb.append(c);
                            ++i;
                        } else
                            break;
                    } else {
                        sb.append(c);
                        ++i;
                    }
                }
                if (c != d)
                    sb.setLength(0);
            }
            if (sb.length() > 0) {
                if (consume)
                    cb.position(cb.position() + i);
                return new Token(Token.Type.STRING, sb.toString());
            } else
                return null;
        }

        private static boolean isNumeric(CharBuffer cb) {
            return getNumeric(cb, false) != null;
        }

        private static Token getNumeric(CharBuffer cb) {
            return getNumeric(cb, true);
        }

        private static Token getNumeric(CharBuffer cb, boolean consume) {
            int i = 0;                      // index from cb[position]
            int j;                          // helper index
            int k;                          // helper index
            int n = cb.remaining();         // # indices remaining
            int m = 0;                      // # digits in mantissa
            int e = 0;                      // # digits in exponent
            char c;
            StringBuffer sb = new StringBuffer();

            // integral component
            j = i;
            c = (i < n) ? cb.charAt(i) : 0;
            if (c == '0') {
                sb.append(c);
                ++i;
            } else if ((c >= '1') && (c <= '9')) {
                sb.append(c);
                ++i;
                while (i < n) {
                    c = cb.charAt(i);
                    if ((c >= '0') && (c <= '9')) {
                        sb.append(c);
                        ++i;
                    } else
                        break;
                }
            }
            m += j - i;

            // fractional component
            j = i;
            c = (i < n) ? cb.charAt(i) : 0;
            if (c == '.') {
                sb.append(c);
                ++i;
                while (i < n) {
                    c = cb.charAt(i);
                    if ((c >= '0') && (c <= '9')) {
                        sb.append(c);
                        ++i;
                    } else
                        break;
                }
            }
            m += j - i;

            // ensure mantissa is non-empty
            if (m == 0)
                return null;

            // exponent component
            k = sb.length();
            c = (i < n) ? cb.charAt(i) : 0;
            if ((c == 'e') || (c == 'E')) {
                sb.append('E');
                ++i;
                c = (i < n) ? cb.charAt(i) : 0;
                if ((c == '+') || (c == '-')) {
                    sb.append(c);
                    ++i;
                }
                j = i;
                while (i < n) {
                    c = cb.charAt(i);
                    if ((c >= '0') && (c <= '9')) {
                        sb.append(c);
                        ++i;
                    } else
                        break;
                }
                e += j - i;
            }
            if (e == 0)
                sb.setLength(k);

            if (sb.length() > 0) {
                if (consume)
                    cb.position(cb.position() + i);
                return new Token(Token.Type.NUMERIC, sb.toString());
            } else
                return null;
        }

        private static boolean isBoolean(CharBuffer cb) {
            return getBoolean(cb, false) != null;
        }

        private static Token getBoolean(CharBuffer cb) {
            return getBoolean(cb, true);
        }

        private static Token getBoolean(CharBuffer cb, boolean consume) {
            Token t = getIdent(cb, false);
            if (t != null) {
                String ident = t.getValue();
                assert ident != null;
                if (ident.equals("true") || ident.equals("false"))
                    t = new Token(Token.Type.BOOLEAN, ident);
                else
                    t = null;
            }
            if (consume && (t != null))
                cb.position(cb.position() + t.length());
            return t;
        }

        private static boolean isUndefined(CharBuffer cb) {
            return getUndefined(cb, false) != null;
        }

        private static Token getUndefined(CharBuffer cb) {
            return getUndefined(cb, true);
        }

        private static Token getUndefined(CharBuffer cb, boolean consume) {
            Token t = getIdent(cb, false);
            if (t != null) {
                String ident = t.getValue();
                assert ident != null;
                if (ident.equals("undefined"))
                    t = new Token(Token.Type.UNDEFINED, ident);
                else
                    t = null;
            }
            if (consume && (t != null))
                cb.position(cb.position() + t.length());
            return t;
        }

        private static boolean isIdent(CharBuffer cb) {
            return getIdent(cb, false) != null;
        }

        private static Token getIdent(CharBuffer cb) {
            return getIdent(cb, true);
        }

        private static Token getIdent(CharBuffer cb, boolean consume) {
            int i = 0;                      // index from cb[position]
            int n = cb.remaining();         // # indices remaining
            char c;
            StringBuffer sb = new StringBuffer();
            c = (i < n) ? cb.charAt(i) : 0;
            if (XML.isNCNameCharStart(c)) {
                sb.append(c);
                ++i;
                while (i < n) {
                    c = (i < n) ? cb.charAt(i) : 0;
                    if (XML.isNCNameCharPart(c)) {
                        sb.append(c);
                        ++i;
                    } else
                        break;
                }
            }
            if (sb.length() > 0) {
                if (consume)
                    cb.position(cb.position() + i);
                return new Token(Token.Type.IDENT, sb.toString());
            } else
                return null;
        }

    }

    public static class ParserException extends RuntimeException {
        static final long serialVersionUID = 0;
        public ParserException(String message) {
            super(message);
        }
    }

    public static class UnexpectedTokenException extends ParserException {
        static final long serialVersionUID = 0;
        UnexpectedTokenException(CharBuffer cb, Token expected, Token actual) {
            this(cb, (expected != null) ? expected.toString() : null, (actual != null) ? actual.toString() : null);
        }
        UnexpectedTokenException(CharBuffer cb, String expected, String actual) {
            super(makeMessage(cb, expected, actual));
        }
        private static String makeMessage(CharBuffer cb, String expected, String actual) {
            StringBuffer sb = new StringBuffer();
            if (expected != null)
                sb.append("expected " + expected);
            if (sb.length() > 0)
                sb.append(", ");
            if (actual != null)
                sb.append("got " + actual);
            if (sb.length() > 0)
                sb.append(", ");
            if (cb != null)
                sb.append("remaining input \"" + cb + "\"");
            return sb.toString();
        }
    }


}
