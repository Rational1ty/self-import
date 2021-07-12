import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { isLanguage, Language } from './env';

const args = process.argv.slice(2);
const MODULE_PATH = path.join(__dirname, '..', 'modules');

if (args.length === 0) {
	colors.error(`expected argument after "install"`);
	process.exit(0);
}

for (const s of args) {
	const [lang, pkg] = validateArg(s);

	const files = getPackageFiles(lang, pkg);

	const pkgSource = path.join(MODULE_PATH, lang, pkg);
	const cwd = process.cwd();

	for (const f of files) {
		const fileName = f.toString();

		const src = path.join(pkgSource, fileName);
		const dest = path.join(cwd, fileName);

		fs.copyFile(src, dest, err => {
			if (err) {
				colors.error('failed to copy file');
				console.error(err);
			}
		})
	}
}

function validateArg(str: string): [Language | 'meta', string] {
	const [lang, pkg] = str.split('/');

	if (!lang || !pkg) {
		colors.error('missing argument for language/package');
		process.exit(0);
	}

	if (lang !== 'meta' && !isLanguage(lang)) {
		colors.error(`"${lang}" is not a valid language`);
		process.exit(0);
	}

	return [lang, pkg];
}

function getPackageFiles(lang: Language | 'meta', pkg: string): fs.PathLike[] {
	const dir = path.join(MODULE_PATH, lang);
	const packages = fs.readdirSync(dir);

	if (!packages.includes(pkg)) {
		colors.error(`package "${pkg}" does not exist`);
		process.exit(0);
	}

	return fs.readdirSync(path.join(dir, pkg));
}