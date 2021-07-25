import static java.lang.System.out;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A fixed-size rectangular grid implementation of the {@code Collection} interface. {@code null}
 * elements are permitted.
 * 
 * @param <T> the type of elements stored in this grid
 */
public class Grid<T> implements Collection<T>, Cloneable {
	private final T[][] grid;

	/**
	 * The number of rows in this grid.
	 */
	public final int rows;

	/**
	 * The number of columns in this grid.
	 */
	public final int cols;

	/**
	 * Constructs an empty grid with no rows or columns.
	 */
	public Grid() {
		this(0, 0);
	}

	/**
	 * Constructs a square grid of the smallest size that contains all of the elements in the given
	 * collection.
	 * 
	 * @param c the collection containing the elements to place in the grid
	 */
	@SuppressWarnings("unchecked")
	public Grid(Collection<? extends T> c) {
		int size = (int) Math.ceil(Math.sqrt(Objects.requireNonNull(c).size()));
		T[] arr = c.toArray((T[]) new Object[0]);

		grid = (T[][]) new Object[size][size];
		rows = cols = size;

		outer:
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int i = (y * size) + x;

				if (i == arr.length) break outer;

				grid[y][x] = arr[i];
			}
		}
	}

	/**
	 * Constructs a grid backed by the given 2-d array.
	 * 
	 * @param grid the contents of the grid
	 */
	public Grid(T[][] grid) {
		if (isJagged(grid))
			throw new IllegalArgumentException("Grid cannot be constructed from jagged array");

		this.grid = grid;
		rows = grid.length;
		cols = grid[0].length;
	}

	/**
	 * Constructs a grid with the given number of rows and columns. The grid is filled with
	 * {@code null} elements.
	 * 
	 * @param rows the number of rows in the grid
	 * @param cols the number of columns in the grid
	 */
	@SuppressWarnings("unchecked")
	public Grid(int rows, int cols) {
		grid = (T[][]) new Object[rows][cols];
		this.rows = rows;
		this.cols = cols;
	}

	/**
	 * Constructs a square grid of the smallest size that contains all of the given elements. All 
	 * leftover cells are filled with {@code null} elements.
	 * 
	 * @param elements the elements to place into the grid
	 */
	@SafeVarargs
	public Grid(T... elements) {
		this(Arrays.asList(elements));
	}

	// Static factory methods

	/**
	 * Constructs a grid from the given rows.
	 * 
	 * @param <T> the type of elements in the grid
	 * @param rows the rows to create the grid from
	 * @return a new {@code Grid} object
	 */
	@SafeVarargs
	public static <T> Grid<T> ofRows(T[]... rows) {
		return new Grid<>(rows);
	}

	/**
	 * Constructs a grid from the given columns.
	 * 
	 * @param <T> the type of elements in the grid
	 * @param cols the columns to create the grid from
	 * @return a new {@code Grid} object
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> Grid<T> ofColumns(T[]... cols) {
		T[][] temp = (T[][]) new Object[cols[0].length][cols.length];

		for (int c = 0; c < cols.length; c++) {
			for (int r = 0; r < cols[0].length; r++) {
				temp[r][c] = cols[c][r];
			}
		}
		
		return new Grid<>(temp);
	}

	// Static utility methods

	/**
	 * Tests whether or not the given array is <em>jagged</em>. An array is considered
	 * jagged if any of the arrays within it are not of uniform length.
	 * <p>
	 * Example of rectangular array:
	 * <pre>
	 *arr = {
	 * {1, 2, 3},
	 * {4, 5, 6},
	 * {7, 8, 9}
	 *}
	 * </pre>
	 * 
	 * Example of jagged array:
	 * <pre>
	 *arr = {
	 * {1, 2, 3, 4},
	 * {5, 6},
	 * {7, 8, 9}
	 *}
	 * </pre>
	 * 
	 * @param <T> the type of elements in the array
	 * @param arr the array to test
	 * @return {@code true} if the given array is jagged, otherwise {@code false}
	 */
	public static <T> boolean isJagged(T[][] arr) {
		Objects.requireNonNull(arr);
		int len = arr[0].length;
		for (int i = 1; i < arr.length; i++) {
			if (arr[i].length != len) return true;
		}
		return false;
	}

	// Collection methods

	/**
	 * This operation is not supported by {@code Grid}.
	 */
	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException("add(T) not supported by Grid");
	}

	/**
	 * This operation is not supported by {@code Grid}.
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("addAll(Collection<? extends T>) not supported by Grid");
	}

	/**
	 * Sets the value of every cell in this grid to {@code null}.
	 */
	@Override
	public void clear() {
		replaceAll(e -> null);
	}

	/**
	 * Creates and returns a copy of this grid. The copy is backed by a new 2-d array with the
	 * same values as the original.
	 * 
	 * @return a clone of this grid
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		T[][] copy = (T[][]) new Object[rows][cols];

		for (int r = 0; r < rows; r++) {
			copy[r] = Arrays.copyOf(grid[r], cols);
		}

		return new Grid<>(copy);
	}

	@Override
	public boolean contains(Object o) {
		for (T e : this) {
			if (Objects.equals(o, e)) return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c) {
			if (!this.contains(e)) return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Grid<?>)) return false;

		var other = (Grid<?>) obj;

		// if both grids point to the same array
		if (grid == other.grid) return true;

		// check if number of rows/columns are different
		if (rows != other.rows || cols != other.cols) return false;

		// check if all cells match
		return Arrays.deepEquals(grid, other.grid);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(grid);
	}

	/**
	 * Returns {@code true} if {@code this.rows == 0} or {@code this.cols == 0}.
	 * 
	 * @return {@code true} if this grid is empty, otherwise {@code false}
	 */
	@Override
	public boolean isEmpty() {
		return rows == 0 || cols == 0;
	}

	/**
	 * Returns an iterator over the elements in this grid.
	 * <p>
	 * The iterator starts at row 0, column 0 (the top-left corner) and traverses the grid
	 * row-by-row, like so:
	 * 
	 * <pre>
	 *1  ->  2  ->  3
	 *   _________ /
	 * /            
	 *4  ->  5  ->  6
	 * </pre>
	 * 
	 * The iterator terminates at the bottom-right corner of the grid.
	 * 
	 * @return an iterator over the elements in the grid
	 */
	@Override
	public Iterator<T> iterator() {
		return new GridIterator<>(this);
	}

	/**
	 * Iterator that traverses grid elements left to right, top to bottom
	 */
	private static class GridIterator<T> implements Iterator<T> {
		private final Grid<T> target;
		private int row = -1;
		private int col = -1;

		private GridIterator(Grid<T> g) {
			target = g;
		}

		@Override
		public boolean hasNext() {
			return !(row == target.rows - 1 && col == target.cols - 1);
		}

		@Override
		public T next() {
			col = ++col == target.cols ? 0 : col;
			row = col == 0 ? row + 1 : row;
			return target.grid[row][col];
		}

		/**
		 * Replaces the last element returned by {@code next()} with the given element.
		 * 
		 * @param e the element with which to replace the last element returned
		 */
		public void set(T e) {
			target.grid[row][col] = e;
		}

		/**
		 * Removes the last element returned by {@code next()}.
		 */
		public void remove() {
			target.grid[row][col] = null;
		}
	}

	/**
	 * Removes the first occurrence of the specified object from this grid, if it is present. More 
	 * formally, removes an element {@code e} such that {@code o.equals(e)}.
	 * <p>
	 * Calling this method with an argument of {@code null} has no effect and will always return
	 * {@code false}.
	 * 
	 * @param o the element to remove
	 * @return {@code true} if this grid changed as a result of the call (if the element was removed)
	 */
	@Override
	public boolean remove(Object o) {
		if (o == null) return false;

		int[] pos = positionOf(o);

		if (pos[0] == -1) return false;

		grid[pos[0]][pos[1]] = null;
		return true;
	}

	/**
	 * Removes all of the elements in this grid that are also contained in the specified collection.
	 * After this call returns, this grid will have no elements in common with the specified 
	 * collection.
	 * 
	 * @param c collection containing the elements to be removed
	 * @return {@code true} if this grid changed as a result of the call
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;

		for (Object o : c) {
			if (remove(o))
				changed = true;
		}

		return changed;
	}

	/**
	 * Replaces each element of this grid with the result of applying the operator to that element.
	 * <p>
	 * The operator is applied to the elements in the same order as returned by
	 * {@link Grid#iterator()}.
	 * 
	 * @param op the operator to apply to each element
	 */
	public void replaceAll(UnaryOperator<T> op) {
		Objects.requireNonNull(op);
		final var iter = new GridIterator<T>(this);

		while (iter.hasNext()) {
			T e = iter.next();
			if (e == null) continue;
			iter.set(op.apply(e));
		}
	}

	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		Objects.requireNonNull(filter);

		final var iter = new GridIterator<T>(this);
		boolean changed = false;

		while (iter.hasNext()) {
			T e = iter.next();
			if (e != null && filter.test(e)) {
				iter.remove();
				changed = true;
			}
		}

		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);

		final var iter = new GridIterator<T>(this);
		boolean changed = false;

		while (iter.hasNext()) {
			T e = iter.next();
			if (e == null || c.contains(e)) continue;
			iter.remove();
			changed = true;
		}

		return changed;
	}

	/**
	 * Returns the area of this grid ({@code rows x cols}).
	 * 
	 * @return the number of cells in this grid
	 */
	@Override
	public int size() {
		return rows * cols;
	}

	@Override
	public Object[] toArray() {
		return Stream.of(grid)
			.flatMap(Stream::of)
			.toArray();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> E[] toArray(E[] a) {
		Objects.requireNonNull(a);

		Object[] arr = toArray();
		int size = size();

		// if the provided array is too small
		if (a.length < size) {
			return (E[]) Arrays.copyOf(arr, size, a.getClass());
		}

		System.arraycopy(arr, 0, a, 0, size);
		return a;
	}

	/**
	 * Returns a string representation of this grid.
	 * <P>
	 * The returned string is of the format {@code "Grid (RxC)"} where {@code R} and {@code C} are
	 * the dimensions of the grid.
	 * 
	 * @return a string representation of this grid
	 */
	@Override
	public String toString() {
		return String.format("Grid (%dx%d)", rows, cols);
	}

	/**
	 * Gets the element at the specified position in this grid.
	 * 
	 * @param row the row index of the element
	 * @param col the column index of the element
	 * @return the element at (row, col)
	 */
	public T get(int row, int col) {
		try {
			return grid[row][col];
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(row, col, rows);
		}
	}

	/**
	 * Returns an entire row from this grid. The returned array is a copy of the row.
	 * 
	 * @param row the index of the row
	 * @return the row
	 */
	public T[] getRow(int row) {
		try {
			return Arrays.copyOf(grid[row], cols);
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(row, 0, rows);
		}
	}

	/**
	 * Returns an entire column from this grid. The returned array is a copy of the column.
	 * 
	 * @param col the index of the column
	 * @return the column
	 */
	@SuppressWarnings("unchecked")
	public T[] getColumn(int col) {
		T[] column = (T[]) new Object[rows];
		try {
			for (int r = 0; r < rows; r++) {
				column[r] = grid[r][col];
			}
			return column;
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(0, col, rows);
		}
	}

	/**
	 * Replaces the element at the specified position in this grid with the given element.
	 * 
	 * @param row the row index of the element
	 * @param col the column index of the element
	 * @param element the element to place into the grid
	 */
	public void set(int row, int col, T element) {
		try {
			grid[row][col] = element;
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(row, col, rows);
		}
	}

	/**
	 * Sets the values of an entire row in this grid.
	 * 
	 * @param row the row to replace
	 * @param elements the contents of the row
	 */
	public void setRow(int row, T[] elements) {
		try {
			grid[row] = Arrays.copyOf(elements, cols);
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(row, 0, rows);
		}
	}

	/**
	 * Sets the values of an entire column in this grid.
	 * 
	 * @param col the column to replace
	 * @param elements the contents of the column
	 */
	public void setColumn(int col, T[] elements) {
		try {
			for (int r = 0; r < rows; r++) {
				grid[r][col] = r < elements.length ? elements[r] : null;
			}
		} catch (IndexOutOfBoundsException ex) {
			throw new GridIndexOutOfBoundsException(0, col, rows);
		}
	}

	/**
	 * Returns the row and column of the first occurrence of the specified element in this grid, or
	 * <code>{-1, -1}</code> if this grid does not contain the element.
	 * 
	 * @param o the element to search for
	 * @return <code>{row, col}</code>, or <code>{-1, -1}</code> if the element was not found
	 * @see Grid#lastPositionOf(Object)
	 */
	public int[] positionOf(Object o) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (o.equals(grid[r][c])) return new int[]{r, c};
			}
		}
		return new int[]{-1, -1};
	}

	/**
	 * Returns the row and column of the last occurrence of the specified element in this grid, or
	 * <code>{-1, -1}</code> if this grid does not contain the element.
	 * 
	 * @param o the element to search for
	 * @return <code>{row, col}</code>, or <code>{-1, -1}</code> if the element was not found
	 * @see Grid#positionOf(Object)
	 */
	public int[] lastPositionOf(Object o) {
		for (int r = rows - 1; r >= 0; r--) {
			for (int c = cols - 1; c >= 0; c--) {
				if (o.equals(grid[r][c])) return new int[]{r, c};
			}
		}
		return new int[]{-1, -1};
	}

	/**
	 * Print the contents of this grid to {@code System.out} with simple formatting.
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

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				T e = grid[r][c];
				String data = e == null ? "null" : e.toString();
				out.printf(f, data);
			}
			out.println();
		}
	}

	/**
	 * Print the contents of this grid to {@code System.out} with extra formatting.
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
	 * Example output 2 (ASCII only):
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
	 * @param useUnicode allows the formatter to use special Unicode characters to
	 * enhance the appearance of the output; otherwise, it will use ASCII characters only
	 */
	public void pprint(boolean useUnicode) {
		final int maxWidth = longestElementLength();

		char divider = useUnicode ? '\u2502' : '|';
		String f = " %s " + divider;

		out.println(gridRow("top", maxWidth, useUnicode));

		for (int r = 0; r < rows; r++) {
			out.print(divider);

			for (int c = 0; c < grid[r].length; c++) {
				T e = grid[r][c];
				String data = e == null ? "null" : e.toString();
				String padded = pad(data, maxWidth);
				out.printf(f, padded);
			}

			out.println();

			if (r < rows - 1) {
				out.println(gridRow("middle", maxWidth, useUnicode));
			}
		}

		out.println(gridRow("bottom", maxWidth, useUnicode));
	}

	// Helper methods for print/pprint

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
			start = '+';
			mid = '+';
			end = '+';
		}

		String row = "" + start;
		for (int i = 0; i < cols; i++) {
			row += between;
			if (i < cols - 1) {
				row += mid;
			}
		}
		row += end;

		return row;
	}

	private int longestElementLength() {
		int maxLen = 0;

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				int len = grid[r][c] == null ? 4 : grid[r][c].toString().length();
				if (len > maxLen) {
					maxLen = len;
				}
			}
		}

		return maxLen;
	}
}