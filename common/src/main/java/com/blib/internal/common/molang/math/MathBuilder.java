package com.blib.internal.common.molang.math;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.internal.common.exception.AzureLibException;
import com.blib.internal.common.molang.math.functions.Function;
import com.blib.internal.common.molang.math.functions.classic.ACos;
import com.blib.internal.common.molang.math.functions.classic.ASin;
import com.blib.internal.common.molang.math.functions.classic.ATan;
import com.blib.internal.common.molang.math.functions.classic.ATan2;
import com.blib.internal.common.molang.math.functions.classic.Abs;
import com.blib.internal.common.molang.math.functions.classic.Cos;
import com.blib.internal.common.molang.math.functions.classic.Exp;
import com.blib.internal.common.molang.math.functions.classic.Ln;
import com.blib.internal.common.molang.math.functions.classic.Mod;
import com.blib.internal.common.molang.math.functions.classic.Pow;
import com.blib.internal.common.molang.math.functions.classic.Sin;
import com.blib.internal.common.molang.math.functions.classic.Sqrt;
import com.blib.internal.common.molang.math.functions.easing.back.EaseInBack;
import com.blib.internal.common.molang.math.functions.easing.back.EaseInOutBack;
import com.blib.internal.common.molang.math.functions.easing.back.EaseOutBack;
import com.blib.internal.common.molang.math.functions.easing.bounce.EaseInBounce;
import com.blib.internal.common.molang.math.functions.easing.bounce.EaseInOutBounce;
import com.blib.internal.common.molang.math.functions.easing.bounce.EaseOutBounce;
import com.blib.internal.common.molang.math.functions.easing.circ.EaseInCirc;
import com.blib.internal.common.molang.math.functions.easing.circ.EaseInOutCirc;
import com.blib.internal.common.molang.math.functions.easing.circ.EaseOutCirc;
import com.blib.internal.common.molang.math.functions.easing.cubic.EaseInCubic;
import com.blib.internal.common.molang.math.functions.easing.cubic.EaseInOutCubic;
import com.blib.internal.common.molang.math.functions.easing.cubic.EaseOutCubic;
import com.blib.internal.common.molang.math.functions.easing.elastic.EaseInElastic;
import com.blib.internal.common.molang.math.functions.easing.elastic.EaseInOutElastic;
import com.blib.internal.common.molang.math.functions.easing.elastic.EaseOutElastic;
import com.blib.internal.common.molang.math.functions.easing.expo.EaseInExpo;
import com.blib.internal.common.molang.math.functions.easing.expo.EaseInOutExpo;
import com.blib.internal.common.molang.math.functions.easing.expo.EaseOutExpo;
import com.blib.internal.common.molang.math.functions.easing.quad.EaseInOutQuad;
import com.blib.internal.common.molang.math.functions.easing.quad.EaseInQuad;
import com.blib.internal.common.molang.math.functions.easing.quad.EaseOutQuad;
import com.blib.internal.common.molang.math.functions.easing.quart.EaseInOutQuart;
import com.blib.internal.common.molang.math.functions.easing.quart.EaseInQuart;
import com.blib.internal.common.molang.math.functions.easing.quart.EaseOutQuart;
import com.blib.internal.common.molang.math.functions.easing.quint.EaseInOutQuint;
import com.blib.internal.common.molang.math.functions.easing.quint.EaseInQuint;
import com.blib.internal.common.molang.math.functions.easing.quint.EaseOutQuint;
import com.blib.internal.common.molang.math.functions.easing.sine.EaseInOutSine;
import com.blib.internal.common.molang.math.functions.easing.sine.EaseInSine;
import com.blib.internal.common.molang.math.functions.easing.sine.EaseOutSine;
import com.blib.internal.common.molang.math.functions.limit.Clamp;
import com.blib.internal.common.molang.math.functions.limit.Max;
import com.blib.internal.common.molang.math.functions.limit.Min;
import com.blib.internal.common.molang.math.functions.rounding.Ceil;
import com.blib.internal.common.molang.math.functions.rounding.Floor;
import com.blib.internal.common.molang.math.functions.rounding.Round;
import com.blib.internal.common.molang.math.functions.rounding.Trunc;
import com.blib.internal.common.molang.math.functions.utility.CopySign;
import com.blib.internal.common.molang.math.functions.utility.DieRoll;
import com.blib.internal.common.molang.math.functions.utility.DieRollInteger;
import com.blib.internal.common.molang.math.functions.utility.HermiteBlend;
import com.blib.internal.common.molang.math.functions.utility.InverseLerp;
import com.blib.internal.common.molang.math.functions.utility.Lerp;
import com.blib.internal.common.molang.math.functions.utility.LerpRotate;
import com.blib.internal.common.molang.math.functions.utility.Random;
import com.blib.internal.common.molang.math.functions.utility.RandomInteger;
import com.blib.internal.common.molang.math.functions.utility.Sign;
import com.blib.mod.BLib;

