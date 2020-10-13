import fs from 'fs'
import * as cp from 'child_process'
import path from 'path'
import Colors from './console_colors'

const rootDir = path.resolve(__dirname.replace(/bin/, ''))

const moduleDirectories = cp.execSync(
	'dir /b', {
		cwd: `${rootDir}/modules`,
		shell: process.env.ComSpec
	}
)

const env = {
	root: rootDir,
	languages: moduleDirectories.toString('utf-8').split(/\r\n/).filter(s => s)
}

fs.writeFile(
	`${rootDir}/src/env.json`,
	JSON.stringify(env, null, '\t'),
	'utf-8',
	(err) => {
		if (err) {
			Colors.error(err.message)
			process.exit(9)
		} else {
			console.log(`Updated ${rootDir}\\src\\env.json`)
		}
	}
)

fs.writeFile(
	`${rootDir}/bin/env.json`,
	JSON.stringify(env, null, '\t'),
	'utf-8',
	(err) => {
		if (err) {
			Colors.error(err.message)
			process.exit(9)
		} else {
			console.log(`Updated ${rootDir}\\bin\\env.json`)
		}
	}
)