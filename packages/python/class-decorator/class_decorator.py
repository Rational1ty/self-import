import functools as ft
import inspect
import re
from typing import TypeVar

T = TypeVar('T')


def singleton(cls: type[T]) -> type[T]:
	@staticmethod
	@ft.wraps(cls.__new__)
	def new(cls: type[T]) -> T:
		if cls._instance is None:
			cls._instance = super(cls, cls).__new__(cls)
		return cls._instance

	@classmethod
	def getinstance(cls: type[T]) -> T:
		return cls()

	cls.__new__ = new
	cls._instance = None
	cls.getinstance = getinstance

	return cls


def typestub(cls: type) -> type:
	class ImplementedError(Exception): pass

	methods = inspect.getmembers(cls, inspect.isfunction)

	for name, fn_obj in methods:
		code = inspect.cleandoc(inspect.getsource(fn_obj))
		signature = re.sub(r'\s+', ' ', code.replace('\n', ''))
		
		emptyfn_regex = r'def .+\(.*\)(?:\s*->\s*.+)?:\s*(?:\.\.\.|pass|"[\s\S]*")'
		empty = re.fullmatch(emptyfn_regex, signature)
		
		if not empty:
			raise ImplementedError(f'{cls.__name__}.{name}: implementation not allowed in stub type')
	
	return cls
