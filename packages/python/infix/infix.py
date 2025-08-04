from typing import Any, Callable, Generic, TypeVar

R = TypeVar('R')
S = TypeVar('S')
T = TypeVar('T')


# Usage:
#	add = Infix(lambda a, b: a + b)
# 	sum = a |add| b
# 	sum = a <<add>> b
class Infix(Generic[S, T, R]):
	def __init__(self, fn: Callable[[S, T], R]):
		self.fn = fn

	def __or__(self, other: T) -> R:
		return self.fn(other)

	def __ror__(self, other: S) -> 'Infix':
		# TODO: see if s and o can be removed because of closure
		return Infix(lambda x, s=self, o=other: s.fn(o, x))

	def __rshift__(self, other: T) -> R:
		return self.fn(other)

	def __rlshift__(self, other: S) -> 'Infix':
		# TODO: see if s and o can be removed because of closure
		return Infix(lambda x, s=self, o=other: s.fn(o, x))

	def __call__(self, a: S, b: R) -> R:
		return self.fn(a, b)
		