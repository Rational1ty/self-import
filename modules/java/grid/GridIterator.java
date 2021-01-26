package self_modules;

/**
 * A subclass of {@link java.util.Iterator} built to traverse a 2-d grid.
 * 
 * @author Matthew Davidson
 * @param <E> {@code E} The type of elements to iterate over.
 */
public class GridIterator<E> implements java.util.Iterator<E> {
	private E[][] grid;
	private int size;
	private int row;
	private int col;

	/**
	 * Construct a {@code GridIterator} from a {@code Grid} object.
	 * 
	 * @param grid The {@code Grid} to iterate over.
	 */
	public GridIterator(Grid<E> grid) {
		this.grid = grid.grid;
		size = grid.size;
		row = -1;
		col = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return !(row == size - 1 && col == size - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	public E next() {
		col = ++col > size - 1 ? 0 : col;
		row = col == 0 ? row + 1 : row;
		return grid[row][col];
	}
}
