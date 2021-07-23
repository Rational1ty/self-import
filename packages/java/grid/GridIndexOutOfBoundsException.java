/**
 * When thrown, indicates that either the row index or the column index of a grid operation was out
 * of range. Similar to {@link ArrayIndexOutOfBoundsException}.
 * 
 * @see IndexOutOfBoundsException
 */
public class GridIndexOutOfBoundsException extends IndexOutOfBoundsException {
    private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code GridIndexOutOfBoundsException} with no error message.
	 */
    public GridIndexOutOfBoundsException() {
        super();
    }

	/**
	 * Constructs a {@code GridIndexOutOfBoundsException} with the specified error message.
	 * 
	 * @param message the error message associated with this exception
	 */
    public GridIndexOutOfBoundsException(String message) {
        super(message);
	}
	
	/**
	 * Constructs a {@code GridIndexOutOfBoundsException} with a custom error message, determined
	 * by checking if the row or column index of the grid operation are out of range.
	 * 
	 * @param row the row index of the grid operation that threw this exception
	 * @param col the column index of the grid operation that threw this exception
	 * @param numRows the number of rows in the grid
	 */
    public GridIndexOutOfBoundsException(int row, int col, int numRows) {
		super(getMessage(row, col, numRows));
	}

	private static String getMessage(int row, int col, int numRows) {
		return row >= numRows
			? "Row index out of range: " + row
			: "Column index out of range: " + col;
	}
}
