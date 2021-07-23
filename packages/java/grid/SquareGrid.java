public class SquareGrid<T> extends Grid<T> {
	public SquareGrid(int rows) {
		super(rows, rows);
	}

	public SquareGrid(T[][] grid) {
		super(grid);

		// make sure grid is square
		if (grid.length != grid[0].length)
			throw new IllegalArgumentException("grid must be square");
	}
}