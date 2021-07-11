import * as colors from './console_colors';
import { isTemplate } from './env';

const args = process.argv.slice(2);

if (args.length === 0 || !isTemplate(args[0])) {
    colors.error(`"${args[0]}" is not a valid template`);
    process.exit(0);
}

const template = args[0];
console.log(`Initializing project in current directory with template "${template}"`);