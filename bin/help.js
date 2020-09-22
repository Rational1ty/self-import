"use strict";
const args = process.argv.slice(2);
if (args.length === 0) {
    console.log("usage: simp [-v | --version] [-h | --help] <command> [<args>]");
    console.log("\nList of common simp commands:");
    console.log("    help\t\tRead about a specific command");
    console.log("    init\t\tCreate an empty lib folder");
    console.log("    install\t\tImport a package or file to /lib from a specific language library");
    console.log("    drop\t\tRemove the specified library package or files from /lib");
    process.exit(0);
}
switch (args[0]) {
    case "help":
        console.log("usage: simp help [<command>]");
        break;
    case "init":
        console.log("usage: simp init [<dir>]");
        break;
    case "install":
        console.log("usage: simp install <language>/[<file> | <package>]");
        console.log("aliases: simp i");
        break;
    case "drop":
        console.log("usage: simp drop <language>/[<file> | <package>]");
        break;
    default:
        console.error("error: help page not found");
}
