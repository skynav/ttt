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
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.skynav.xml.helpers.XML;

public class Condition {

    public interface EvaluatorState {
        boolean hasBinding(String identifier);
        Object getBinding(String identifier);
        void setBinding(String identifier, Object value);
    }

    public interface EvaluatorFunction {
        Object apply(EvaluatorState state, List<Object> arguments);
    }

    public interface Evaluator {
        Object evaluate(ExpressionEvaluator ee, Expression e);
    }

    private Expression expression;

    private Condition(Expression expression) {
        assert expression != null;
        this.expression = expression;
    }

    public boolean evaluate(EvaluatorState state) {
        Object o = new ExpressionEvaluator(state).evaluate(expression);
        Class<?> operandClass = Boolean.class;
        if (checkCompatibleOperand(o, operandClass)) {
            o = convertCompatibleOperand(o, operandClass);
            if (o instanceof Boolean)
                return ((Boolean) o).booleanValue();
            else
                throw new IllegalStateException();
        } else
            throw new EvaluatorException("condition expression evaluates to non-boolean compatible value " + o);
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    public static Condition valueOf(String condition) throws ParserException {
        Parser p = new Parser();
        return p.parse(CharBuffer.wrap(condition.trim()));
    }

    public static EvaluatorState makeEvaluatorState(Map<String,Object> mediaParams, Map<String,Object> boundParams, Set<String> supportFeatures) {
        EvaluatorState state = new EvaluatorBindingState();
        state.setBinding("media", new MediaFunction(mediaParams));
        state.setBinding("parameter", new ParameterFunction(boundParams));
        state.setBinding("supports", new SupportsFunction(supportFeatures));
        return state;
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
                state.operators().push(Operator.SENTINEL);
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
            Token t0 = state.next();
            if (t0 == null) {
                state.error();
            } if (t0.isLiteral()) {
                state.consume();
                Token t1 = state.next();
                if (t1 == Token.OPEN) {
                    state.pushOperator(Operator.APPLY);
                    state.pushOperand(t0);
                    parseArguments();
                } else
                    state.pushOperand(t0);
            } else if (t0 == Token.OPEN) {
                state.consume();
                state.operators().push(Operator.SENTINEL);
                parseExpression();
                state.expect(Token.CLOSE);
                state.operators.pop();
            } else if (t0.isUnaryOp()) {
                state.consume();
                state.pushOperator(Operator.fromToken(t0, OperatorContext.UNARY));
                parsePrimary();
            } else
                state.error(t0);
        }

        private void parseArguments() {
            assert state.next() == Token.OPEN;
            state.consume();
            state.operators().push(Operator.SENTINEL);
            int na = 0;
            Token t;
            while ((t = state.next()) != Token.CLOSE) {
                if (na > 0) {
                    if (t == Token.COMMA)
                        state.consume();
                    else
                        state.error(Token.COMMA, t);
                }
                parseExpression();
                ++na;
            }
            state.expect(Token.CLOSE);
            state.operators.pop();
            List<Object> args = new java.util.ArrayList<Object>();
            while (na > 0) {
                args.add(state.operands.pop());
                --na;
            }
            state.operands.push(new Expression(Operator.GROUP, args));
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

        Stack<Operator> operators() {
            return operators;
        }

        Stack<Expression> operands() {
            return operands;
        }

        void setInput(CharBuffer cb, boolean skipWhitespace) {
            this.tokenizer = new Tokenizer(cb, skipWhitespace);
            operators().clear();
            operands().clear();
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

        void pushOperator(Operator op) {
            Operator top;
            while ((top = topOperator()) != null) {
                if (top.comparePrecedence(op) > 0)
                    popOperator();
                else
                    break;
            }
            operators().push(op);
        }

        Operator popOperator() {
            assert !operators().empty();
            Operator top = topOperator();
            if (top != null) {
                if (top.isBinary() || (top == Operator.APPLY)) {
                    assert operands().size() > 1;
                    Object o1 = operands().pop();
                    Object o0 = operands().pop();
                    pushOperand(new Expression(operators().pop(), Arrays.asList(new Object[]{ o0, o1 })));
                    return top;
                } else if (top.isUnary()) {
                    assert operands().size() > 0;
                    Object o0 = operands().pop();
                    pushOperand(new Expression(operators().pop(), o0));
                    return top;
                } else {
                    assert false;
                    throw new ParserStateException("unexpected operator: " + top);
                }
            } else
                throw new ParserStateException("operator stack is empty");
        }

        Operator topOperator() {
            return operators().empty() ? null : operators().peek();
        }

        void pushOperand(Token t) {
            assert t.isLiteral();
            pushOperand(new Expression(Operator.LITERAL, t));
        }

        void pushOperand(Expression e) {
            operands().push(e);
        }

        Expression topOperand() {
            return operands().empty() ? null : operands().peek();
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

        public Object getLiteralValue(EvaluatorState state) {
            assert isLiteral();
            if (type == Type.IDENT) {
                return state.getBinding(value);
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
            } else {
                throw new IllegalStateException();
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

        private void expect(Token token) {
            Token next = next();
            if ((next != null) && next.equals(token))
                consume();
            else
                error(token, next);
        }

        private Token next() {
            return getToken(cb, false, false);
        }

        private void consume() {
            getToken(cb, true, skipWhitespace);
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
        APPLY           ( 9, Associativity.NONE  ),
        COMMA           ( 0, Associativity.LEFT  ),
        DIVIDE          ( 6, Associativity.LEFT  ),
        EQ              ( 3, Associativity.LEFT  ),
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
        private List<Object> operands;          // (Expression|Token)*

        Expression(Operator operator, Object operand) {
            this(operator, Arrays.asList(new Object[]{operand}));
        }

        Expression(Operator operator, List<Object> operands) {
            assert operator != null;
            this.operator = operator;
            assert operands != null;
            this.operands = operands;
        }

        public Operator getOperator() {
            return operator;
        }

        public int getOperandCount() {
            return operands.size();
        }

        public Object getOperand(int index) {
            if ((index < 0) || (index >= operands.size()))
                throw new IllegalArgumentException();
            else
                return operands.get(index);
        }

        public List<Object> getOperands() {
            return Collections.unmodifiableList(operands);
        }

        public Object evaluate(ExpressionEvaluator ee) {
            return ee.evaluate(this);
        }

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

    private static class EvaluatorBindingState implements EvaluatorState {

        private Map<String,Object> bindings;

        public EvaluatorBindingState() {
            this.bindings = new java.util.HashMap<String,Object>();
        }

        public boolean hasBinding(String identifier) {
            return bindings.containsKey(identifier);
        }

        public Object getBinding(String identifier) {
            return bindings.get(identifier);
        }

        public void setBinding(String identifier, Object value) {
            bindings.put(identifier, value);
        }

    }

    private static class ExpressionEvaluator {

        private static final Map<Operator,Evaluator> evaluators;
        static {
            Map<Operator,Evaluator> m = new java.util.HashMap<Operator,Evaluator>();
            Evaluator e;
            e = new ApplyEvaluator();
            m.put(Operator.APPLY, e);
            e = new BinaryArithmeticEvaluator();
            m.put(Operator.ADD, e);
            m.put(Operator.SUBTRACT, e);
            m.put(Operator.MULTIPLY, e);
            m.put(Operator.DIVIDE, e);
            m.put(Operator.MODULO, e);
            e = new BinaryLogicEvaluator();
            m.put(Operator.AND, e);
            m.put(Operator.OR, e);
            e = new BinaryRelationEvaluator();
            m.put(Operator.EQ, e);
            m.put(Operator.GEQ, e);
            m.put(Operator.GT, e);
            m.put(Operator.LEQ, e);
            m.put(Operator.LT, e);
            m.put(Operator.NEQ, e);
            e = new GroupEvaluator();
            m.put(Operator.GROUP, e);
            e = new LiteralEvaluator();
            m.put(Operator.LITERAL, e);
            e = new UnaryLogicEvaluator();
            m.put(Operator.NOT, e);
            e = new UnaryArithmeticEvaluator();
            m.put(Operator.MINUS, e);
            m.put(Operator.PLUS, e);
            evaluators = Collections.unmodifiableMap(m);
        }

        EvaluatorState state;

        public ExpressionEvaluator(EvaluatorState state) {
            assert state != null;
            this.state = state;
        }

        private EvaluatorState getState() {
            return state;
        }

        public Object evaluate(Object o) {
            if (o == null) {
                throw new IllegalArgumentException();
            } else if (o instanceof Expression) {
                Expression e = (Expression) o;
                Operator operator = e.getOperator();
                Evaluator evaluator = evaluators.get(operator);
                if (evaluator != null)
                    return evaluator.evaluate(this, e);
                else
                    throw new IllegalArgumentException(o.toString());
            } else if (o instanceof Number) {
                return o;
            } else if (o instanceof String) {
                return o;
            } else if (o instanceof Boolean) {
                return o;
            } else {
                throw new IllegalArgumentException();
            }
        }

    }

    private static class ApplyEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                final int minCount = 1;
                final int maxCount = -1;
                if (checkOperandCount(e, minCount, maxCount)) {
                    Object o0 = ee.evaluate(e.getOperand(0));
                    if (o0 instanceof EvaluatorFunction) {
                        EvaluatorFunction f = (EvaluatorFunction) o0;
                        Object o1 = e.getOperand(1);
                        if (o1 instanceof Expression) {
                            Expression eArguments = (Expression) o1;
                            if (eArguments.getOperator() == Operator.GROUP) {
                                List<Object> arguments = new java.util.ArrayList<Object>();
                                for (Object operand : eArguments.getOperands()) {
                                    arguments.add(ee.evaluate(operand));
                                }
                                return f.apply(ee.getState(), arguments);
                            } else
                                throw new IllegalStateException();
                        } else
                            throw new IllegalStateException();
                    } else
                        throw new IncompatibleOperandException(e, o0, EvaluatorFunction.class);
                } else
                    throw new BadOperandCountException(e, minCount, maxCount);
            } else
                throw new BadOperatorException(e, "apply");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.APPLY)
                return true;
            else
                return false;
        }

    }

    private static class BinaryArithmeticEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                final int minCount = 2;
                final int maxCount = 2;
                if (checkOperandCount(e, minCount, maxCount)) {
                    Class<?> operandClass = Number.class;
                    Object o0 = ee.evaluate(e.getOperand(0));
                    if (!checkCompatibleOperand(o0, operandClass))
                        throw new IncompatibleOperandException(e, o0, operandClass);
                    else
                        o0 = convertCompatibleOperand(o0, operandClass);
                    Object o1 = ee.evaluate(e.getOperand(1));
                    if (!checkCompatibleOperand(o1, operandClass))
                        throw new IncompatibleOperandException(e, o1, operandClass);
                    else
                        o1 = convertCompatibleOperand(o1, operandClass);
                    return evaluate(ee.getState(), e.getOperator(), o0, o1);
                } else
                    throw new BadOperandCountException(e, minCount, maxCount);
            } else
                throw new BadOperatorException(e, "binary arithmetic");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.ADD)
                return true;
            else if (operator == Operator.SUBTRACT)
                return true;
            else if (operator == Operator.MULTIPLY)
                return true;
            else if (operator == Operator.DIVIDE)
                return true;
            else if (operator == Operator.MODULO)
                return true;
            else
                return false;
        }

