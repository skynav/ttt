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

package com.skynav.ttv.model.value;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.skynav.xml.helpers.XML;

public class Condition {

    private Expression expression;

    private Condition(Expression expression) {
        assert expression != null;
        this.expression = expression;
    }

    /*
    public boolean evaluate() {
        return false; // [TBD] - IMPLEMENT ME
    }
    */

    @Override
    public String toString() {
        return expression.toString();
    }

    public static Condition fromValue(String condition) throws ParserException {
        Parser p = new Parser();
        return p.parse(CharBuffer.wrap(condition.trim()));
    }

    /**
     * A condition expression parser based upon the algorithm
     * described in "Parsing Expressions by Recursive Descent", by
     * Theodore Norvell (1999).
     *
     * For the sake of convenience, this implementation retains the
     * recursive nature of the Norvell algorithm. Converting to full
     * tail recursion or iteration is an exercise for future
     * optimizers.
     */
    private static class Parser {

        private State state;
        private boolean skipWhitespace;

        Parser() {
            this.state = new State();
            this.skipWhitespace = true;
        }

        Condition parse(CharBuffer cb) {
            if (cb.hasRemaining()) {
                state.setInput(cb, skipWhitespace);
                state.pushOperator(Operator.SENTINEL);
                parseExpression();
                state.expect(Token.EOS);
                return new Condition(state.topOperand());
            } else
                throw new ParserException("empty condition");
        }

        private void parseExpression() {
            parsePrimary();
            Token t = state.next();
            while (t != Token.EOS) {
                if (t.isBinaryOp()) {
                    state.consume();
                    state.pushOperator(Operator.fromToken(t, OperatorContext.BINARY));
                    parsePrimary();
                } else
                    break;
                t = state.next();
            }
            while (state.topOperator() != Operator.SENTINEL)
                state.popOperator();
        }

        private void parsePrimary() {
            Token t = state.next();
            if (t == null) {
                state.error();
            } if (t.isLiteral()) {
                state.consume();
                state.pushOperand(t);
            } else if (t == Token.OPEN) {
                state.consume();
                state.pushOperator(Operator.SENTINEL);
                parseExpression();
                state.expect(Token.CLOSE);
                state.popOperator();
            } else if (t.isUnaryOp()) {
                state.consume();
                state.pushOperator(Operator.fromToken(t, OperatorContext.UNARY));
                parsePrimary();
            } else
                state.error(t);
        }

    }
    
    private static class State {

        Tokenizer tokenizer;            // input tokenizer
        Stack<Operator> operators;      // operator stack
        Stack<Expression> operands;     // operand stack (an operand is either a literal token or an expression)

        State() {
            this.operators = new Stack<Operator>();
            this.operands = new Stack<Expression>();
        }

        void setInput(CharBuffer cb, boolean skipWhitespace) {
            this.tokenizer = new Tokenizer(cb, skipWhitespace);
            this.operators.clear();
            this.operands.clear();
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
            tokenizer.error();
        }

        void error(Token t) {
            tokenizer.error(t);
        }

        void pushOperator(Operator op) {
            Operator top;
            while ((top = topOperator()) != null) {
                if (top.comparePrecedence(op) > 0)
                    popOperator();
                else
                    break;
            }
            this.operators.push(op);
        }

        Operator popOperator() {
            assert !operators.empty();
            Operator top = topOperator();
            if (top != null) {
                if (top.isBinary()) {
                    assert operators.size() > 0;
                    assert operands.size() > 1;
                    Object o1 = operands.pop();
                    Object o0 = operands.pop();
                    pushOperand(new Expression(operators.pop(), Arrays.asList(new Object[]{ o0, o1 })));
                    return top;
                } else if (top.isUnary()) {
                    assert operands.size() > 0;
                    Object o0 = operands.pop();
                    pushOperand(new Expression(operators.pop(), o0));
                    return top;
                } else {
                    assert false;
                    throw new ParserStateException("unexpected operator: " + top);
                }
            } else
                throw new ParserStateException("operator stack is empty");
        }

        Operator topOperator() {
            return operators.empty() ? null : operators.peek();
        }

        void pushOperand(Token t) {
            assert t.isLiteral();
            pushOperand(new Expression(Operator.LITERAL, t));
        }

        void pushOperand(Expression e) {
            this.operands.push(e);
        }

        Expression topOperand() {
            return operands.empty() ? null : operands.peek();
        }

    }

    public static class Token {

        enum Type {

