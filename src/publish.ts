import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { PACKAGE_PATH, validatePackage } from './package';

const args = process.argv.slice(2);
const cwd = process.cwd();

if (args.length === 0) {
	colors.error('argument expected');
	process.exit(0);
}

const [lang, pkg] = validatePackage(args[0]);
const src = path.join(cwd, args[1] ?? '.');

console.log(`Publishing "${pkg}"`);

// create package directory in repo
const dest = path.join(PACKAGE_PATH, lang, pkg);
fs.mkdirSync(dest, { recursive: true });

// get file names to copy
const files = fs.readdirSync(src);

// copy files from source directory to repo
for (const f of files) {
	console.log(` | ${f}`);

	fs.copyFile(path.join(src, f), path.join(dest, f), err => {
		if (err) {
			colors.error(`failed to copy "${f}"`);
			console.error(err);
		}
	});
}

console.log();
colors.done('Package published successfully');