import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { isLanguage } from './env';
import { getPackageFiles, PACKAGE_PATH, validatePackage } from './package';

const args = process.argv.slice(2);

if (args.length === 0) {
	colors.error('argument expected');
	process.exit(0);
}

// if argument is language/package
if (args[0].includes('/')) {
	const [lang, pkg] = validatePackage(args[0]);
	const files = getPackageFiles(lang, pkg);

	if (files.length === 0) {
		colors.warn(`No files exist yet in package "${lang}/${pkg}"`);
	}

	for (const f of files) {
		console.log(f);
	}
}

// if argument is language only
else {
	const lang = args[0];

	if (!isLanguage(lang)) {
		colors.error(`"${lang}" is not a language`);
		process.exit(0);
	}
	
	const pkgList = fs.readdirSync(path.join(PACKAGE_PATH, lang));
	
	if (pkgList.length === 0) {
		colors.warn(`No packages exist yet for "${lang}"`);
	}
	
	for (const pkg of pkgList) {
		console.log(pkg);
	}
}