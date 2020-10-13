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
const fs_1 = __importDefault(require("fs"));
const commands = __importStar(require("./command_utils"));
const console_colors_1 = __importDefault(require("./console_colors"));
const env_json_1 = __importDefault(require("./env.json"));
const args = process.argv.slice(2);
if (args.length === 0 || args[0] === '-h' || args[0] === '--help') {
    cp.execSync(`node ${env_json_1.default.root}/bin/help.js`, { stdio: 'inherit' });
    process.exit(0);
}
if (args[0] === '-v' || args[0] === '--version') {
    const raw = fs_1.default.readFileSync('./package.json', 'utf-8');
    const pkg = JSON.parse(raw);
    console.log(`self-import v${pkg.version}`);
    process.exit(0);
}
if (args[0].includes('-')) {
    console_colors_1.default.error('invalid flag or option');
    process.exit(9);
}
if (commands.isValidCommand(args[0])) {
    args[0] = commands.getCommandFromAlias(args[0]);
    cp.execSync(`node ${env_json_1.default.root}/bin/${args[0]}.js ${args.slice(1).join(' ')}`, { cwd: process.cwd(), stdio: 'inherit' });
}
else {
    console_colors_1.default.error('command not found');
}