            AND     ("&&"),             // logical and
            BOOLEAN ("#B"),             // boolean literal
            CLOSE   (")" ),             // open group
            COMMA   ("," ),             // comma (argument list separator)
            DIVIDE  ("/" ),             // divide by
            EOS     ("#E"),             // end of stream literal
            EQ      ("=="),             // equals
            GEQ     (">="),             // greater than or equals
            GT      (">" ),             // greater than
            IDENT   ("#I"),             // symbol (identifier) literal
            LEQ     ("<="),             // less than or equals
            LT      ("<" ),             // less than
            MINUS   ("-" ),             // minus or subtract from
            MODULO  ("%" ),             // modulus of
            MULTIPLY("*" ),             // multiply by
            NEQ     ("!="),             // not equals
            NOT     ("!" ),             // logical not
            NUMERIC ("#N"),             // numeric literal
            OPEN    ("(" ),             // open group
            OR      ("||"),             // logical or
            PLUS    ("+" ),             // plus or add to
            SPACE   ("#W"),             // whitespace literal
            STRING  ("#S");             // string literal

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

        static final Token AND          = new Token(Type.AND);
        static final Token CLOSE        = new Token(Type.CLOSE);
        static final Token COMMA        = new Token(Type.COMMA);
        static final Token DIVIDE       = new Token(Type.DIVIDE);
        static final Token EOS          = new Token(Type.EOS);
        static final Token EQ           = new Token(Type.EQ);
        static final Token GEQ          = new Token(Type.GEQ);
        static final Token GT           = new Token(Type.GT);
        static final Token IDENT        = new Token(Type.IDENT);
        static final Token LEQ          = new Token(Type.LEQ);
        static final Token LT           = new Token(Type.LT);
        static final Token MINUS        = new Token(Type.MINUS);
        static final Token MODULO       = new Token(Type.MODULO);
        static final Token MULTIPLY     = new Token(Type.MULTIPLY);
        static final Token NEQ          = new Token(Type.NEQ);
        static final Token NOT          = new Token(Type.NOT);
        static final Token OPEN         = new Token(Type.OPEN);
        static final Token OR           = new Token(Type.OR);
        static final Token PLUS         = new Token(Type.PLUS);

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

        public boolean isBinaryOp() {
            Operator op = Operator.fromToken(this, OperatorContext.BINARY);
            return (op != null) ? op.isBinary() : false;
        }

