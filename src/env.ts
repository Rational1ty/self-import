const languages = ['c', 'cpp', 'java', 'javascript', 'json', 'python', 'typescript'] as const;
export type Language = typeof languages[number];

export function isLanguage(str: string): str is Language {
	return (languages as ReadonlyArray<string>).includes(str);
}

const templates = ['react'] as const;
export type Template = typeof templates[number];

export function isTemplate(str: string): str is Template {
	return (templates as ReadonlyArray<string>).includes(str);
}