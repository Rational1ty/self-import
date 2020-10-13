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
const fs_1 = __importDefault(require("fs"));
const cp = __importStar(require("child_process"));
const path_1 = __importDefault(require("path"));
const console_colors_1 = __importDefault(require("./console_colors"));
const rootDir = path_1.default.resolve(__dirname.replace(/bin/, ''));
const moduleDirectories = cp.execSync('dir /b', {
    cwd: `${rootDir}/modules`,
    shell: process.env.ComSpec
});
const env = {
    root: rootDir,
    languages: moduleDirectories.toString('utf-8').split(/\r\n/).filter(s => s)
};
fs_1.default.writeFile(`${rootDir}/src/env.json`, JSON.stringify(env, null, '\t'), 'utf-8', (err) => {
    if (err) {
        console_colors_1.default.error(err.message);
        process.exit(9);
    }
    else {
        console.log(`Updated ${rootDir}\\src\\env.json`);
    }
});
fs_1.default.writeFile(`${rootDir}/bin/env.json`, JSON.stringify(env, null, '\t'), 'utf-8', (err) => {
    if (err) {
        console_colors_1.default.error(err.message);
        process.exit(9);
    }
    else {
        console.log(`Updated ${rootDir}\\bin\\env.json`);
    }
});
