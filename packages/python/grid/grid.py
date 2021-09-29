"""
This module provides access to the `Grid` type, which can be used to represent
2-dimensional data.
"""


import itertools as it
from typing import Callable, Final, Generator, Optional, Union, Generic, TypeVar


# Type aliases
Position = tuple[int, int]
Dimensions = tuple[int, int]
UnaryOperator = Callable[[object], object]
Predicate = Callable[[object], bool]
Consumer = Callable[[object], None]

T = TypeVar('T')


class _EmptyCell:
	_instance = None
	_create_key = object()

	def __init__(self, create_key):
		assert create_key is _EmptyCell._create_key, \
			'_EmptyCell cannot be instantiated'

	@classmethod
	def getinstance(cls):
		if cls._instance is None:
			cls._instance = cls(cls._create_key)
		return cls._instance
		
	def __str__(self) -> str:
		return 'EMPTY_CELL'

	def __repr__(self) -> str:
		return 'Grid.EMPTY_CELL'


class Grid(Generic[T]):
	"""
	A fixed-size rectangular grid.
	"""

	EMPTY_CELL: Final = _EmptyCell.getinstance()
	"""
	Represents an empty grid cell. This object is the same across all `Grid` instances.
	"""

	def __init__(self, rows: int, cols: int, *elements: T):
		"""
		Creates a new Grid

		Args:
			rows (int): the number of rows in the grid
			cols (int): the number of columns in the grid
			*elements (T): the items to place in the grid
		"""
		self._rows = rows
		self._cols = cols
		self._grid = []
		
		for y in range(rows):
			self._grid.append(_pad_tuple(elements[y * cols : (y + 1) * cols], cols))

	@classmethod
	def from_rows(cls, *rows: list) -> 'Grid':
		"""
		Creates a new Grid from a list of rows.

		If the rows are not all the same length, they will be padded with 
		Grid.EMPTY_CELL elements to the length of the longest row.

		Args:
			*rows (list): the rows to create the grid from

		Returns:
			Grid: the grid
		"""
		max_len = max(len(r) for r in rows)
		return cls(len(rows), max_len, (_pad_list(r, max_len) for r in rows))

	def dimensions(self) -> Dimensions:
		"""
		Gets the dimensions of this grid.

		Returns:
			Dimensions: the number of rows and columns in this grid (rows, cols)
		"""
		return self._rows, self._cols

	def pprint(self, use_unicode: bool = True, use_thin: bool = True):
		"""
		Print the contents of this grid to the standard output stream with 
		extra formatting (\"pretty-prints\" the grid).

		The output is a grid of equally-sized cells divided by grid lines. Each
		element is centered within its space in the grid, and has at least 1
		space of padding on each side.

		Args:
			use_unicode (bool, optional): use unicode characters in output; defaults to `True`
			use_thin (bool, optional): use thin box drawing characters in output; defaults to `True`
		"""
		maxwidth = self._longest_element_length()
		divider = ('│' if use_thin else '┃') if use_unicode else '|'
		f = f' {{:^{maxwidth}}} {divider}'

		print(self._gridrow('top', maxwidth, use_unicode, use_thin))
		
		for r in range(self._rows):
			print(divider, end='')

			for c in range(self._cols):
				e = self._grid[r][c]
				print(f.format(str(e)), end='')
			
			print()
			if r < self._rows - 1:
				print(self._gridrow('middle', maxwidth, use_unicode, use_thin))

		print(self._gridrow('bottom', maxwidth, use_unicode, use_thin))

	def _gridrow(
		self,
		location: str,
		data_width: int,
		use_unicode: bool,
		use_thin: bool
	) -> str:
		between = (('─' if use_thin else '━') if use_unicode else '-') * (data_width + 2)
		start = mid = end = '+'

		if use_unicode:
			if location == 'top':
				start = '┌' if use_thin else '┏'
				mid = '┬' if use_thin else '┳'
				end = '┐' if use_thin else '┓'
			elif location == 'middle':
				start = '├' if use_thin else '┣'
				mid = '┼' if use_thin else '╋'
				end = '┤' if use_thin else '┫'
			elif location == 'bottom':
				start = '└' if use_thin else '┗'
				mid = '┴' if use_thin else '┻'
				end = '┘' if use_thin else '┛'

		row = start
		for i in range(self._cols):
			row += between
			if i < self._cols - 1:
				row += mid
		row += end

		return row

	def _longest_element_length(self) -> int:
		return max(len(str(e)) for e in self)

	def add(self, o: T) -> bool:
		"""
		Adds the specified element to this grid.

		The element is inserted into the first empty cell.

		Args:
			o (T): the element to add

		Returns:
			bool: `True` if the element was inserted successfully, otherwise `False`
		"""
		for e, r, c in self.elements():
			if e is Grid.EMPTY_CELL:
				self._grid[r][c] = o
				return True
		return False

	def addall(self, *items: T) -> bool:
		"""
		Adds all of the provided elements into this grid.

		The elements are inserted one-by-one into each empty cell until there are no empty cells
		left or there are no elements remaining.

		Args:
			*items (T): the elements to add

		Returns:
			bool: `True` if all of the elements were inserted successfully, otherwise `False`
		"""
		item_iter = iter(items)

		for e, r, c in self.elements():
			if e is Grid.EMPTY_CELL:
				try:
					self._grid[r][c] = next(item_iter)
				except StopIteration:
					return True

		try:
			next(item_iter)
		except StopIteration:
			return True
		return False
		
	def clear(self):
		"""
		Clears this grid (all cells are set to `Grid.EMPTY_CELL`).
		"""
		self.replaceall(lambda _: Grid.EMPTY_CELL)

	def fill(self, o: T):
		"""
		Fills this grid with the specified element.

		This is the same as calling `grid.replaceall(lambda _: o)`.

		Args:
			o (T): the object to fill this grid with
		"""
		self.replaceall(lambda _: o)

	def remove(self, o: object) -> bool:
		"""
		Removes the specified object from this grid, if it exists.

		Args:
			o (object): the object to remove

		Returns:
			bool: `True` if the element was removed, otherwise `False`
		"""
		for e, r, c in self.elements():
			if e == o:
				self._grid[r][c] = Grid.EMPTY_CELL
				return True
		return False

	def removeall(self, *items: object):
		"""
		Removes all of the specified items from this grid.

		Args:
			*items (object): the items to be removed
		"""
		for e, r, c in self.elements():
			if e not in items: continue
			self._grid[r][c] = Grid.EMPTY_CELL

	def replaceall(self, replacefn: UnaryOperator):
		"""
		Replaces every element in this grid with the result of calling `replacefn` on that element.

		Args:
			replacefn (UnaryOperator): mapping function to call for each element
		"""
		for e, r, c in self.elements():
			if e is Grid.EMPTY_CELL: continue
			self._grid[r][c] = replacefn(e)

	def removeif(self, filter: Predicate):
		"""
		Removes all of the elements from this grid that satisfy the given predicate.

		More specifically, removes each element `e` for which `filter(e) == True`

		Args:
			filter (Predicate): function used to filter elements
		"""
		for e, r, c in self.elements():
			if e is Grid.EMPTY_CELL: continue
			if filter(e):
				self._grid[r][c] = Grid.EMPTY_CELL

	def retainall(self, *items: object):
		"""
		Removes all of the elements in this grid that are not contained in the specified list of
		items.

		Args:
			*items (object): the items to keep
		"""
		for e, r, c in self.elements():
			if e in items: continue
			self._grid[r][c] = Grid.EMPTY_CELL

	def foreach(self, action: Consumer):
		"""
		Performs the given action for each element in this grid.

		The grid is not mutated as a result of this method.

		Args:
			action (Consumer): the action to perform for each element
		"""
		for e in self:
			action(e)

	def positionof(self, o: T) -> Optional[Position]:
		"""
		Finds the position of the first occurrence of the specified object in this grid.

		Args:
			o (T): the object to find the position of

		Returns:
			Position: the position of the object, or `None` if it was not found
		"""
		for e, r, c in self.elements():
			if e == o:
				return r, c
		return None

	def lastpositionof(self, o: T) -> Optional[Position]:
		"""
		Finds the position of the last occurrence of the specified object in this grid.

		Args:
			o (T): the object to find the position of

		Returns:
			Position: the last position of the object, or `None` if it was not found
		"""
		for e, r, c in self.elements(True):
			if e == o:
				return r, c
		return None

	def getadjacent(
		self,
		row: int,
		col: int,
		include_diagonals: bool = False
	) -> list[T]:
		"""
		Gets all of the elements adjacent to the specified cell, without wrapping.

		Args:
			row (int): the row of the cell
			col (int): the column of the cell
			include_diagonals (bool, optional): indicates whether or not to include elements that
				are adjacent diagonally; defaults to `False`

		Returns:
			list[T]: list of adjacent elements
		"""

		# helper function for filtering out None elements
		not_none = lambda e: e is not None

		row_max, col_max = self._rows - 1, self._cols - 1
		
		# adjacent to edges (top, left, bottom, right)
		neighbors = filter(not_none, (
			self._grid[row - 1][col] if row > 0 else None,
			self._grid[row][col - 1] if col > 0 else None,
			self._grid[row + 1][col] if row < row_max else None,
			self._grid[row][col + 1] if col < col_max else None
		))

		if not include_diagonals:
			return list(neighbors)

		# adjacent diagonally (top-left, top-right, bottom-left, bottom-right)
		diagonals = filter(not_none, (
			self._grid[row - 1][col - 1] if row > 0 and col > 0 else None,
			self._grid[row - 1][col + 1] if row > 0 and col < col_max else None,
			self._grid[row + 1][col - 1] if row < row_max and col > 0 else None,
			self._grid[row + 1][col + 1] if row < row_max and col < col_max else None
		))

		return [*neighbors, *diagonals]

	def elements(self, reverse: bool = False) -> Generator[tuple[T, int, int], None, None]:
		"""
		Returns all of the elements of this grid along with their positions.

		Args:
			reverse (bool, optional): if `True`, indicates reverse iteration; defaults to `False`

		Yields:
			tuple[T, int, int]: the next element and its position `(element, row, col)`
		"""
		return ((self._grid[r][c], r, c) for r, c in self.prod_rc(reverse))

	def prod_rc(self, reverse: bool = False) -> it.product:
		"""
		Returns the product of this grid's rows and columns.

		The result of this method is an `itertools.product` containing all of the valid
		`Position`s in this grid.

		Args:
			reverse (bool, optional): if `True`, indicates reverse iteration; defaults to `False`

		Returns:
			itertools.product[Position]: `product` object of all possible `(row, col)` combinations
		"""
		return it.product(range(self._rows), range(self._cols)) if not reverse \
		else it.product(reversed(range(self._rows)), reversed(range(self._cols)))

	def __repr__(self) -> str:
		elements = ', '.join(repr(e) for e in self)
		return f'Grid({self._rows}, {self._cols}, {elements})'

	def __str__(self) -> str:
		return f'Grid ({self._rows}x{self._cols})\n{self._bare_grid()}'

	def _bare_grid(self) -> str:
		maxwidth = self._longest_element_length()
		f = f' {{:<{maxwidth}}} '
		res = ''

		for row in self._grid:
			for e in row:
				res += f.format(str(e))
			res += '\n'

		return res

	def __eq__(self, o: object) -> bool:
		if not isinstance(o, Grid):
			return False
		if self._rows != o._rows or self._cols != o._cols:
			return False
		return self._grid == o._grid

	def __len__(self) -> int:
		return self._rows * self._cols

	def __getitem__(self, pos: Union[Position, slice]) -> Union[T, 'Grid']:
		if isinstance(pos, slice):
			gs = GridSlice(self, pos)
			sliced = (self._grid[r][c] for r, c in gs.indices())
			return Grid(len(gs.rows()), len(gs.cols()), *sliced)

		r, c = pos
		if not self._isvalidpos(r, c):
			raise IndexError()

		return self._grid[r][c]

	def __setitem__(self, pos: Union[Position, slice], value: object):
		if isinstance(pos, slice):
			gs = GridSlice(self, pos)
			fill = self._getfill(gs, value)
			fs = GridSlice.all(fill)

			for slice_pos, fill_pos in zip(gs.indices(), fs.indices()):
				slice_r, slice_c = slice_pos
				fill_r, fill_c = fill_pos
				self._grid[slice_r][slice_c] = fill._grid[fill_r][fill_c]
			return

		r, c = pos
		if not self._isvalidpos(r, c):
			raise IndexError()

		self._grid[r][c] = value

	def __delitem__(self, pos: Union[Position, slice]):
		if isinstance(pos, slice):
			gs = GridSlice(self, pos)
			for r, c in gs.indices():
				self._grid[r][c] = Grid.EMPTY_CELL
			return

		r, c = pos
		if not self._isvalidpos(r, c):
			raise IndexError()

		self._grid[r][c] = Grid.EMPTY_CELL

	def _isvalidpos(self, r: int, c: int) -> bool:
		if r < 0:
			r += self._rows
		if c < 0:
			c += self._cols

		return 0 <= r < self._rows and 0 <= c < self._cols

	def _getfill(self, gs, val):
		n_rows, n_cols = gs.dimensions()

		if isinstance(val, Grid):
			if val._rows == n_rows and val._cols == n_cols:
				return val
			raise ValueError()
		
		return Grid(n_rows, n_cols, *(val for _ in range(n_rows * n_cols)))

	def __iter__(self) -> Generator[T, None, None]:
		return (self._grid[r][c] for r, c in self.prod_rc())

	def __reversed__(self) -> Generator[T, None, None]:
		return (self._grid[r][c] for r, c in self.prod_rc(True))


