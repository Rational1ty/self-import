package lib;

import static java.lang.System.out;
import java.math.BigInteger;

/**
 * A class containing static implementations of more advanced mathematical operations, formulas, and utilities.
 * <p>
 * The {@code AdvMath} class is meant to add to the capabilities of other math-focused utility classes, like
 * {@code Math}. As such, it provides methods to perform less-commonly-used operations, such as factorials,
 * prime checking, and sequences (ex. Fibonacci).
 * <p>
 * Many of the methods in this class return results in the form of {@code BigInteger} or {@code BigDecimal},
 * although many methods support {@code long} return values as well.
 * 
 * @see java.lang.Math
 */
public final class AdvMath {
    private AdvMath() {
    }

    // --------------------------------------------------------------------------------------------------------------------- Factorials

    public static BigInteger factorial(int n) {
        // Calculates n! = n * n-1 * n-2 * ... * 1
        BigInteger result = BigInteger.valueOf(n);
        for (int i = n - 1; i > 0; i--)
            result = result.multiply(BigInteger.valueOf(i));
        return result;
    }

    public static long factorial(int n, boolean dummy) {
        BigInteger result = factorial(n);
        try {
            return result.longValueExact();
        } catch (ArithmeticException ex) {
            return Long.MAX_VALUE;
        }
    }

    public static BigInteger sFactorial(int n) {
        // Calculates n$ = n! * (n-1)! * (n-2)! * ... * 1
        BigInteger result = factorial(n);
        for (int i = n - 1; i > 0; i--)
            result = result.multiply(factorial(i));
        return result;
    }

    public static long sFactorial(int n, boolean dummy) {
        BigInteger result = sFactorial(n);
        try {
            return result.longValueExact();
        } catch (ArithmeticException ex) {
            return Long.MAX_VALUE;
        }
    }

    public static BigInteger hFactorial(int n) {
        // Calculates H(n) = n^n * (n-1)^(n-1) * (n-2)^(n-2) * ... * 1
        BigInteger result = BigInteger.valueOf(n).pow(n);
        for (int i = n - 1; i > 0; i--)
            result = result.multiply(BigInteger.valueOf(i).pow(i));
        return result;
    }

    public static long hFactorial(int n, boolean dummy) {
        BigInteger result = hFactorial(n);
        try {
            return result.longValueExact();
        } catch (ArithmeticException ex) {
            return Long.MAX_VALUE;
        }
    }

    public static BigInteger primorial(int n) {
        // Calculates n#, which is equal to the product of all primes <= n
        BigInteger result = BigInteger.ONE;
        for (int i = n; i > 0; i--) 
            if (isPrime(i)) 
                result = result.multiply(BigInteger.valueOf(i));
        return result;
    }

    public static boolean isPrime(long n) {
        double lim = Math.sqrt(n);
        if (n == 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i <= lim; i += 2)
            if (n % i == 0) return false;
        return true;
    }

    // --------------------------------------------------------------------------------------------------------------------- Fibonacci sequence

    /**
     * Returns the {@code nth} fibonacci number
     * 
     * @param n the 1-based index of the number to compute
     * @return the {@code nth} number in the sequence
     */
    public static BigInteger fibonacci(int n) {
        if (n == 1) return BigInteger.valueOf(0);
        if (n == 2) return BigInteger.valueOf(1);

        var a = BigInteger.valueOf(0);
        var b = BigInteger.valueOf(1);
        var c = BigInteger.valueOf(1);

        for (; n > 2; n--) {
            c = a.add(b);
            a = b;
            b = c;
        }

        return c;
    }

    /**
     * Returns the {@code nth} fibonacci number
     * 
     * @param n the 1-based index of the number to compute
     * @return the {@code nth} number in the sequence, or {@code Long.MAX_VALUE} if the result is too large
     */
    public static long fibonacci(int n, boolean dummy) {
        if (n == 1) return 0;
        if (n == 2) return 1;

        try {
            return fibonacci(n).longValueExact();
        } catch (ArithmeticException ex) {
            return Long.MAX_VALUE;
        }
    }

    // --------------------------------------------------------------------------------------------------------------------- Number formatting

