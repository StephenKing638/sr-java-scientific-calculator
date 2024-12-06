
package sr.calculator;

import java.util.Objects;

/**
 * Imported from sr.control.
 * <p>
 * A utility class for math operations on fractions.
 * <p>
 * Fractions in this class are represented as {@code int[2]} arrays where
 * {@code int[0]} is the numerator and {@code int[1]} is the denominator. Most
 * of these methods except the {@link #fractionToString(int[])} require non-zero
 * denominators.
 *
 * @author StephenKing638 (https://github.com/StephenKing638)
 * @since 2-12-24 from sr.control
 */
public class FracUtil {
    
    /**
     * Converts a decimal number to its fractional representation and returns the
     * string representation.
     * 
     * @param x The decimal number to convert.
     * @return A string representation of the fraction equivalent to the decimal.
     */
    public static String fractionToString(double x){
        return fractionToString(toFraction(x));
    }

    /**
     * Converts a fraction represented by an int array to its string representation.
     * 
     * @param frac An int array representing a fraction [numerator, denominator].
     * @return A string representation of the fraction.
     * @throws IllegalArgumentException If the provided int[] array is not a valid fraction.
     */
    public static String fractionToString(int[] frac) {
        if(frac.length != 2) {
            throw new IllegalArgumentException("The provided int[] array is not a fraction");
        }
        return String.format("%d/%d", frac[0], frac[1]);
    }

    /**
     * Calculates the greatest common divisor (GCD) of the fraction represented by a
     * decimal number and returns the fraction in reduced form.
     * 
     * @param x The decimal number representing the fraction.
     * @return int array representing the fraction in reduced form [numerator,
     *         denominator].
     */
    public static int[] gcdFraction(double x) {
        int[] frac = toFraction(x);
        int gcd = getGCD(frac[0], frac[1]);
        frac[0] = frac[0] / gcd;
        frac[1] = frac[1] / gcd;
        return frac;
    }

    /**
     * Converts a decimal number to its fractional representation.
     * 
     * @param x decimal number.
     * @return int array representing the fraction [numerator, denominator].
     */
    public static int[] toFraction(double x) {
        return toFraction(x, 1.0E-6);
    }

    /**
     * Converts an accurate fraction representation of irrational numbers up to 15 digits.
     * 
     * @param x decimal number.
     * @return int array representing the fraction [numerator, denominator].
     */
    public static int[] toLargeFraction(double x) {
        return toFraction(x, 1.0E-16);
    }

    
    /** 
     * @param x
     * @param tolerance
     * @return int[]
     */
    private static int[] toFraction(double x, double tolerance) {
        if (x < 0){
            int[] frac = toFraction(-x);
            frac[0] = -frac[0];
            return frac;
        }
        double h1=1; double h2=0;
        double k1=0; double k2=1;
        double b = x;
        do {
            double a = Math.floor(b);
            double aux = h1; h1 = a*h1+h2; h2 = aux;
            aux = k1; k1 = a*k1+k2; k2 = aux;
            b = 1/(b-a);
        } while (Math.abs(x-h1/k1) > x*tolerance);
    
        return new int[] {(int) h1, (int) k1};
    }

    /**
     * Calculates the greatest common divisor (GCD) of two integers using the
     * Euclidean algorithm.
     * 
     * @param a The first integer.
     * @param b The second integer.
     * @return The GCD of the two integers.
     */
    public static int getGCD(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);

        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    
    /** 
     * @param frac1
     * @param frac2
     * @return int[]
     */
    public static int[] addFractions(int[] frac1, int[] frac2) {
        requireNonZeroDenom(frac1);
        requireNonZeroDenom(frac2);

        int commonDenominator = lcm(frac1[1], frac2[1]);

        int numeratorSum = frac1[0] * (commonDenominator / frac1[1]) + frac2[0] * (commonDenominator / frac2[1]);

        return simplifyFraction(new int[]{numeratorSum, commonDenominator});
    }

    
    /** 
     * @param frac1
     * @param frac2
     * @return int[]
     */
    public static int[] subtractFractions(int[] frac1, int[] frac2) {
        requireNonZeroDenom(frac1);
        requireNonZeroDenom(frac2);

        int commonDenominator = lcm(frac1[1], frac2[1]);

        int numeratorDifference = frac1[0] * (commonDenominator / frac1[1]) - frac2[0] * (commonDenominator / frac2[1]);
        return simplifyFraction(new int[]{numeratorDifference, commonDenominator});
    }

    
    /** 
     * @param frac1
     * @param frac2
     * @return int[]
     */
    public static int[] multiplyFractions(int[] frac1, int[] frac2) {
        requireNonZeroDenom(frac1);
        requireNonZeroDenom(frac2);

        int numeratorProduct = frac1[0] * frac2[0];
        int denominatorProduct = frac1[1] * frac2[1];

        return simplifyFraction(new int[]{numeratorProduct, denominatorProduct});
    }

    public static int[] divideFractions(int[] frac1, int[] frac2) {
        requireNonZeroDenom(frac1);
        requireNonZeroDenom(frac2);

        int[] reciprocalFrac2 = reciprocal(frac2);
        return multiplyFractions(frac1, reciprocalFrac2);
    }

    public static int compareFractions(int[] frac1, int[] frac2) {
        requireNonZeroDenom(frac1);
        requireNonZeroDenom(frac2);

        int commonDenominator = lcm(frac1[1], frac2[1]);

        int numerator1 = frac1[0] * (commonDenominator / frac1[1]);
        int numerator2 = frac2[0] * (commonDenominator / frac2[1]);

        return Integer.compare(numerator1, numerator2);
    }

    public static int[] reciprocal(int[] frac) {
        requireNonZeroDenom(frac);

        return new int[]{frac[1], frac[0]};
    }

    public static double fractionToDecimal(int[] frac) {
        requireNonZeroDenom(frac);

        return (double) frac[0] / (double) frac[1];
    }

    public static int[] simplifyFraction(int[] frac) {
        requireNonZeroDenom(frac);

        int gcd = getGCD(frac[0], frac[1]);
        return new int[]{frac[0] / gcd, frac[1] / gcd};
    }

    // Least Common Multiple (LCM) calculation
    private static int lcm(int a, int b) {
        return Math.abs(a * b) / getGCD(a, b);
    }


    /* OTHER UTILITY METHODS */

    public static void requireNonNullFraction(int[] frac) {
        Objects.requireNonNull(frac);
        if(frac.length != 2) {
            throw new IllegalArgumentException("the provided int[] is not a fraction");
        }
    }

    public static void requireNonZeroDenom(int[] frac) {
        requireNonNullFraction(frac);
        if(frac[1] == 0) {
            throw new IllegalArgumentException("denominator cannot be zero: " + fractionToString(frac));
        }
    }
}