        private Object evaluate(EvaluatorState state, Operator operator, Object o0, Object o1) {
            assert o0 instanceof Number;
            double d0 = ((Number) o0).doubleValue();
            assert o1 instanceof Number;
            double d1 = ((Number) o1).doubleValue();
            if (operator == Operator.ADD) {
                return (Double) (d0 + d1);
            } else if (operator == Operator.SUBTRACT) {
                return (Double) (d0 - d1);
            } else if (operator == Operator.MULTIPLY) {
                return (Double) (d0 * d1);
            } else if (operator == Operator.DIVIDE) {
                return (Double) (d0 / d1);
            } else if (operator == Operator.MODULO) {
                return (Double) (d0 % d1);
            } else
                throw new IllegalStateException();
        }

    }

    private static class BinaryLogicEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                final int minCount = 2;
                final int maxCount = 2;
                if (checkOperandCount(e, minCount, maxCount)) {
                    Class<?> operandClass = Boolean.class;
                    Object o0 = ee.evaluate(e.getOperand(0));
                    if (!checkCompatibleOperand(o0, operandClass))
                        throw new IncompatibleOperandException(e, o0, operandClass);
                    else
                        o0 = convertCompatibleOperand(o0, operandClass);
                    Object o1 = ee.evaluate(e.getOperand(1));
                    if (!checkCompatibleOperand(o1, operandClass))
                        throw new IncompatibleOperandException(e, o1, operandClass);
                    else
                        o1 = convertCompatibleOperand(o1, operandClass);
                    return evaluate(ee.getState(), e.getOperator(), o0, o1);
                } else
                    throw new BadOperandCountException(e, minCount, maxCount);
            } else
                throw new BadOperatorException(e, "binary logic");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.AND)
                return true;
            else if (operator == Operator.OR)
                return true;
            else
                return false;
        }

        private Object evaluate(EvaluatorState state, Operator operator, Object o0, Object o1) {
            assert o0 instanceof Boolean;
            boolean b0 = ((Boolean) o0).booleanValue();
            assert o1 instanceof Boolean;
            boolean b1 = ((Boolean) o1).booleanValue();
            if (operator == Operator.AND) {
                return (Boolean) (b0 && b1);
            } else if (operator == Operator.OR) {
                return (Boolean) (b0 || b1);
            } else
                throw new IllegalStateException();
        }

    }

    private static class BinaryRelationEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                Operator operator = e.getOperator();
                final int minCount = 2;
                final int maxCount = 2;
                if (checkOperandCount(e, minCount, maxCount)) {
                    Object o0 = ee.evaluate(e.getOperand(0));
                    Object o1 = ee.evaluate(e.getOperand(1));
                    if (o0 instanceof Number) {
                        Class<?> operandClass = Number.class;
                        if (o1 instanceof Number) {
                            return evaluate(ee.getState(), e, operator, (Number) o0, (Number) o1);
                        } else if (checkCompatibleOperand(o1, operandClass)) {
                            return evaluate(ee.getState(), e, operator, (Number) o0, (Number) convertCompatibleOperand(o1, operandClass));
                        } else {
                            throw new IncompatibleOperandException(e, o1, operandClass);
                        }
                    } else if (o0 instanceof String) {
                        Class<?> operandClass = String.class;
                        if (o1 instanceof String) {
                            return evaluate(ee.getState(), e, operator, (String) o0, (String) o1);
                        } else if (checkCompatibleOperand(o1, operandClass)) {
                            return evaluate(ee.getState(), e, operator, (String) o0, (String) convertCompatibleOperand(o1, operandClass));
                        } else {
                            throw new IncompatibleOperandException(e, o1, operandClass);
                        }
                    } else if (o0 instanceof Boolean) {
                        Class<?> operandClass = Boolean.class;
                        if (o1 instanceof Boolean) {
                            return evaluate(ee.getState(), e, operator, (Boolean) o0, (Boolean) o1);
                        } else if (checkCompatibleOperand(o1, operandClass)) {
                            return evaluate(ee.getState(), e, operator, (Boolean) o0, (Boolean) convertCompatibleOperand(o1, operandClass));
                        } else {
                            throw new IncompatibleOperandException(e, o1, operandClass);
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                } else
                    throw new BadOperandCountException(e, minCount, maxCount);
            } else
                throw new BadOperatorException(e, "binary arithmetic");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.EQ)
                return true;
            else if (operator == Operator.GEQ)
                return true;
            else if (operator == Operator.GT)
                return true;
            else if (operator == Operator.LEQ)
                return true;
            else if (operator == Operator.LT)
                return true;
            else if (operator == Operator.NEQ)
                return true;
            else
                return false;
        }

        private Object evaluate(EvaluatorState state, Expression e, Operator operator, Number n0, Number n1) {
            Double d0 = Double.valueOf(n0.doubleValue());
            Double d1 = Double.valueOf(n1.doubleValue());
            int d = d0.compareTo(d1);
            if (operator == Operator.EQ) {
                return (Boolean) (d == 0);
            } else if (operator == Operator.GEQ) {
                return (Boolean) (d >= 0);
            } else if (operator == Operator.GT) {
                return (Boolean) (d > 0);
            } else if (operator == Operator.LEQ) {
                return (Boolean) (d <= 0);
            } else if (operator == Operator.LT) {
                return (Boolean) (d < 0);
            } else if (operator == Operator.NEQ) {
                return (Boolean) (d != 0);
            } else
                throw new IllegalStateException();
        }

        private Object evaluate(EvaluatorState state, Expression e, Operator operator, String s0, String s1) {
            int d = s0.compareTo(s1);
            if (operator == Operator.EQ) {
                return (Boolean) (d == 0);
            } else if (operator == Operator.GEQ) {
                return (Boolean) (d >= 0);
            } else if (operator == Operator.GT) {
                return (Boolean) (d > 0);
            } else if (operator == Operator.LEQ) {
                return (Boolean) (d <= 0);
            } else if (operator == Operator.LT) {
                return (Boolean) (d < 0);
            } else if (operator == Operator.NEQ) {
                return (Boolean) (d != 0);
            } else
                throw new IllegalStateException();
        }

        private Object evaluate(EvaluatorState state, Expression e, Operator operator, Boolean b0, Boolean b1) {
            if (operator == Operator.EQ) {
                return (Boolean) b0.equals(b1);
            } else if (operator == Operator.GEQ) {
                throw new IncompatibleOperatorException(e, operator, Boolean.class);
            } else if (operator == Operator.GT) {
                throw new IncompatibleOperatorException(e, operator, Boolean.class);
            } else if (operator == Operator.LEQ) {
                throw new IncompatibleOperatorException(e, operator, Boolean.class);
            } else if (operator == Operator.LT) {
                throw new IncompatibleOperatorException(e, operator, Boolean.class);
            } else if (operator == Operator.NEQ) {
                return (Boolean) !b0.equals(b1);
            } else
                throw new IllegalStateException();
        }

    }

    private static class GroupEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                final int count = 1;
                if (checkOperandCount(e, count)) {
                    return ee.evaluate(e.getOperand(0));
                } else
                    throw new BadOperandCountException(e, count, count);
            } else
                throw new BadOperatorException(e, "group");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.GROUP)
                return true;
            else
                return false;
        }

    }

    private static class LiteralEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                final int count = 1;
                if (checkOperandCount(e, count)) {
                    Object o0 = e.getOperand(0);
                    if (!(o0 instanceof Token))
                        throw new IncompatibleOperandException(e, o0, Token.class);
                    else
                        return evaluate(ee.getState(), e.getOperator(), o0);
                } else
                    throw new BadOperandCountException(e, count, count);
            } else
                throw new BadOperatorException(e, "literal");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.LITERAL)
                return true;
            else
                return false;
        }

        private Object evaluate(EvaluatorState state, Operator operator, Object o0) {
            assert o0 instanceof Token;
            Token t = (Token) o0;
            if (operator == Operator.LITERAL) {
                assert t.isLiteral();
                return t.getLiteralValue(state);
            } else
                throw new IllegalStateException();
        }

    }

    private static class UnaryArithmeticEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                final int minCount = 1;
                final int maxCount = 1;
                if (checkOperandCount(e, minCount, maxCount)) {
                    Class<?> operandClass = Number.class;
                    Object o0 = ee.evaluate(e.getOperand(0));
                    if (!checkCompatibleOperand(o0, operandClass))
                        throw new IncompatibleOperandException(e, o0, operandClass);
                    else
                        o0 = convertCompatibleOperand(o0, operandClass);
                    return evaluate(ee.getState(), e.getOperator(), o0);
                } else
                    throw new BadOperandCountException(e, minCount, maxCount);
            } else
                throw new BadOperatorException(e, "unary arithmetic");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.MINUS)
                return true;
            else if (operator == Operator.PLUS)
                return true;
            else
                return false;
        }

        private Object evaluate(EvaluatorState state, Operator operator, Object o0) {
            assert o0 instanceof Number;
            double d0 = ((Number) o0).doubleValue();
            if (operator == Operator.MINUS) {
                return (Double) (-d0);
            } else if (operator == Operator.PLUS) {
                return (Double) (+d0);
            } else
                throw new IllegalStateException();
        }

    }

    private static class UnaryLogicEvaluator implements Evaluator {

        public Object evaluate(ExpressionEvaluator ee, Expression e) {
            if (checkOperator(e)) {
                final int minCount = 1;
                final int maxCount = 1;
                if (checkOperandCount(e, minCount, maxCount)) {
                    Class<?> operandClass = Boolean.class;
                    Object o0 = ee.evaluate(e.getOperand(0));
                    if (!checkCompatibleOperand(o0, operandClass))
                        throw new IncompatibleOperandException(e, o0, operandClass);
                    else
                        o0 = convertCompatibleOperand(o0, operandClass);
                    return evaluate(ee.getState(), e.getOperator(), o0);
                } else
                    throw new BadOperandCountException(e, minCount, maxCount);
            } else
                throw new BadOperatorException(e, "unary logic");
        }

        private boolean checkOperator(Expression e) {
            Operator operator = e.getOperator();
            if (operator == Operator.NOT)
                return true;
            else
                return false;
        }

        private Object evaluate(EvaluatorState state, Operator operator, Object o0) {
            assert o0 instanceof Boolean;
            boolean b0 = ((Boolean) o0).booleanValue();
            if (operator == Operator.NOT) {
                return (Boolean) (!b0);
            } else
                throw new IllegalStateException();
        }

    }

    private static class MediaFunction implements EvaluatorFunction {
        private Map<String,Object> parameters;
        public MediaFunction(Map<String,Object> parameters) {
            assert parameters != null;
            this.parameters = parameters;
        }
        public Object apply(EvaluatorState state, List<Object> arguments) {
            if (arguments.size() < 1)
                throw new BadOperandCountException(arguments.size(), 1, 1);
            else {
                Class<?> operandClass = String.class;
                Object o0 = arguments.get(0);
                if (checkCompatibleOperand(o0, operandClass)) {
                    o0 = convertCompatibleOperand(o0, operandClass);
                    assert o0 instanceof String;
                    String query = (String) o0;
                    try {
                        MediaQuery mq = MediaQuery.valueOf(query);
                        return mq.evaluate(parameters);
                    } catch (MediaQuery.ParserException e) {
                        throw new BuiltinFunctionException(e);
                    }
                } else
                    throw new IncompatibleOperandException(o0, operandClass);
            }
        }
    }

    private static class ParameterFunction implements EvaluatorFunction {
        private Map<String,Object> parameters;
        public ParameterFunction(Map<String,Object> parameters) {
            assert parameters != null;
            this.parameters = parameters;
        }
        public Object apply(EvaluatorState state, List<Object> arguments) {
            if (arguments.size() < 1)
                throw new BadOperandCountException(arguments.size(), 1, 1);
            else {
                Class<?> operandClass = String.class;
                Object o0 = arguments.get(0);
                if (checkCompatibleOperand(o0, operandClass)) {
                    o0 = convertCompatibleOperand(o0, operandClass);
                    assert o0 instanceof String;
                    String name = (String) o0;
                    if (parameters.containsKey(name))
                        return parameters.get(name);
                    else
                        throw new UnknownParameterException(name);
                } else
                    throw new IncompatibleOperandException(o0, operandClass);
            }
        }
    }

    private static class SupportsFunction implements EvaluatorFunction {
        private Set<String> features;
        public SupportsFunction(Set<String> features) {
            assert features != null;
            this.features = features;
        }
        public Object apply(EvaluatorState state, List<Object> arguments) {
            if (arguments.size() < 1)
                throw new BadOperandCountException(arguments.size(), 1, 1);
            else {
                Class<?> operandClass = String.class;
                Object o0 = arguments.get(0);
                if (checkCompatibleOperand(o0, operandClass)) {
                    o0 = convertCompatibleOperand(o0, operandClass);
                    assert o0 instanceof String;
                    String designator = (String) o0;
                    return Boolean.valueOf(features.contains(designator));
                } else
                    throw new IncompatibleOperandException(o0, operandClass);
            }
        }
    }

    private static boolean checkOperandCount(Expression e, int count) {
        return checkOperandCount(e, count, count);
    }

    private static boolean checkOperandCount(Expression e, int minCount, int maxCount) {
        List<Object> operands = e.getOperands();
        if (operands.size() < minCount) {
            return false;
        } else if ((maxCount >= 0) && (operands.size() > maxCount)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkCompatibleOperand(Object o, Class<?> compatibleClass) {
        if (o instanceof Number) {
            if (compatibleClass == Number.class)
                return true;
            else if (compatibleClass == String.class)
                return true;
            else if (compatibleClass == Boolean.class)
                return false;
            else
                throw new IllegalArgumentException();
        } else if (o instanceof String) {
            if (compatibleClass == Number.class) {
                try {
                    Double d = Double.valueOf((String) o);
                    return !d.isNaN();
                } catch (NumberFormatException e) {
                    return false;
                }
            } else if (compatibleClass == String.class) {
                return true;
            } else if (compatibleClass == Boolean.class) {
                String s = (String) o;
                if (s.equals("true"))
                    return true;
                else if (s.equals("false"))
                    return true;
                else
                    return false;
            } else
                throw new IllegalArgumentException();
        } else if (o instanceof Boolean) {
            if (compatibleClass == Number.class)
                return false;
            else if (compatibleClass == String.class)
                return true;
            else if (compatibleClass == Boolean.class)
                return true;
            else
                throw new IllegalArgumentException();
        } else
            throw new IllegalStateException();
    }

    public static Object convertCompatibleOperand(Object o, Class<?> compatibleClass) {
        if (o == null)
            throw new IllegalStateException();
        else if (o instanceof Number)
            return convertCompatibleOperand((Number) o, compatibleClass);
        else if (o instanceof String)
            return convertCompatibleOperand((String) o, compatibleClass);
        else if (o instanceof Boolean)
            return convertCompatibleOperand((Boolean) o, compatibleClass);
        else
            throw new IllegalStateException();
    }

    private static Object convertCompatibleOperand(Number n, Class<?> compatibleClass) {
        if (n == null) {
            throw new IllegalStateException();
        } else if (compatibleClass == Number.class) {
            return n;
        } else if (compatibleClass == String.class) {
            return n.toString();
        } else {
            throw new IllegalStateException();
        }
    }

    private static Object convertCompatibleOperand(String s, Class<?> compatibleClass) {
        if (s == null) {
            throw new IllegalStateException();
        } else if (compatibleClass == Number.class) {
            try {
                return Double.valueOf(s);
            } catch (NumberFormatException e) {
                throw new IllegalStateException();
            }
        } else if (compatibleClass == String.class) {
            return s;
        } else if (s.equals("true")) {
            return Boolean.TRUE;
        } else if (s.equals("false")) {
            return Boolean.FALSE;
        } else {
            throw new IllegalStateException();
        }
    }

    private static Object convertCompatibleOperand(Boolean b, Class<?> compatibleClass) {
        if (b == null) {
            throw new IllegalStateException();
        } else if (compatibleClass == String.class) {
            return b.toString();
        } else if (compatibleClass == Boolean.class) {
            return b;
        } else {
            throw new IllegalStateException();
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

    public static class ParserException extends RuntimeException {
        static final long serialVersionUID = 0;
        public ParserException(String message) {
            super(message);
        }
    }

    public static class ParserStateException extends ParserException {
        static final long serialVersionUID = 0;
        ParserStateException(String message) {
            super(message);
        }
    }

    public static class EvaluatorException extends RuntimeException {
        static final long serialVersionUID = 0;
        public EvaluatorException(String message) {
            super(message);
        }
    }

    public static class BadOperatorException extends EvaluatorException {
        static final long serialVersionUID = 0;
        public BadOperatorException(Expression e, String expectation) {
            this(e.getOperator(), expectation);
        }
        public BadOperatorException(Operator operator, String expectation) {
            super("expected " + expectation + ", got" + operator);
        }
    }

    public static class BadOperandCountException extends EvaluatorException {
        static final long serialVersionUID = 0;
        public BadOperandCountException(Expression e, int minCount, int maxCount) {
            this(e.getOperandCount(), minCount, maxCount);
        }
        public BadOperandCountException(int count, int minCount, int maxCount) {
            super("expected from " + minCount + " to " + (maxCount >= 0 ? Integer.toString(maxCount) : "...") + " operands, got" + count);
        }
    }

    public static class IncompatibleOperatorException extends EvaluatorException {
        static final long serialVersionUID = 0;
        public IncompatibleOperatorException(Expression e, Operator operator, Class<?> operandClass) {
            super("operator " + operator + " incompabitle with " + operandClass);
        }
    }

    public static class IncompatibleOperandException extends EvaluatorException {
        static final long serialVersionUID = 0;
        public IncompatibleOperandException(Object operand, Class<?> operandClass) {
            this(null, operand, operandClass);
        }
        public IncompatibleOperandException(Expression e, Object operand, Class<?> operandClass) {
            super("operand " + operand + " incompabitle with " + operandClass);
        }
    }

    public static class BuiltinFunctionException extends EvaluatorException {
        static final long serialVersionUID = 0;
        public BuiltinFunctionException(Exception e) {
            this(e.getMessage());
        }
        public BuiltinFunctionException(String message) {
            super(message);
        }
    }

    public static class UnknownParameterException extends BuiltinFunctionException {
        static final long serialVersionUID = 0;
        public UnknownParameterException(String name) {
            super("unknown parameter '" + name + "'");
        }
    }

}
