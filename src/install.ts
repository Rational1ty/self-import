import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { isLanguage, Language } from './env';

const args = process.argv.slice(2);
const PACKAGE_PATH = path.join(__dirname, '..', 'packages');

if (args.length === 0) {
	colors.error('argument expected');
	process.exit(0);
}

for (const s of args) {
	const [lang, pkg] = validateArg(s);

	const files = getPackageFiles(lang, pkg);

	const pkgSource = path.join(PACKAGE_PATH, lang, pkg);
	const cwd = process.cwd();

	for (const f of files) {
		const fileName = f.toString();
		console.log(`Installing "${fileName}"`)

		const src = path.join(pkgSource, fileName);
		const dest = path.join(cwd, fileName);

		fs.copyFile(src, dest, err => {
			if (err) {
				colors.error(`failed to copy "${fileName}"`);
				console.error(err);
			}
		});
	}

	console.log();
}

colors.done(`Package${args.length > 1 ? 's' : ''} installed successfully`);

function validateArg(str: string): [Language, string] {
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

function getPackageFiles(lang: Language, pkg: string): fs.PathLike[] {
	const dir = path.join(PACKAGE_PATH, lang);
	const packages = fs.readdirSync(dir);

	if (!packages.includes(pkg)) {
		colors.error(`package "${pkg}" does not exist`);
		process.exit(0);
	}

	return fs.readdirSync(path.join(dir, pkg));
}