public class MathBuilder {

    public Map<String, Variable> variables = new HashMap<String, Variable>();

    public Map<String, Class<? extends Function>> functions = new HashMap<>();

    public MathBuilder() {
        /* Some default values */
        this.register(new Variable("PI", Math.PI));
        this.register(new Variable("E", Math.E));

        /* Rounding functions */
        this.functions.put("floor", Floor.class);
        this.functions.put("round", Round.class);
        this.functions.put("ceil", Ceil.class);
        this.functions.put("trunc", Trunc.class);

        /* Selection and limit functions */
        this.functions.put("clamp", Clamp.class);
        this.functions.put("max", Max.class);
        this.functions.put("min", Min.class);

        /* Classical functions */
        this.functions.put("abs", Abs.class);
        this.functions.put("acos", ACos.class);
        this.functions.put("asin", ASin.class);
        this.functions.put("atan", ATan.class);
        this.functions.put("atan2", ATan2.class);
        this.functions.put("cos", Cos.class);
        this.functions.put("sin", Sin.class);
        this.functions.put("exp", Exp.class);
        this.functions.put("ln", Ln.class);
        this.functions.put("sqrt", Sqrt.class);
        this.functions.put("mod", Mod.class);
        this.functions.put("pow", Pow.class);

        /* Utility functions */
        this.functions.put("lerp", Lerp.class);
        this.functions.put("lerprotate", LerpRotate.class);
        this.functions.put("hermite_blend", HermiteBlend.class);
        this.functions.put("die_roll", DieRoll.class);
        this.functions.put("die_roll_integer", DieRollInteger.class);
        this.functions.put("random", Random.class);
        this.functions.put("random_integer", RandomInteger.class);
        this.functions.put("copy_sign", CopySign.class);
        this.functions.put("sign", Sign.class);
        this.functions.put("inverse_lerp", InverseLerp.class);

        /* Quadratic easing functions */
        this.functions.put("ease_in_quad", EaseInQuad.class);
        this.functions.put("ease_out_quad", EaseOutQuad.class);
        this.functions.put("ease_in_out_quad", EaseInOutQuad.class);

        /* Cubic easing functions */
        this.functions.put("ease_in_cubic", EaseInCubic.class);
        this.functions.put("ease_out_cubic", EaseOutCubic.class);
        this.functions.put("ease_in_out_cubic", EaseInOutCubic.class);

        /* Quartic easing functions */
        this.functions.put("ease_in_quart", EaseInQuart.class);
        this.functions.put("ease_out_quart", EaseOutQuart.class);
        this.functions.put("ease_in_out_quart", EaseInOutQuart.class);

        /* Quintic easing functions */
        this.functions.put("ease_in_quint", EaseInQuint.class);
        this.functions.put("ease_out_quint", EaseOutQuint.class);
        this.functions.put("ease_in_out_quint", EaseInOutQuint.class);

        /* Sine easing functions */
        this.functions.put("ease_in_sine", EaseInSine.class);
        this.functions.put("ease_out_sine", EaseOutSine.class);
        this.functions.put("ease_in_out_sine", EaseInOutSine.class);

        /* Exponential easing functions */
        this.functions.put("ease_in_expo", EaseInExpo.class);
        this.functions.put("ease_out_expo", EaseOutExpo.class);
        this.functions.put("ease_in_out_expo", EaseInOutExpo.class);

        /* Circular easing functions */
        this.functions.put("ease_in_circ", EaseInCirc.class);
        this.functions.put("ease_out_circ", EaseOutCirc.class);
        this.functions.put("ease_in_out_circ", EaseInOutCirc.class);

        /* Back easing functions */
        this.functions.put("ease_in_back", EaseInBack.class);
        this.functions.put("ease_out_back", EaseOutBack.class);
        this.functions.put("ease_in_out_back", EaseInOutBack.class);

        /* Elastic easing functions */
        this.functions.put("ease_in_elastic", EaseInElastic.class);
        this.functions.put("ease_out_elastic", EaseOutElastic.class);
        this.functions.put("ease_in_out_elastic", EaseInOutElastic.class);

        /* Bounce easing functions */
        this.functions.put("ease_in_bounce", EaseInBounce.class);
        this.functions.put("ease_out_bounce", EaseOutBounce.class);
        this.functions.put("ease_in_out_bounce", EaseInOutBounce.class);
    }

