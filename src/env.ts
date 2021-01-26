export type Language = 'java' | 'javascript' | 'typescript' | 'python';

export type Extension = 
	| typeof JavaExtension[number]
	| typeof TSExtension[number]
	| typeof JSExtension[number]
	| typeof PythonExtension[number];

export const JavaExtension		= ['java'] 						as const;
export const JSExtension		= ['js', 'mjs', 'cjs', 'jsx'] 	as const;
export const TSExtension		= ['ts', 'tsx'] 				as const;
export const PythonExtension	= ['py', 'py3', 'pyw'] 			as const;

export function isLanguage(str: string): str is Language {
	return ['java', 'javascript', 'typescript', 'python'].includes(str);
}

export function getLanguageFromExtension(ext: string): Language | null {
	if (ext === null) return null;
	
	ext = ext.replace('.', '');

	let res: Language | null = null;

	if (JavaExtension	.some(e => e === ext)) res = 'java';
	if (JSExtension		.some(e => e === ext)) res = 'javascript';
	if (TSExtension		.some(e => e === ext)) res = 'typescript';
	if (PythonExtension	.some(e => e === ext)) res = 'python';

	return res;
}