import java.util.Collection;

public class SquareGrid<T> extends Grid<T> {
	/**
	 * Constructs an empty grid with no rows or columns.
	 */
	public SquareGrid() {
		super();
	}

	/**
	 * Constructs a square grid of the smallest size that contains all of the elements in the given
	 * collection.
	 * 
	 * @param c collection containing the elements to place in the grid
	 */
	public SquareGrid(Collection<? extends T> c) {
		super(c);
	}

	/**
	 * Constructs a square grid backed by the specified 2-d array. The array must be square.
	 * 
	 * @param grid square 2-d array to create the grid from
	 */
	public SquareGrid(T[][] grid) {
		super(grid);

		// make sure grid is square
		if (grid.length != grid[0].length)
			throw new IllegalArgumentException("grid must be square");
	}

	/**
	 * Constructs a square grid with the given number of rows.
	 * 
	 * @param rows the number of rows and in the grid
	 */
	public SquareGrid(int rows) {
		super(rows, rows);
	}

	/**
	 * Constructs a square grid of the smallest size that contains all of the given elements. All 
	 * leftover cells are filled with {@code null}.
	 * 
	 * @param elements the elements to place into the grid
	 */
	@SafeVarargs
	public SquareGrid(T... elements) {
		super(elements);
	}

	// Static factory methods

	/**
	 * Constructs a square grid from the given rows.
	 * 
	 * @param <T> the type of elements in the grid
	 * @param rows the rows to create the grid from
	 * @return a new {@code SquareGrid} object
	 */
	@SafeVarargs
	public static <T> SquareGrid<T> ofRows(T[]... rows) {
		return new SquareGrid<>(rows);
	}

	/**
	 * Constructs a square grid from the given columns.
	 * 
	 * @param <T> the type of elements in the grid
	 * @param cols the columns to create the grid from
	 * @return a new {@code SquareGrid} object
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> SquareGrid<T> ofColumns(T[]... cols) {
		T[][] temp = (T[][]) new Object[cols[0].length][cols.length];

		for (int c = 0; c < cols.length; c++) {
			for (int r = 0; r < cols[0].length; r++) {
				temp[r][c] = cols[c][r];
			}
		}
		
		return new SquareGrid<>(temp);
	}
}