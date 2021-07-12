import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { isLanguage, Language } from './env';

export const PACKAGE_PATH = path.join(__dirname, '..', 'packages');

export function validatePackage(str: string): [Language, string] {
	const [lang, pkg] = str.split('/');

	if (!lang || !pkg) {
		colors.error('missing argument for language/package');
		process.exit(0);
	}

	if (!isLanguage(lang)) {
		colors.error(`"${lang}" is not a valid language`);
		process.exit(0);
	}

	return [lang, pkg];
}

export function getPackageFiles(lang: Language, pkg: string): fs.PathLike[] {
	const dir = path.join(PACKAGE_PATH, lang);
	const packages = fs.readdirSync(dir);

	if (!packages.includes(pkg)) {
		colors.error(`package "${pkg}" does not exist`);
		process.exit(0);
	}

	return fs.readdirSync(path.join(dir, pkg));
}