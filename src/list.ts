import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { isLanguage } from './env';
import { PACKAGE_PATH } from './package';

const args = process.argv.slice(2);

if (args.length === 0) {
	colors.error('argument expected');
	process.exit(0);
}

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