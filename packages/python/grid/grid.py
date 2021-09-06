import itertools as it


Indices = tuple[tuple[int, int], ...]
Position = tuple[int, int]


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


class Grid:
	EMPTY_CELL = _EmptyCell.getinstance()

	def __init__(self, rows: int, cols: int, *elements):
		self._rows = rows
		self._cols = cols
		self._grid = []
		
		for y in range(rows):
			self._grid.append(_pad_tuple(elements[y * cols : (y + 1) * cols], cols))

	@classmethod
	def from_rows(cls, *rows: list):
		max_len = max(len(r) for r in rows)
		return cls(len(rows), max_len, (_pad_list(r, max_len) for r in rows))

	def dimensions(self) -> tuple[int, int]:
		return self._rows, self._cols
				
	def pprint(self, use_unicode: bool = True, use_thin: bool = True) -> None:
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

	def _gridrow(self, 
			location: str, 
			data_width: int, 
			use_unicode: bool, 
			use_thin: bool) -> str:
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

	def __len__(self):
		return self._rows * self._cols

	def __getitem__(self, pos):
		if isinstance(pos, slice):
			gs = GridSlice(self, pos)
			sliced = (self._grid[r][c] for r, c in gs.indices())
			return Grid(len(gs.rows()), len(gs.cols()), *sliced)

		r, c = pos
		if not self._isvalidpos(r, c):
			raise IndexError()

		return self._grid[r][c]

	def __setitem__(self, pos, value):
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

	def __delitem__(self, pos):
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

	def __iter__(self):
		return GridIterator(self)

	def __reversed__(self):
		return GridIterator(self, True)


class GridIterator:
	def __init__(self, g: Grid, reverse: bool = False):
		self._grid = g
		self._forward = not reverse
		self._x = 0 if self._forward else g._rows - 1
		self._y = 0 if self._forward else g._cols - 1

	def __iter__(self):
		return self

	def __next__(self):
		return self._next_forward() if self._forward else self._next_reverse()

	def _next_forward(self):
		if self._y >= self._grid._rows:
			raise StopIteration()

		element = self._grid[self._y, self._x]

		self._x += 1
		if self._x >= self._grid._cols:
			self._y += 1
			self._x = 0

		return element

	def _next_reverse(self):
		if self._y < 0:
			raise StopIteration()

		element = self._grid[self._y, self._x]

		self._x -= 1
		if self._x < 0:
			self._y -= 1
			self._x = self._grid._cols - 1

		return element


class GridSlice:
	def __init__(self, g: Grid, s: slice):
		self.start_y, self.start_x = self._validatestart(g, *self._getsliceparam(s.start, (0, 0)))
		self.stop_y, self.stop_x = self._validatestop(g, *self._getsliceparam(s.stop, (g._rows, g._cols)))
		self.step_y, self.step_x = self._getsliceparam(s.step, (1, 1))

	@classmethod
	def all(cls, g: Grid):
		return cls(g, slice(None, None, None))

	def indices(self) -> Indices:
		return tuple(it.product(self.rows(), self.cols()))

	def dimensions(self):
		return len(self.rows()), len(self.cols())

	def rows(self) -> tuple[int, ...]:
		return tuple(i for i in range(self.start_y, self.stop_y, self.step_y))

	def cols(self) -> tuple[int, ...]:
		return tuple(i for i in range(self.start_x, self.stop_x, self.step_x))

	@staticmethod
	def _validatestart(g: Grid, r: int, c: int) -> Position:
		if r >= g._rows or c >= g._rows:
			raise IndexError()

		if r < 0:
			r += g._rows
		if c < 0:
			c += g._cols

		return r, c

	@staticmethod
	def _validatestop(g: Grid, r: int, c: int) -> Position:
		if r > g._rows or c > g._rows:
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