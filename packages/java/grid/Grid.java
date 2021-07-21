import static java.lang.System.out;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

// TODO: implement Collection methods

/**
 * A square fixed-length grid class with support for various iterative and
 * mutative operations
 * 
 * @author Matthew Davidson
 * @param <T> {@code T} the type of elements stored in the grid
 */
public class Grid<T> implements Iterable<T> {
	protected int size;
	protected T[][] grid;

	public static void main(String[] args) throws Throwable {
		
	}

	/**
	 * Construct a new {@code Grid} with a size of 0.
	 */
	public Grid() {
		this(0);
	}

	/**
	 * Construct an empty {@code Grid} with the given size.
	 * 
	 * @param size The size of the grid.
	 */
	public Grid(int size) {
		this.size = size;
		grid = (T[][]) new Object[size][size];
	}

	/**
	 * Construct a {@code Grid} from the given 2-dimensional array.
	 * <p>
	 * Note that the 2-d array must contain rows of uniform length, and the total number of rows
	 * must equal the length of each row. A 2-d array that does not satisfy these conditions is
	 * referred to as <em>jagged</em>.
	 * 
	 * <pre>
	 * // Example of a jagged array:
	 * [
	 * 	[1, 2, 3],
	 * 	[4, 5],
	 * 	[6, 7, 8, 9, 10]
	 * ]
	 * </pre>
	 * 
	 * @param initial The 2-d array to construct the grid from.
	 * @throws IllegalArgumentException If the given array is jagged
	 */
	public Grid(T[][] initial) throws IllegalArgumentException {
		if (initial == null) {
			throw new IllegalArgumentException("Expected 2-d array but got null");
		}

		boolean isSquare = true;
		int rows = initial.length;
		for (int r = 0; r < rows; r++) {
			if (initial[r].length != rows) {
				isSquare = false;
				break;
			}
		}

		if (isSquare) {
			size = rows;
			grid = initial;
		} else {
			throw new IllegalArgumentException("Expected square matrix but got jagged matrix");
		}
	}

