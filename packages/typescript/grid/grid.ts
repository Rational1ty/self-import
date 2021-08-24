export default class Grid<T> {

	// Private constructor to be called from static factory methods
	private constructor(
		protected readonly grid: T[][],
		public readonly rows: number,
		public readonly cols: number
	) {}

	// Static factory methods

	static fromMatrix<T>(mat: T[][]): Grid<T> {
		if (this.isJagged(mat))
			throw new Error('mat cannot be jagged');
		return new Grid(mat, mat.length, mat[0].length);
	}

	static fromDimensions<T>(rows: number, cols: number): Grid<T> {
		const grid = this.createMatrix<T>(rows, cols);
		return new Grid<T>(grid, rows, cols);
	}

	static of<T>(...elements: T[]): Grid<T> {
		const size = Math.ceil(Math.sqrt(elements.length));
		const grid = this.createMatrix<T>(size, size);

		outer:
		for (let y = 0; y < size; y++) {
			for (let x = 0; x < size; x++) {
				const i = (y * size) + x;

				if (i === elements.length) break outer;

				grid[y][x] = elements[i];
			}
		}

		return new Grid(grid, size, size);
	}

	static ofRows<T>(...rows: T[][]): Grid<T> {
		if (this.isJagged(rows))
			throw new Error('rows cannot be jagged');
		return new Grid(rows, rows.length, rows[0].length);
	}

	static ofColumns<T>(...cols: T[][]): Grid<T> {
		if (this.isJagged(cols))
			throw new Error('cols cannot be jagged');

		const grid = this.createMatrix<T>(cols[0].length, cols.length);

		for (let c = 0; c < cols.length; c++) {
			for (let r = 0; r < cols[0].length; r++) {
				grid[r][c] = cols[c][r];
			}
		}

		return new Grid(grid, grid.length, grid[0].length);
	}

	// Static utility methods

	static isJagged<T>(arr: T[][]): boolean {
		const len = arr[0].length;
		for (let i = 1; i < arr.length; i++) {
			if (arr[i].length !== len) return true;
		}
		return false;
	}

	static createMatrix<T>(rows: number, cols: number): T[][] {
		return new Array(rows)
			.fill(null)
			.map(() => new Array<T>(cols).fill(null));
	}
}