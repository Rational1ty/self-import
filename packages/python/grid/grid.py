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
		return 'Grid.EMPTY_CELL'

	def __repr__(self) -> str:
		return '_EmptyCell.getinstance()'


class Grid:
	EMPTY_CELL = _EmptyCell.getinstance()

	def __init__(self, rows: int, cols: int, *elements):
		self._rows = rows
		self._cols = cols
		self._grid = []
		
		for y in range(rows):
			self._grid.append(_pad_tuple(elements[y * cols : (y + 1) * cols], cols))

	@classmethod
	def from_dimensions(cls, rows: int, cols: int):
		cls(rows, cols, (Grid.EMPTY_CELL for _ in range(rows * cols)))

	@classmethod
	def from_rows(cls, *rows: list):
		max_len = max(len(r) for r in rows)
		cls(len(rows), max_len, (_pad_list(r, max_len) for r in rows))

	def dimensions(self) -> tuple[int, int]:
		return self._rows, self._cols

	def print(self) -> None:
		maxwidth = self._longest_element_length()
		f = f' {{:<{maxwidth}}} '

		for r in range(self._rows):
			for c in range(self._cols):
				e = self._grid[r][c]
				print(f.format(str(e)), end='')
			print()
				
	# TODO: add extra characters (corners, etc) to ascii pprint
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

	def _isvalidpos(self, y: int, x: int) -> bool:
		if y < 0:
			y += self._rows
		if x < 0:
			x += self._cols

		return y >= 0 and y < self._rows and x >= 0 and x < self._cols

	def __repr__(self) -> str:
		elements = ", ".join(str(e) for e in self)
		return f'Grid({self._rows}, {self._cols}, {elements})'

	def __str__(self) -> str:
		return f'Grid ({self._rows}x{self._cols})'

	def __eq__(self, o: object) -> bool:
		if not isinstance(o, Grid):
			return False
		if self._rows != o._rows or self._cols != o._cols:
			return False
		return self._grid == o._grid

	def __len__(self):
		return self._rows * self._cols

	def __getitem__(self, pos: tuple[int, int]):
		y, x = pos
		if not self._isvalidpos(y, x):
			raise IndexError()
		return self._grid[y][x]

	def __setitem__(self, pos: tuple[int, int], value):
		y, x = pos
		if not self._isvalidpos(y, x):
			raise IndexError()
		self._grid[y][x] = value

	def __delitem__(self, pos: tuple[int, int]):
		y, x = pos
		if not self._isvalidpos(y, x):
			raise IndexError()
		self._grid[y][x] = Grid.EMPTY_CELL

	def __iter__(self):
		return GridIterator(self)

	def __reversed__(self):
		return GridIterator(self, True)


class GridIterator:
	def __init__(self, grid: Grid, reverse: bool = False):
		self._grid = grid
		self._forward = not reverse
		self._x = 0 if self._forward else self._grid._rows - 1
		self._y = 0 if self._forward else self._grid._cols - 1

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


if __name__ == '__main__':
	g = Grid(3, 3, 'hello', 'these', 'are', 'the', 'elements', 'of', 'the', 'grid')
	# g = Grid(3, 3, 6, 27, 31, 400, 92, 5, 21, 733, 64)
	g.pprint(True, False)