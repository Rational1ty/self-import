#!/usr/bin/env node

import fs from 'fs';
import { execSync } from 'child_process';
import * as commands from './command_utils';
import * as colors from './console_colors';

const args = process.argv.slice(2);

// general help
if (args.length === 0 || args[0] === '-h' || args[0] === '--help') {
    execSync(
        `node ${__dirname}/help.js`,
        { stdio: 'inherit' }
    );
    process.exit(0);
}

// version
if (args[0] === '-v' || args[0] === '--version') {
    const raw = fs.readFileSync('./package.json', 'utf-8');
    const pkg = JSON.parse(raw);
    console.log(`self-import v${pkg.version}`);
    process.exit(0);
}

// any invalid option / not a command
if (args[0].includes('-')) {
    colors.error('invalid flag or option');
    process.exit(0);
}

if (commands.isValidCommand(args[0])) {
    // If args[0] is an alias, change it to the corresponding command name (canonical name)
	args[0] = commands.getCommandFromAlias(args[0]);
	
	// if one of the arguments is a help flag
	if (args.includes('-h', 1) || args.includes('--help', 1)) {
		execSync(
			`node ${__dirname}/help.js ${args[0]}`,
			{ stdio: 'inherit' }
		);
	} else {
		// run command subprocess    
		execSync(
			`node ${__dirname}/${args[0]}.js ${args.slice(1).join(' ')}`,
			{ stdio: 'inherit' }
		);
	}
} else {
    colors.error('command not found');
}