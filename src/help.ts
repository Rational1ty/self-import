import Colors from './console_colors'
import * as commands from './command_utils'

const args = process.argv.slice(2)

if (args.length === 0) {
    console.log(`Usage: ${commands.obj.simp.usage}`)

    console.log('\nOptions:')
    console.log('    [-h | --help]\tView a general help page')
    console.log('    [-v | --version]\tDisplay current version number')

    console.log('\nCommands:')
    for (const cmd of commands.list) {
        console.log(`    ${cmd}\t\t${commands.obj[cmd].description}`)
    }
    process.exit(0)
}

if (args[0].match(commands.matcher())) {
    const name = commands.getCommandFromAlias(args[0])
    const command = commands.obj[name]
    console.log(`Usage: simp ${name} ${command.usage}`)
    console.log(command.description)
    if (command.aliases?.length) {
        console.log(`Aliases: ${command.aliases.join(', ')}`)
    }
} else {
    Colors.error('help page not found')
}