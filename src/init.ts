import fs from 'fs'
import Colors from './console_colors'

const args = process.argv.slice(2);

// if (args[0].match(/temp/))

fs.mkdir(`${process.cwd()}/${args[0] ?? '.'}/${'self-modules'}`, { recursive: true }, err => {
    if (err) {
        Colors.error(`Initialization failed: ${err.message}`)
    }
})