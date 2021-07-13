import fs from 'fs';
import path from 'path';
import * as colors from './console_colors';
import { getPackageFiles, validatePackage } from './package';

const args = process.argv.slice(2);
const cwd = process.cwd();

if (args.length === 0) {
	colors.error('argument expected');
	process.exit(0);
}

for (const s of args) {
	const [lang, pkg] = validatePackage(s);
	const pkgFiles = getPackageFiles(lang, pkg);

	console.log(`Removing "${pkg}"`);

	const cwdFiles = fs.readdirSync(process.cwd())

	for (const f of cwdFiles) {
		if (!pkgFiles.includes(f)) continue;
		
		console.log(` | ${f}`);

		fs.unlink(path.join(cwd, f), err => {
			if (err && err.code !== 'ENOENT') {
				colors.error(`failed to delete "${f}"`);
			}
		});
	}

	console.log();
}

colors.done(`Package${args.length > 1 ? 's' : ''} removed successfully`);