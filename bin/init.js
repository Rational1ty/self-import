"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const fs_1 = __importDefault(require("fs"));
const args = process.argv.slice(2);
fs_1.default.mkdir(`${args[0] ? args[0] : '.'}/lib`, { recursive: true }, err => {
    if (err) {
        console.error('Initialization failed:');
        console.error(err);
    }
});