    public void register(Variable variable) {
        this.variables.put(variable.getName(), variable);
    }

    public IValue parse(String expression) throws Exception {
        return this.parseSymbols(this.breakdownChars(this.breakdown(expression)));
    }

    public String[] breakdown(String expression) throws AzureLibException {
        /* If given string has illegal characters, then it can't be parsed */
        if (!expression.matches("^[\\w\\d\\s_+-/*%^&|<>=!?:.,()]+$")) {
            throw new AzureLibException("Given expression '" + expression + "' contains illegal characters!");
        }

        /* Remove all spaces, and leading and trailing parenthesis */
        expression = expression.replaceAll("\\s+", "");

        String[] chars = expression.split("(?!^)");

        int left = 0;
        int right = 0;

        for (String s : chars) {
            if (s.equals("(")) {
                left++;
            } else if (s.equals(")")) {
                right++;
            }
        }

        /* Amount of left and right brackets should be the same */
        if (left != right) {
            throw new AzureLibException(
                "Given expression '" + expression
                    + "' has more uneven amount of parenthesis, there are " + left + " open and " + right + " closed!"
            );
        }

        return chars;
    }

    public List<Object> breakdownChars(String[] chars) {
        List<Object> symbols = new ArrayList<>();
        String buffer = "";
        int len = chars.length;

        for (int i = 0; i < len; i++) {
            String s = chars[i];
            boolean longOperator = i > 0 && this.isOperator(chars[i - 1] + s);

            if (this.isOperator(s) || longOperator || s.equals(",")) {
                /*
                 * Taking care of a special case of using minus sign to invert the positive value
                 */
                if (s.equals("-")) {
                    int size = symbols.size();

                    boolean isFirst = size == 0 && buffer.isEmpty();
                    boolean isOperatorBehind = size > 0
                        && (this.isOperator(symbols.get(size - 1)) || symbols.get(size - 1).equals(","))
                        && buffer.isEmpty();

                    if (isFirst || isOperatorBehind) {
                        buffer += s;

                        continue;
                    }
                }

                if (longOperator) {
                    s = chars[i - 1] + s;
                    buffer = buffer.substring(0, buffer.length() - 1);
                }

                /* Push buffer and operator */
                if (!buffer.isEmpty()) {
                    symbols.add(buffer);
                    buffer = "";
                }

                symbols.add(s);
            } else if (s.equals("(")) {
                /* Push a list of symbols */
                if (!buffer.isEmpty()) {
                    symbols.add(buffer);
                    buffer = "";
                }

                int counter = 1;

                for (int j = i + 1; j < len; j++) {
                    String c = chars[j];

                    if (c.equals("(")) {
                        counter++;
                    } else if (c.equals(")")) {
                        counter--;
                    }

                    if (counter == 0) {
                        symbols.add(this.breakdownChars(buffer.split("(?!^)")));

                        i = j;
                        buffer = "";

                        break;
                    } else {
                        buffer += c;
                    }
                }
            } else {
                /* Accumulate the buffer */
                buffer += s;
            }
        }

        if (!buffer.isEmpty()) {
            symbols.add(buffer);
        }

        return symbols;
    }

