import functools


def singleton(cls: type) -> type:
	original_init = cls.__init__

	@functools.wraps(cls.__init__)
	def __init__(self, create_key=None, *args, **kwargs):
		assert create_key is self._create_key, \
				f'{cls.__name__} cannot be directly instantiated. Use {cls.__name__}.getinstance() instead.'
		original_init(self, *args, **kwargs)

	@classmethod
	def getinstance(cls):
		if cls._instance is None:
			cls._instance = cls(cls._create_key)
		return cls._instance

	cls.__init__ = __init__
	cls._instance = None
	cls._create_key = object()
	cls.getinstance = getinstance

	return cls
