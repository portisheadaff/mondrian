package mondrian.olap.fun;

import mondrian.calc.Calc;
import mondrian.calc.DoubleCalc;
import mondrian.calc.ExpCompiler;
import mondrian.calc.StringCalc;
import mondrian.calc.impl.AbstractBooleanCalc;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Evaluator;
import mondrian.olap.FunDef;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.ScalarType;
import mondrian.olap.type.StringType;

public class EqualsTypeInvarianceFunDef extends FunDefBase {

    public EqualsTypeInvarianceFunDef(FunDef dummyFunDef) {
        super(dummyFunDef);
    }

    static final FunDefBase resolver =
        new FunDefBase(
            "=",
            "Returns whether two expressions are equal.",
            "ibvv") {

        public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
            if (call.getArg(0).getType() instanceof NumericType
                && call.getArg(1).getType() instanceof NumericType)
            {
                return getCalcNumbers(call, compiler);
            } else if (call.getArg(0).getType() instanceof StringType
                && call.getArg(1).getType() instanceof StringType)
            {
                return getCalcString(call, compiler);
            } else if (call.getArg(0).getType() instanceof MemberType
                || call.getArg(1).getType() instanceof MemberType)
            {
                return getCalcMember(call, compiler);
            }

            return getCalcGeneric(call, compiler);
        }
    };

    protected static Calc getCalcMember(
        final ResolvedFunCall call, ExpCompiler compiler)
    {
        final Calc genCalc0 = compiler.compile(call.getArg(0));
        final Calc genCalc1 = compiler.compile(call.getArg(1));
        final StringCalc strCalc0 = compiler.compileString(call.getArg(0));
        final StringCalc strCalc1 = compiler.compileString(call.getArg(1));
        final DoubleCalc dblCalc0 = compiler.compileDouble(call.getArg(0));
        final DoubleCalc dblCalc1 = compiler.compileDouble(call.getArg(1));

        return new AbstractBooleanCalc(call,
            new Calc[] {genCalc0, genCalc1, strCalc0, strCalc1, dblCalc0, dblCalc1})
        {
            public boolean evaluateBoolean(Evaluator evaluator) {
                Object b0;
                Object b1;

                try {
                    b0 = strCalc0.evaluateString(evaluator);
                } catch(Exception ex) {
                    try {
                        b0 = dblCalc0.evaluateDouble(evaluator);
                    } catch(Exception ex1) {
                        b0 = genCalc0.evaluate(evaluator);
                    }
                }

                try {
                    b1 = strCalc1.evaluateString(evaluator);
                } catch(Exception ex) {
                    try {
                        b1 = dblCalc1.evaluateDouble(evaluator);
                    } catch(Exception ex1) {
                        b1 = genCalc1.evaluate(evaluator);
                    }
                }

                if (b0 == null || b1 == null) {
                    return BooleanNull;
                }
                return b0.toString().equals(b1.toString());
            }
        };
    }

    protected static Calc getCalcGeneric(final ResolvedFunCall call, ExpCompiler compiler) {
        final Calc calc0 = compiler.compile(call.getArg(0));
        final Calc calc1 = compiler.compile(call.getArg(1));
        return new AbstractBooleanCalc(call, new Calc[] {calc0, calc1})
        {
            public boolean evaluateBoolean(Evaluator evaluator) {
                final Object b0 = calc0.evaluate(evaluator);
                final Object b1 = calc1.evaluate(evaluator);
                if (b0 == null || b1 == null) {
                    return BooleanNull;
                }
                return b0.equals(b1);
            }
        };
    }

    protected static Calc getCalcNumbers(final ResolvedFunCall call, ExpCompiler compiler) {
        final DoubleCalc dcalc0 = compiler.compileDouble(call.getArg(0));
        final DoubleCalc dcalc1 = compiler.compileDouble(call.getArg(1));

        return new AbstractBooleanCalc(call, new Calc[] {dcalc0, dcalc1})
        {
            public boolean evaluateBoolean(Evaluator evaluator) {
                final double v0 = dcalc0.evaluateDouble(evaluator);
                final double v1 = dcalc1.evaluateDouble(evaluator);
                if (Double.isNaN(v0)
                        || Double.isNaN(v1)
                        || v0 == DoubleNull
                        || v1 == DoubleNull)
                {
                    return BooleanNull;
                }
                return v0 == v1;
            }
        };
    }

    /*protected static Calc getCalcScalar(final ResolvedFunCall call, ExpCompiler compiler) {
        final Calc dcalc0 = compiler.compileScalar(call.getArg(0), true);
        final Calc dcalc1 = compiler.compileScalar(call.getArg(1), true);

        return new AbstractBooleanCalc(call, new Calc[] {dcalc0, dcalc1})
        {
            public boolean evaluateBoolean(Evaluator evaluator) {
                final Object v0 = dcalc0.evaluate(evaluator);
                final Object v1 = dcalc1.evaluate(evaluator);
                if (v0 == null || v1 == null)
                {
                    return BooleanNull;
                }
                return v0 == v1;
            }
        };
    } */

    protected static Calc getCalcString(final ResolvedFunCall call, ExpCompiler compiler) {
        final StringCalc scalc0 = compiler.compileString(call.getArg(0));
        final StringCalc scalc1 = compiler.compileString(call.getArg(1));
        return new AbstractBooleanCalc(call, new Calc[] {scalc0 , scalc1})
        {
            public boolean evaluateBoolean(Evaluator evaluator) {
                final String b0 = scalc0.evaluateString(evaluator);
                final String b1 = scalc1.evaluateString(evaluator);
                if (b0 == null || b1 == null) {
                    return BooleanNull;
                }
                return b0.equals(b1);
            }
        };
    }


}