    public static String toSciNotation(BigInteger n) {
        String str = n.toString();
        String result = "";
        if (str.length() >= 3) {
            result += str.charAt(0) + ".";
            result += str.charAt(1) + "" + str.charAt(2);
            result += " x 10^";
            result += str.length() - 1;
        }
        return result;
    }

    public static String toSciNotation(long n) {
        String str = String.valueOf(n);
        String result = "";
        if (str.length() >= 3) {
            result += str.charAt(0) + ".";
            result += str.charAt(1) + str.charAt(2);
            result += " x 10^";
            result += str.length() - 1;
        }
        return result;
    }

    public static String groupDigits(BigInteger n) {
        return null;
    }

    // --------------------------------------------------------------------------------------------------------------------- Pascal's triangle

    // Returns an int array containing 1 row of Pascal's triangle
    public static int[] pascal(int row) {
        int count = 1;
        int[] prev = {1};
        int[] curr = {1};

        while (count <= row) {
            curr = new int[count];

            curr[0] = prev[0];
            for (int i = 1; i < curr.length - 1; i++)
                curr[i] = prev[i - 1] + prev[i];
            curr[curr.length - 1] = prev[prev.length - 1];

            prev = curr.clone();
            count++;
        }
        return curr;
    }

    // Returns a 2d array where each array contains 1 row of Pascal's triangle,
    // starting at row [start] and ending at row [stop]
    public static int[][] pascal(int start, int stop) {
        if (start < 1) {
            start = 1;
        }
        int row = 1;
        int[] prev = {1};
        int[] curr = {1};
        int[][] results = new int[stop - start + 1][];

        while (row <= stop) {
            curr = new int[row];

            curr[0] = prev[0];
            for (int i = 1; i < curr.length - 1; i++)
                curr[i] = prev[i - 1] + prev[i];
            curr[curr.length - 1] = prev[prev.length - 1];

            prev = curr.clone();
            if (row >= start)
                results[row - start] = curr;
            row++;
        }
        return results;
    }

    // Prints an array of rows from Pascal's triangle as a triangle
    public static void printAsTriangle(int[][] data) {
        int base = data[data.length - 1].length;
        base *= 2;
        base -= 1;
        int height = data.length;

        //char[][] triangle = new char[data.length][base];

        int mid, space;
        for (int row = 0; row < height; row++) {
            mid = (data[row].length * 2) - 1;
            space = Math.round((base - mid) / 2);

            for (int i = 0; i < space; i++) {
                out.print(" ");
                //triangle[row][i] = ' ';
            }
            for (int i = 0; i < data[row].length; i++) {
                out.print(data[row][i]);
                //triangle[row][i + space] = Character.forDigit(data[row][i], 10);
                if (i != data[row].length - 1) {
                    out.print(" ");
                    //triangle[row][i + space] = ' ';
                }
            }
            for (int i = 0; i < space; i++) {
               out.print(" ");
            }
            // for (int i = space; i > 0; i--) {
            //     triangle[row][triangle[row].length - i] = ' ';
            // }
            
            out.println();
        }

        //return triangle;
    }

    public static char[][] asTriangle(int[][] data) {
        return new char[][]{};
    }

    // --------------------------------------------------------------------------------------------------------------------- Utilities

    public static double constrain(double val, double min, double max) {
        return Math.max(min, Math.min(val, max));
    }

    public static double dist(double x1, double y1, double x2, double y2) {
        //return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        return Math.hypot(x2 - x1, y2 - y1);
    }

    // TODO: manhattan distance method

    public static double reflectAngleHoriz(double angDeg) {
        double delta;

        delta = -angDeg * 2;
        angDeg += delta;

        while (angDeg < 0)
            angDeg += 360;
        while (angDeg > 360)
            angDeg -= 360;

        return angDeg;
    }

    public static void main(String[] args) {
        var results = new BigInteger[100];
        for (int i = 1; i <= 100; i++) {
            results[i - 1] = fibonacci(i);
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                out.print(results[(10 * i) + j]);
                out.print(" ");
            }
            out.println();
        }
    }
}