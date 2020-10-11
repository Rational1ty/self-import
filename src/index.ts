#!/usr/bin/env node

import * as cp from 'child_process'
import fs from 'fs'
import * as commands from './command_utils'
import Colors from './console_colors'
import env from './env.json'

const args = process.argv.slice(2);

// General help
if (args.length === 0 || args[0] === '-h' || args[0] === '--help') {
    console.log(
        cp.execSync(
            'node ./bin/help.js',
            { cwd: env.root }
        ).toString('utf-8')
    )
    process.exit(0)
}

// Version
if (args[0] === '-v' || args[0] === '--version') {
    const raw = fs.readFileSync('./package.json', 'utf-8')
    const pkg = JSON.parse(raw)
    console.log(`self-import v${pkg.version}`)
    process.exit(0)
}

if (args[0].includes('-')) {
    Colors.error('invalid flag or option')
    process.exit(env.codes.caughtException);
}

if (commands.isValidCommand(args[0])) {
    // If args[0] is an alias, change it to the corresponding command name (canonical name)
    args[0] = commands.getCommandFromAlias(args[0])

    // Run command subprocess    
    console.log(
        cp.execSync(
            `node ./bin/${args[0]}.js ${args.slice(1).join(' ')}`,
            { cwd: env.root }
        ).toString('utf-8')
    )
} else {
    Colors.error('command not found')
}