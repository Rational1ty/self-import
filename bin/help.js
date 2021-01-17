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
const console_colors_1 = __importDefault(require("./console_colors"));
const commands = __importStar(require("./command_utils"));
const args = process.argv.slice(2);
if (args.length === 0) {
    console.log(`Usage: ${commands.obj.sim.usage}`);
    console.log('\nOptions:');
    console.log('    [-h | --help]\tView a general help page');
    console.log('    [-v | --version]\tDisplay current version number');
    console.log('\nCommands:');
    for (const cmd of commands.list) {
        console.log(`    ${cmd}\t\t${commands.obj[cmd].description}`);
    }
    process.exit(0);
}
if (args[0].match(commands.matcher())) {
    const name = commands.getCommandFromAlias(args[0]);
    const command = commands.obj[name];
    console.log(`Usage: simp ${name} ${command.usage}`);
    console.log(command.description);
    if (command.aliases?.length) {
        console.log(`Aliases: ${command.aliases.join(', ')}`);
    }
}
else {
    console_colors_1.default.error('help page not found');
}
