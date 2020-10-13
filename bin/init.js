"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const fs_1 = __importDefault(require("fs"));
const console_colors_1 = __importDefault(require("./console_colors"));
const args = process.argv.slice(2);
fs_1.default.mkdir(`${process.cwd()}/${args[0] ?? '.'}/${'self-modules'}`, { recursive: true }, err => {
    if (err) {
        console_colors_1.default.error(`Initialization failed: ${err.message}`);
    }
});