    public IValue parseSymbols(List<Object> symbols) throws Exception {
        IValue ternary = this.tryTernary(symbols);

        if (ternary != null) {
            return ternary;
        }

        int size = symbols.size();

        /* Constant, variable or group (parenthesis) */
        if (size == 1) {
            return this.valueFromObject(symbols.get(0));
        }

        /* Function */
        if (size == 2) {
            Object first = symbols.get(0);
            Object second = symbols.get(1);

            if ((this.isVariable(first) || first.equals("-")) && second instanceof List) {
                return this.createFunction((String) first, (List<Object>) second);
            }
        }

        /* Any other math expression */
        int lastOp = this.seekLastOperator(symbols);
        int op = lastOp;

        while (op != -1) {
            int leftOp = this.seekLastOperator(symbols, op - 1);

            if (leftOp != -1) {
                Operation left = this.operationForOperator((String) symbols.get(leftOp));
                Operation right = this.operationForOperator((String) symbols.get(op));

                if (right.value > left.value) {
                    IValue leftValue = this.parseSymbols(symbols.subList(0, leftOp));
                    IValue rightValue = this.parseSymbols(symbols.subList(leftOp + 1, size));

                    return new Operator(left, leftValue, rightValue);
                } else if (left.value > right.value) {
                    Operation initial = this.operationForOperator((String) symbols.get(lastOp));

                    if (initial.value < left.value) {
                        IValue leftValue = this.parseSymbols(symbols.subList(0, lastOp));
                        IValue rightValue = this.parseSymbols(symbols.subList(lastOp + 1, size));

                        return new Operator(initial, leftValue, rightValue);
                    }

                    IValue leftValue = this.parseSymbols(symbols.subList(0, op));
                    IValue rightValue = this.parseSymbols(symbols.subList(op + 1, size));

                    return new Operator(right, leftValue, rightValue);
                }
            }

            op = leftOp;
        }

        Operation operation = this.operationForOperator((String) symbols.get(lastOp));

        return new Operator(
            operation,
            this.parseSymbols(symbols.subList(0, lastOp)),
            this.parseSymbols(symbols.subList(lastOp + 1, size))
        );
    }

    protected int seekLastOperator(List<Object> symbols) {
        return this.seekLastOperator(symbols, symbols.size() - 1);
    }

    protected int seekLastOperator(List<Object> symbols, int offset) {
        for (int i = offset; i >= 0; i--) {
            Object o = symbols.get(i);

            if (this.isOperator(o)) {
                return i;
            }
        }

        return -1;
    }

    protected int seekFirstOperator(List<Object> symbols) {
        return this.seekFirstOperator(symbols, 0);
    }

    protected int seekFirstOperator(List<Object> symbols, int offset) {
        for (int i = offset, size = symbols.size(); i < size; i++) {
            Object o = symbols.get(i);

            if (this.isOperator(o)) {
                return i;
            }
        }

        return -1;
    }

