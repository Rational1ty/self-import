import abc
import enum
from typing import Any, Generic, Iterable, TypeVar

import win32com.client as wincl

T = TypeVar('T')

ISpeechBaseStream = Any
SpObjectToken = Any
SpeechVisemeType = int


SP_MAX_WORD_LENGTH = 128
SP_MAX_PRON_LENGTH = 384


class SpeechRunState(enum.IntFlag):
	SRSWaiting = 0
	SRSDone = 1
	SRSIsSpeaking = 2


class SpeechVoiceEvents(enum.IntFlag):
	SVENoEvents = 0
	SVEStartInputStream = 2
	SVEEndInputStream = 4
	SVEVoiceChange = 8
	SVEBookmark = 16
	SVEWordBoundary = 32
	SVEPhoneme = 64
	SVESentenceBoundary = 128
	SVEViseme = 256
	SVEAudioLevel = 512
	SVEPrivate = 32768
	SVEAllEvents = 33790


class SpeechVisemeFeature(enum.IntFlag):
	SVFNone = 0
	SVFStressed = 1
	SVFEmphasis = 2


class SpeechVoicePriority(enum.IntFlag):
	SVPNormal = 0
	SVPAlert = 1
	SVPOver = 2


class SpeechVoiceSpeakFlags(enum.IntFlag):
	# SpVoice Flags
    SVSFDefault = 0
    SVSFlagsAsync = 1
    SVSFPurgeBeforeSpeak = 2
    SVSFIsFilename = 4
    SVSFIsXML = 8
    SVSFIsNotXML = 16
    SVSFPersistXML = 32

    # Normalizer Flags
    SVSFNLPSpeakPunc = 64

    # Masks
    SVSFNLPMask = 64
    SVSFVoiceMask = 127
    SVSFUnusedFlags = -128


class SpCollection(Generic[T]):
	def Item(self, index: int) -> T: ...


class SpPhoneConverter:
	LanguageId: int

	def IdToPhone(self, IdArray: int | list[int]) -> str: ...

	def PhoneToId(self, Phonemes: str) -> Iterable[int]: ...


class SpeechVoiceStatus:
	CurrentStreamNumber: int
	InputSentenceLength: int
	InputSentencePosition: int
	InputWordLength: int
	InputWordPosition: int
	LastBookmark: str
	LastBookmarkId: int
	LastHResult: int
	LastStreamNumberQueued: int
	PhonemeId: int
	RunningState: SpeechRunState
	VisemeId: int


class SpVoice:
	AlertBoundary: SpeechVoiceEvents
	AllowAudioOutputFormatChangesOnNextSet: bool
	AudioOutput: SpObjectToken
	AudioOutputStream: ISpeechBaseStream
	EventInterests: SpeechVoiceEvents
	Priority: SpeechVoicePriority
	Rate: int
	Status: SpeechVoiceStatus
	SynchronousSpeakTimeout: int
	Voice: SpObjectToken
	Volume: int

	def DisplayUI(self, hWndParent: int, Title: str, TypeOfUI: str, ExtraData: Any = None): ...

	def GetAudioOutputs(self,
		RequiredAttributes: str = '',
		OptionalAttributes: str = ''
	) -> SpCollection[SpObjectToken]: ...

	def GetVoices(self,
		RequiredAttributes: str = '',
		OptionalAttributes: str = ''
	) -> SpCollection[SpObjectToken]: ...

	def IsUISupported(self, TypeOfUI: str, ExtraData: Any = None) -> bool: ...

	def Pause(self): ...

	def Resume(self): ...

	def Skip(self, Type: str, NumItems: int) -> int: ...

	def Speak(self,
		Text: str,
		Flags: SpeechVoiceSpeakFlags = SpeechVoiceSpeakFlags.SVSFDefault
	) -> int: ...

	def SpeakCompleteEvent(self) -> int: ...

	def SpeakStream(self,
		Stream: ISpeechBaseStream,
		Flags: SpeechVoiceSpeakFlags = SpeechVoiceSpeakFlags.SVSFDefault
	) -> int: ...

	def WaitUntilDone(self, msTimeout: int) -> bool: ...


class SpeechVoiceEventHandler(abc.ABC):
	@staticmethod
	def _print_event(name: str, args: dict):
		arglist = ', '.join(f'{k}={v}' for k, v in args.items() if k != 'self')
		print(f'{name} ({arglist})')

	def OnAudioLevel(self, StreamNumber: int, StreamPosition: Any, AudioLevel: int):
		self._print_event('AudioLevel', locals())

	def OnBookmark(self, StreamNumber: int, StreamPosition: Any, Bookmark: str, BookmarkId: int):
		self._print_event('Bookmark', locals())

	def OnEndStream(self, StreamNumber: int, StreamPosition: Any):
		self._print_event('EndStream', locals())

	def OnEnginePrivate(self, StreamNumber: int, StreamPosition: int, EngineData: Any):
		self._print_event('EnginePrivate', locals())

	def OnPhoneme(self,
		StreamNumber: int,
		StreamPosition: Any,
		Duration: int,
		NextPhoneId: int,
		Feature: SpeechVisemeFeature,
		CurrentPhoneId: int
	):
		self._print_event('Phoneme', locals())

	def OnSentence(self, StreamNumber: int, StreamPosition: Any, CharacterPosition: int, Length: int):
		self._print_event('Sentence', locals())

	def OnStartStream(self, StreamNumber: int, StreamPosition: Any):
		self._print_event('StartStream', locals())

	def OnViseme(self,
		StreamNumber: int,
		StreamPosition: Any,
		Duration: int,
		NextVisemeId: SpeechVisemeType,
		Feature: SpeechVisemeFeature,
		CurrentVisemeId: SpeechVisemeType
	):
		self._print_event('Viseme', locals())

	def OnVoiceChange(self, StreamNumber: int, StreamPosition: Any, VoiceObjectToken: SpObjectToken):
		self._print_event('VoiceChange', locals())

	def OnWord(self, StreamNumber: int, StreamPosition: Any, CharacterPosition: int, Length: int):
		self._print_event('Word', locals())


def getvoice(eventhandler: type[SpeechVoiceEventHandler] = ...) -> SpVoice:
	if eventhandler is ...:
		return wincl.Dispatch('SAPI.SpVoice')
	return wincl.DispatchWithEvents('SAPI.SpVoice', eventhandler)


def getspeaker(voice: SpVoice, gender: str) -> SpObjectToken:
	male = gender.lower().startswith('m')
	return voice.GetVoices().Item(0 if male else 1)


def getphoneconverter() -> SpPhoneConverter:
	return wincl.Dispatch('SAPI.SpPhoneConverter')
