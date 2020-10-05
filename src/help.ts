const args = process.argv.slice(2);

if (args.length === 0) {
    console.log("Usage: simp [-v | --version] <command> [<args>]");
    console.log("\nList of common simp commands:");
    console.log("    help\t\tRead about a specific command");
    console.log("    init\t\tCreate an empty lib folder");
    console.log("    install\t\tImport a package or file to /lib from a specific language library");
    console.log("    drop\t\tRemove the specified library package or files from /lib");
    process.exit(0);
}

switch (args[0]) {
    case "help":
        console.log("Usage: simp help [<command>]");
        break;
    case "init":
        console.log("Usage: simp init <language> [<dir>]");
        break;
    case "install":
        console.log("Usage: simp install (<file> | <package>)");
        console.log("aliases: simp i");
        break;
    case "drop":
        console.log("Usage: simp drop (<file> | <package>)");
        break;
    default:
        console.log("%c Error: help page not found", "color: #FF0000");
}