#!/usr/bin/env node
"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    Object.defineProperty(o, k2, { enumerable: true, get: function() { return m[k]; } });
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const cp = __importStar(require("child_process"));
const path_1 = __importDefault(require("path"));
const fs_1 = __importDefault(require("fs"));
const args = process.argv.slice(2);
if (args.length === 0) {
    console.log('usage: simp [-v | --version] [-h | --help] <command> [<args>]');
    console.log('\nList of common simp commands:');
    console.log('    help\t\tRead about a specific command');
    console.log('    init\t\tCreate an empty lib folder');
    console.log('    install\t\tImport a package or file to /lib from a specific language library');
    console.log('    drop\t\tRemove the specified library package or files from /lib');
    process.exit(0);
}
if (args[0] === '-v' || args[0] === '--version') {
    const raw = fs_1.default.readFileSync('./package.json', 'utf-8');
    const pkg = JSON.parse(raw);
    console.log(`self-import v${pkg.version}`);
    process.exit(0);
}
const commands = /help|init|install|drop/;
switch (args[0]) {
    case 'i':
        args[0] = 'install';
        break;
}
if (args[0].match(commands)) {
    cp.fork(`${path_1.default.resolve(__dirname, args[0])}.js`, args.slice(1), {
        cwd: process.cwd()
    });
}
else {
    console.error("error: command not found");
}
