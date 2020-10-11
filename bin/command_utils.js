"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.getCommandFromAlias = exports.matcher = exports.isValidCommand = exports.list = exports.obj = void 0;
const commands_json_1 = __importDefault(require("./commands.json"));
exports.obj = commands_json_1.default;
exports.list = Object.keys(exports.obj).slice(1);
function isValidCommand(command) {
    return command.match(matcher()) ? true : false;
}
exports.isValidCommand = isValidCommand;
function matcher() {
    const commandOrAlias = exports.list.map(key => [key].concat(exports.obj[key].aliases ?? []))
        .flat()
        .join('|');
    return new RegExp(`\\b(${commandOrAlias})\\b`, 'i');
}
exports.matcher = matcher;
function getCommandFromAlias(alias) {
    if (alias in exports.obj)
        return alias;
    for (const cmd of exports.list) {
        for (const a of exports.obj[cmd].aliases ?? []) {
            if (alias === a)
                return cmd;
        }
    }
    return alias;
}
exports.getCommandFromAlias = getCommandFromAlias;