	/**
	 * Construct a new {@code Grid} of the given size, initialized with a list of elements.
	 * <p>
	 * The spaces in the grid are populated in the same order returned by {@link Grid#elements()}
	 * (that is, row-by-row). If the number of values provided is less than the area of the grid
	 * ({@code values.length < size * size}), any remaining spaces will be initialized to
	 * {@code null}.
	 * 
	 * @param size The size of the grid.
	 * @param values The elements to place in the grid.
	 */
	@SafeVarargs
	public Grid(int size, T... values) {
		this.size = size;

		grid = (T[][]) new Object[size][size];

		int i = 0;
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				grid[r][c] = values[i];
				if (i == values.length - 1)
					break;
				i++;
			}
		}
	}

	@SafeVarargs
	public static <T> Grid<T> ofRows(T[]... rows) {
		return new Grid<T>(rows);
	}

	@SafeVarargs
	public static <T> Grid<T> ofColumns(T[]... cols) {
		T[][] temp = (T[][]) new Object[cols.length][cols.length];
		for (int c = 0; c < cols.length; c++) {
			for (int r = 0; r < cols[c].length; r++) {
				temp[r][c] = cols[c][r];
			}
		}
		return new Grid<T>(temp);
	}

	/**
	 * Returns an ordered {@code Stream} of all the elements in this {@code Grid}.
	 * <p>
	 * The returned {@code Stream} starts at row 0, column 0 (the top-left corner) and traverses
	 * the {@code Grid} row-by-row, like this:
	 * 
	 * <pre>
	 *1  ->  2  ->  3
	 *   _________ /
	 * /            
	 *4  ->  5  ->  6
	 * </pre>
	 * 
	 * The stream ends at the bottom-right corner of the grid.
	 * 
	 * @return an ordered {@code Stream} of the elements in the grid.
	 */
	public Stream<T> elements() {
		return Arrays.stream(grid).flatMap(arr -> Stream.of(arr));
	}

	/**
	 * Returns a {@code Stream} of all of the rows in this {@code Grid}.
	 * 
	 * @return the {@code Stream} of rows.
	 */
	public Stream<T[]> rows() {
		return Arrays.stream(grid);
	}

	/**
	 * Returns a {@code Stream} of all the columns in this {@code Grid}.
	 * 
	 * @return the {@code Stream} of columns.
	 */
	public Stream<T[]> columns() {
		Stream.Builder<T[]> cols = Stream.builder();

		for (int c = 0; c < size; c++) {
			T[] col = (T[]) new Object[size];

			for (int r = 0; r < size; r++) {
				col[r] = grid[r][c];
			}

			cols.accept(col);
		}

		return cols.build();
	}

	/**
	 * Access an element in a space in this {@code Grid}.
	 * 
	 * @param row The row index of the space.
	 * @param col The column index of the space.
	 * @return The element.
	 * @throws GridIndexOutOfBoundsException If either {@code row} or {@code col} is out of range
	 * ({@code rowOrCol < 0 || rowOrCol >= size}).
	 */
	public T get(int row, int col) throws GridIndexOutOfBoundsException {
		try {
			return grid[row][col];
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(row, col, size);
		}
	}

	/**
	 * Set the value of a space in this {@code Grid}. This replaces the old value.
	 * 
	 * @param row The row index of the space.
	 * @param col The column index of the space.
	 * @param element The element to put in the space.
	 * @throws GridIndexOutOfBoundsException If either {@code row} or {@code col} is out of range
	 * ({@code rowOrCol < 0 || rowOrCol >= size}).
	 */
	public void set(int row, int col, T element) throws GridIndexOutOfBoundsException {
		try {
			grid[row][col] = element;
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(row, col, size);
		}
	}

	/**
	 * Get a specific row from this {@code Grid}.
	 * 
	 * @param r The index of the row.
	 * @return The row.
	 * @throws GridIndexOutOfBoundsException If the row index is out of range
	 * ({@code r < 0 || r >= size}).
	 */
	public T[] getRow(int r) throws GridIndexOutOfBoundsException {
		try {
			return grid[r];
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(r, 0, size);
		}
	}

	/**
	 * Get a specific column from this {@code Grid}.
	 * 
	 * @param c The index of the column.
	 * @return The column.
	 * @throws GridIndexOutOfBoundsException If the column index is out of range
	 * ({@code c < 0 || c >= size}).
	 */
	public T[] getColumn(int c) throws GridIndexOutOfBoundsException {
		T[] col = (T[]) new Object[size];
		try {
			for (int r = 0; r < size; r++) {
				col[r] = grid[r][c];
			}
			return col;
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(0, c, size);
		}
	}

	public void setRow(int r, T[] content) {
	}

	public void setColumn(int c, T[] content) {
	}

	/**
	 * Print the contents of this {@code Grid} to {@code System.out} with simple formatting.
	 * <p>
	 * The output is a grid with cells of equal width and with no dividing lines between cells.
	 * The content of each space is left-aliged, and there is at least 2 spaces between elements.
	 * 
	 * <pre>
	 * // Example output:
	 * 6    27   31
	 * 400  92   5
	 * 21   733  64
	 * </pre>
	 */
	public void print() {
		final int maxWidth = longestElementLength();
		String f = String.format(" %%-%ds ", maxWidth);

		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				T e = grid[r][c];
				String data = e == null ? "null" : e.toString();
				out.printf(f, data);
			}
			out.println();
		}
	}

	/**
	 * Print the contents of this {@code Grid} to {@code System.out} with more formatting.
	 * <p>
	 * The output is a grid of equally-sized cells divided by gridlines. Each element is centererd
	 * within its space in the grid, and has at least 1 space of padding on its left and right.
	 * <p>
	 * Example output 1 (with Unicode):
	 * <pre>
	 *┌─────┬─────┬─────┐
	 *│  6  │ 27  │ 31  │
	 *├─────┼─────┼─────┤
	 *│ 400 │ 92  │  5  │
	 *├─────┼─────┼─────┤
	 *│ 21  │ 733 │ 64  │
	 *└─────┴─────┴─────┘
	 * </pre>
	 * Example output 2 (without Unicode):
	 * <pre>
	 *+-----+-----+-----+
	 *|  6  | 27  | 31  |
	 *+-----+-----+-----+
	 *| 400 | 92  |  5  |
	 *+-----+-----+-----+
	 *| 21  | 733 | 64  |
	 *+-----+-----+-----+
	 * </pre>
	 * 
	 * @param useUnicode If {@code true}, allows the formatter to use special Unicode characters to
	 * enhance the appearance of the output. Otherwise, it will only use non-special characters to
	 * avoid any display issues.
	 */
	public void prettyPrint(boolean useUnicode) {
		final int maxWidth = longestElementLength();

		char divider = useUnicode ? '\u2502' : '|';
		String f = " %s " + divider;

		out.println(gridRow("top", maxWidth, useUnicode));
		for (int r = 0; r < grid.length; r++) {
			out.print(divider);
			for (int c = 0; c < grid[r].length; c++) {
				T e = grid[r][c];
				String data = e == null ? "null" : e.toString();
				String padded = pad(data, maxWidth);
				out.printf(f, padded);
			}
			out.println();
			if (r < size - 1) {
				out.println(gridRow("middle", maxWidth, useUnicode));
			}
		}
		out.println(gridRow("bottom", maxWidth, useUnicode));
	}

	private String pad(String str, int totalWidth) {
		int space = totalWidth - str.length();
		int padding = (int) Math.floor(space / 2.0);
		int remaining = space - padding;
		return String.format("%s%s%s", " ".repeat(padding), str, " ".repeat(remaining));
	}

	private String gridRow(String location, int dataWidth, boolean useUnicode) {
		char start = '|', mid = '|', end = '|';
		String between = (useUnicode ? "\u2500" : "-").repeat(dataWidth + 2);

		if (useUnicode) {
			switch (location.toLowerCase()) {
				case "top":
					start = '\u250c';
					mid = '\u252c';
					end = '\u2510';
					break;
				case "middle":
					start = '\u251c';
					mid = '\u253c';
					end = '\u2524';
					break;
				case "bottom":
					start = '\u2514';
					mid = '\u2534';
					end = '\u2518';
					break;
			}
		} else {
			switch (location.toLowerCase()) {
				case "top":
					start = '+';
					mid = '+';
					end = '+';
					break;
				case "middle":
					start = '+';
					mid = '+';
					end = '+';
					break;
				case "bottom":
					start = '+';
					mid = '+';
					end = '+';
					break;
			}
		}

		String row = "" + start;
		for (int i = 0; i < size; i++) {
			row += between;
			if (i < size - 1) {
				row += mid;
			}
		}
		row += end;

		return row;
	}

	private int longestElementLength() {
		int maxLen = 0;
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				int len;
				if (grid[r][c] == null) {
					len = 4;
				} else {
					len = grid[r][c].toString().length();
				}
				maxLen = len > maxLen ? len : maxLen;
			}
		}
		return maxLen;
	}

	/**
	 * Get the size of this {@code Grid}.
	 * <p>
	 * This is equal to the length of one row or one column of the grid (ie, the side length of
	 * the grid).
	 * 
	 * @return The size of the grid
	 */
	public int size() {
		return size;
	}

	/**
	 * Get the area of this {@code Grid}.
	 * <p>
	 * Equal to {@code size * size}, or the total number of spaces in the grid.
	 * 
	 * @return The total number of spaces in the grid
	 */
	public int area() {
		return size * size;
	}

	/**
	 * Applies the given function to every element in the {@code Grid}, replacing the
	 * original element with the result of the function.
	 * <p>
	 * The function is applied to the elements in the same order as returned by 
	 * {@link Grid#elements()} (that is, by traversing row-by-row).
	 * 
	 * @param operator The {@code Function} to apply to each element.
	 */
	public void replaceAll(UnaryOperator<T> operator) {
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				grid[r][c] = operator.apply(grid[r][c]);
			}
		}
	}

	// @Override
	// public void forEach(Consumer<? super T> action) {
		
	// }

	/**
	 * {@inheritDoc}
	 */
	public Iterator<T> iterator() {
		return new GridIterator<T>(this);
	}

	/**
	 * Returns a string representation of this {@code Grid}. The result is a concise but informative
	 * representation that is easy for a person to read.
	 * 
	 * @return A string representation of this {@code Grid}, in the format of {@code Grid (NxN)}
	 * where {@code N} is the size of the grid.
	 */
	@Override
	public String toString() {
		return String.format("Grid (%dx%d)", size, size);
	}
}