    protected IValue tryTernary(List<Object> symbols) throws Exception {
        int question = -1;
        int questions = 0;
        int colon = -1;
        int colons = 0;
        int size = symbols.size();

        for (int i = 0; i < size; i++) {
            Object object = symbols.get(i);

            if (object instanceof String) {
                if (object.equals("?")) {
                    if (question == -1) {
                        question = i;
                    }

                    questions++;
                } else if (object.equals(":")) {
                    if (colons + 1 == questions && colon == -1) {
                        colon = i;
                    }

                    colons++;
                }
            }
        }

        if (questions == colons && question > 0 && question + 1 < colon && colon < size - 1) {
            return new Ternary(
                this.parseSymbols(symbols.subList(0, question)),
                this.parseSymbols(symbols.subList(question + 1, colon)),
                this.parseSymbols(symbols.subList(colon + 1, size))
            );
        }

        return null;
    }

    protected IValue createFunction(String first, List<Object> args) throws Exception {
        /* Handle special cases with negation */
        if (first.equals("!")) {
            return new Negate(this.parseSymbols(args));
        }

        if (first.startsWith("!") && first.length() > 1) {
            return new Negate(this.createFunction(first.substring(1), args));
        }

        /* Handle inversion of the value */
        if (first.equals("-")) {
            return new Negative(new Group(this.parseSymbols(args)));
        }

        if (first.startsWith("-") && first.length() > 1) {
            return new Negative(this.createFunction(first.substring(1), args));
        }

        if (!this.functions.containsKey(first)) {
            throw new AzureLibException("Function '" + first + "' couldn't be found!");
        }

        List<IValue> values = new ArrayList<>();
        List<Object> buffer = new ArrayList<>();

        for (Object o : args) {
            if (o.equals(",")) {
                values.add(this.parseSymbols(buffer));
                buffer.clear();
            } else {
                buffer.add(o);
            }
        }

        if (!buffer.isEmpty()) {
            values.add(this.parseSymbols(buffer));
        }

        Class<? extends Function> function = this.functions.get(first);
        Constructor<? extends Function> ctor = function.getConstructor(IValue[].class, String.class);
        return ctor.newInstance(values.toArray(new IValue[values.size()]), first);
    }

    public IValue valueFromObject(Object object) {
        try {

            if (object instanceof List) {
                return new Group(this.parseSymbols((List<Object>) object));
            }

            if (object instanceof String symbol) {
                /* Variable and constant negation */
                if (symbol.startsWith("!")) {
                    return new Negate(this.valueFromObject(symbol.substring(1)));
                }

                if (this.isDecimal(symbol)) {
                    return new Constant(Double.parseDouble(symbol));
                } else if (this.isVariable(symbol)) {
                    /* Need to account for a negative value variable */
                    if (symbol.startsWith("-")) {
                        symbol = symbol.substring(1);
                        Variable value = this.getVariable(symbol);

                        if (value != null) {
                            return new Negative(value);
                        }
                    } else {
                        IValue value = this.getVariable(symbol);

                        /* Avoid NPE */
                        if (value != null) {
                            return value;
                        }
                    }
                }
            }
        } catch (Exception e) {
            BLib.LOGGER.error("Failed to convert object to value: {}. Using default fallback.", object, e);
        }

        return new Constant(0);
    }

    protected Variable getVariable(String name) {
        return this.variables.get(name);
    }

    protected Operation operationForOperator(String op) throws AzureLibException {
        for (Operation operation : Operation.values()) {
            if (operation.sign.equals(op)) {
                return operation;
            }
        }

        throw new AzureLibException("There is no such operator '" + op + "'!");
    }

    protected boolean isVariable(Object o) {
        return o instanceof String string && !this.isDecimal((String) o) && !this.isOperator(string);
    }

    protected boolean isOperator(Object o) {
        return o instanceof String string && this.isOperator(string);
    }

    protected boolean isOperator(String s) {
        return Operation.OPERATORS.contains(s) || s.equals("?") || s.equals(":");
    }

    protected boolean isDecimal(String s) {
        return s.matches("^-?\\d+(\\.\\d+)?$");
    }
}
