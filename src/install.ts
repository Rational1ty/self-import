import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { getPackageFiles, PACKAGE_PATH, validatePackage } from './package';

const args = process.argv.slice(2);
const cwd = process.cwd();

if (args.length === 0) {
	colors.error('argument expected');
	process.exit(0);
}

for (const s of args) {
	const [lang, pkg] = validatePackage(s);

	const files = getPackageFiles(lang, pkg);

	const pkgSource = path.join(PACKAGE_PATH, lang, pkg);

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