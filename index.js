#!/usr/bin/env node
"use strict";

const cp = require('child_process');
const fs = require('fs');

const args = process.argv.slice(2);

// General help
if (args.length === 0) {
    console.log('usage: simp [-v | --version] [-h | --help] <command> [<args>]');
    console.log('\nList of common simp commands:');
    console.log('    help\t\tRead about a specific command');
    console.log('    init\t\tCreate an empty lib folder');
    console.log('    install\t\tImport a package or file to /lib from a specific language library');
    console.log('    drop\t\tRemove the specified library package or files from /lib');
    process.exit(0);
}

// Version
if (args[0] === '-v' || args[0] === '--version') {
    const raw = fs.readFileSync('./package.json', 'utf-8');
    const pkg = JSON.parse(raw);
    console.log(`self-import v${pkg.version}`);
    process.exit(0);
}

const commands = /help|init|install|drop/;

// Aliases
switch (args[0]) {
    case 'i': args[0] = 'install'; break;
}

cp.fork(`./${args[0]}.js`, [args.slice(1)], {
    cwd: process.cwd()
});