class Grids:
	...


class GridSlice:
	"""
	Represents a slice of a grid.

	Similar to the built-in `slice` type, but can be used in two dimensions.

	Attributes:
		start_y (int): row to start on (inclusive); defaults to `0`
		start_x (int): column to start on (inclusive); defaults to `0`
		stop_y (int): row to stop at (exclusive); defaults to `grid.dimensions()[0]`
		stop_x (int): column to stop at (exclusive); defaults to `grid.dimensions()[1]`
		step_y (int): step for rows; defaults to `1`
		step_x (int): step for columns; defaults to `1`
	"""

	def __init__(self, g: Grid, s: slice):
		"""
		Creates a `GridSlice`.

		The start, stop, and step attributes of the `slice` argument can be of type `Position`,
		`int`, or `None`. Here is an example of how different types of slice arguments are handled:

			If `s.stop` is `None`, `stop_y` and `stop_x` use the default value.\n
			If `s.stop` is an `int`, `stop_y = s.stop` and `stop_x` uses the default value.\n
			If `s.stop` is a `tuple[int, int]`, `stop_y, stop_x = s.stop`

		Args:
			g (Grid): the grid to slice
			s (slice): the `slice` object used to create the `GridSlice`

		Raises:
			ValueError: if the slice arguments are not of type `Position`, `int`, or `None`
			IndexError: if `s.start` or `s.stop` do not contain valid indexes
		"""
		self.start_y, self.start_x = self._validatestart(g, *self._getsliceparam(s.start, (0, 0)))
		self.stop_y, self.stop_x = self._validatestop(g, *self._getsliceparam(s.stop, (g._rows, g._cols)))
		self.step_y, self.step_x = self._getsliceparam(s.step, (1, 1))

	@classmethod
	def all(cls, g: Grid):
		"""
		Slices all of the elements from the given grid.

		Args:
			g (Grid): the grid

		Returns:
			GridSlice: a slice of all of the elements in the grid
		"""
		return cls(g, slice(None, None, None))

	def indices(self) -> it.product:
		"""
		Gets the positions of the elements included in this slice.

		Returns:
			itertools.product[Position]: the positions of the elements
		"""
		return it.product(self.rows(), self.cols())

	def dimensions(self) -> Dimensions:
		"""
		Gets the dimensions of the resultant grid of this slice.

		Returns:
			Dimensions: the number of rows and columns in the slice
		"""
		return len(self.rows()), len(self.cols())

	def rows(self) -> range:
		"""
		Gets the indexes of the rows included in this slice.

		Returns:
			range: the rows in this slice
		"""
		return range(self.start_y, self.stop_y, self.step_y)

	def cols(self) -> range:
		"""
		Gets the indexes of the columns included in this slice.

		Returns:
			range: the columns in this slice
		"""
		return range(self.start_x, self.stop_x, self.step_x)

	@staticmethod
	def _validatestart(g: Grid, r: int, c: int) -> Position:
		if r >= g._rows or c >= g._cols:
			raise IndexError()

		if r < 0:
			r += g._rows
		if c < 0:
			c += g._cols

		return r, c

	@staticmethod
	def _validatestop(g: Grid, r: int, c: int) -> Position:
		if r > g._rows or c > g._cols:
			raise IndexError()

		if r < 0:
			r += g._rows
		if c < 0:
			c += g._cols

		return r, c

	def _getsliceparam(self, sp, defaults: tuple[int, int]) -> Position:
		if sp is None:
			return defaults
		if isinstance(sp, int):
			return sp, defaults[1]
		if isinstance(sp, tuple) and len(sp) == 2:
			return sp
		raise ValueError()


def _pad_list(lst: list, length) -> list:
	diff = length - len(lst)
	if diff > 0:
		lst.extend(Grid.EMPTY_CELL for _ in range(diff))
	return lst


def _pad_tuple(tup: tuple, length) -> list:
	diff = length - len(tup)
	if diff > 0:
		return [*tup, *(Grid.EMPTY_CELL for _ in range(diff))]
	return list(tup)