        public boolean isUnaryOp() {
            Operator op = Operator.fromToken(this, OperatorContext.UNARY);
            return (op != null) ? op.isUnary() : false;
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

        private Token expect(Token token) {
            Token next = next();
            if ((next != null) && next.equals(token))
                return next;
            else {
                error(token, next);
                return null;
            }
        }

        private Token next() {
            return getToken(cb, false, false);
        }

        private void consume() {
            getToken(cb, true, skipWhitespace);
        }

        private void error() {
            error(null, null);
        }

        private void error(Token actual) {
            error(null, actual);
        }

        private void error(Token expected, Token actual) {
            throw new UnexpectedTokenException(cb, expected, actual);
        }

        private static Token getToken(CharBuffer cb, boolean consume, boolean skipWhitespace) {
            Token t;
            int   p0 = cb.position();
            int   n  = cb.remaining();
            char  c1 = (n > 0) ? cb.charAt(0) : 0;
            char  c2 = (n > 1) ? cb.charAt(1) : 0;
            // the following clauses are order dependent
            if (c1 == 0) {
                t = Token.EOS;
            } else if (c1 == '(') {
                t = Token.OPEN;
            } else if (c1 == ')') {
                t = Token.CLOSE;
            } else if (c1 == ',') {
                t = Token.COMMA;
            } else if (c1 == '+') {
                t = Token.PLUS;
            } else if (c1 == '-') {
                t = Token.MINUS;
            } else if (c1 == '*') {
                t = Token.MULTIPLY;
            } else if (c1 == '/') {
                t = Token.DIVIDE;
            } else if (c1 == '%') {
                t = Token.MODULO;
            } else if (c1 == '=') {
                if (c2 == '=') {
                    t = Token.EQ;
                } else
                    t = null;
            } else if (c1 == '!') {
                if (c2 == '=') {
                    t = Token.NEQ;
                } else
                    t = Token.NOT;
            } else if (c1 == '<') {
                if (c2 == '=')
                    t = Token.LEQ;
                else
                    t = Token.LT;
            } else if (c1 == '>') {
                if (c2 == '=')
                    t = Token.GEQ;
                else
                    t = Token.GT;
            } else if (c1 == '|') {
                if (c2 == '|')
                    t = Token.OR;
                else
                    t = null;
            } else if (c1 == '&') {
                if (c2 == '&')
                    t = Token.AND;
                else
                    t = null;
            } else if (isString(cb)) {
                t = getString(cb);
            } else if (isNumeric(cb)) {
                t = getNumeric(cb);
            } else if (isBoolean(cb)) {
                t = getBoolean(cb);
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

    public enum Associativity {
        NONE,
        LEFT,
        RIGHT;
    }

    public enum OperatorContext {
        BINARY,
        UNARY;
    }

    public enum Operator {

        ADD             ( 5, Associativity.LEFT  ),
        AND             ( 2, Associativity.LEFT  ),
        COMMA           ( 0, Associativity.LEFT  ),
        DIVIDE          ( 6, Associativity.LEFT  ),
        EQ              ( 3, Associativity.LEFT  ),
        FUNCTION        ( 7, Associativity.NONE  ),
        GEQ             ( 4, Associativity.LEFT  ),
        GROUP           ( 8, Associativity.NONE  ),
        GT              ( 4, Associativity.LEFT  ),
        LEQ             ( 4, Associativity.LEFT  ),
        LITERAL         (-1, Associativity.NONE  ),
        LT              ( 4, Associativity.LEFT  ),
        MINUS           ( 7, Associativity.RIGHT ),
        MODULO          ( 6, Associativity.LEFT  ),
        MULTIPLY        ( 6, Associativity.LEFT  ),
        NEQ             ( 3, Associativity.LEFT  ),
        NOT             ( 7, Associativity.RIGHT ),
        OR              ( 1, Associativity.LEFT  ),
        PLUS            ( 7, Associativity.RIGHT ),
        SENTINEL        (-1, Associativity.NONE  ),
        SUBTRACT        ( 5, Associativity.LEFT  );

        private int precedence;
        private Associativity associativity;

        Operator(int precedence, Associativity associativity) {
            this.precedence = precedence;
            this.associativity = associativity;
        }

        public int getPrecedence() {
            return precedence;
        }

        public Associativity getAssociativity() {
            return associativity;
        }

        public boolean isBinary() {
            return (precedence > 0) && (precedence < 7);
        }

        public boolean isUnary() {
            return precedence == 7;
        }

        public int comparePrecedence(Operator other) {
            if (getPrecedence() < other.getPrecedence())
                return -1;
            else if (getPrecedence() > other.getPrecedence())
                return 1;
            else
                return 0;
        }

        public static Operator fromToken(Token t, OperatorContext context) {
            if (t == Token.AND)
                return AND;
            else if (t == Token.COMMA)
                return COMMA;
            else if (t == Token.DIVIDE)
                return DIVIDE;
            else if (t == Token.EQ)
                return EQ;
            else if (t == Token.GEQ)
                return GEQ;
            else if (t == Token.GT)
                return GT;
            else if (t == Token.LEQ)
                return LEQ;
            else if (t == Token.LT)
                return LT;
            else if (t == Token.MINUS)
                return (context == OperatorContext.UNARY) ? MINUS : SUBTRACT;
            else if (t == Token.MODULO)
                return MODULO;
            else if (t == Token.MULTIPLY)
                return MULTIPLY;
            else if (t == Token.NEQ)
                return NEQ;
            else if (t == Token.NOT)
                return NOT;
            else if (t == Token.OR)
                return OR;
            else if (t == Token.PLUS)
                return (context == OperatorContext.UNARY) ? PLUS : ADD;
            else
                return null;
        }

    }

    public static class Expression {

        private Operator operator;
        private List<Object> operands;

        Expression(Operator operator, Object operand) {
            this(operator, Arrays.asList(new Object[]{operand}));
        }

        Expression(Operator operator, List<Object> operands) {
            this.operator = operator;
            this.operands = operands;
        }

        public Operator getOperator() {
            return operator;
        }

        public List<Object> getOperands() {
            return Collections.unmodifiableList(operands);
        }

        /*
        boolean evaluate() {
            return false; // [TBD] - IMPLEMENT ME
        }
        */

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(operator);
            sb.append('(');
            boolean firstOperand = true;
            for (Object operand : operands) {
                if (!firstOperand)
                    sb.append(',');
                else
                    firstOperand = false;
                sb.append(operand);
            }
            sb.append(')');
            return sb.toString();
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

    public static class ParserStateException extends ParserException {
        static final long serialVersionUID = 0;
        ParserStateException(String message) {
            super(message);
        }
    }

    public static class ParserException extends RuntimeException {
        static final long serialVersionUID = 0;
        ParserException(String message) {
            super(message);
        }
    }

}
