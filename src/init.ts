import fs from 'fs'
import { join } from 'path'
import * as colors from './console_colors'
import { isLanguage } from './env'

const args = process.argv.slice(2);
const modulePath = join(process.cwd(), 'self_modules');

fs.mkdir(modulePath, err => {
    if (err) {
        colors.error(`Initialization failed: ${err.message}`);
        return;
    }
    console.log('Created directory self_modules');
});

if (args.length === 0 || !isLanguage(args[0])) {
    colors.error(`expected <language> but got "${args[0]}"`);
    process.exit(0);
}

fs.writeFile(
    join(modulePath, '.lang'),
    args[0],
    err => {
        if (err) {
            colors.error(`Could not create language file: ${err.message}`);
            return;
        }
        console.log(`Wrote "${args[0]}" to self_modules/.lang`)
    }
);