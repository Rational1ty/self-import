package self_modules;

/**
 * When thrown, indicates that either the row index or the column index of a grid operation was out
 * of range. Similar to {@linkplain ArrayIndexOutOfBoundsException}.
 * 
 * @author Matthew Davidson
 * @see IndexOutOfBoundsException
 */
public class GridIndexOutOfBoundsException extends IndexOutOfBoundsException {
    private static final long serialVersionUID = 1L;

	/**
	 * Construct a new {@code GridIndexOutOfBoundsException} with no error message.
	 */
    public GridIndexOutOfBoundsException() {
        super();
    }

	/**
	 * Construct a {@code GridIndexOutOfBoundsException} with the specified error message.
	 * 
	 * @param message The error message associated with this exception.
	 */
    public GridIndexOutOfBoundsException(String message) {
        super(message);
	}
	
	/**
	 * Construct a {@code GridIndexOutOfBoundsException} with a custom error message, determined
	 * by checking if the row or column index of the grid operation are out of range.
	 * <p>
	 * If both indexes are out of range, the message says so specifically. Otherwise, the message
	 * will say which index was out of bounds.
	 * 
	 * @param row The row index of the grid operation that threw this exception.
	 * @param col The column index of the grid operation that threw this exception.
	 * @param size The size of the grid.
	 */
    public GridIndexOutOfBoundsException(int row, int col, int size) {
		super(getCustomMessage(row, col, size));
	}

	private static String getCustomMessage(int row, int col, int size) {
		String message = " out of bounds for size " + size;

        boolean rowOOB = row > size - 1 || row < 0;
        boolean colOOB = col > size - 1 || col < 0;

        if (rowOOB && colOOB) {
            message = String.format("Row index %d and column index %d", row, col) + message;
        } else if (rowOOB) {
            message = String.format("Row index %d", row) + message;
        } else if (colOOB) {
            message = String.format("Column index %d", col) + message;
		}

		return message;
	}
}
