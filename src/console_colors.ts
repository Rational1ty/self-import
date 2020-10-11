export default abstract class Colors {
	static ctrl = {
		reset: '\x1b[0m',
		bright: '\x1b[1m',
		dim: '\x1b[2m',
		underscore: '\x1b[4m',
		blink: '\x1b[5m',
		reverse: '\x1b[7m',
		hidden: '\x1b[8m'
	}

	static fg = {
		black: '\x1b[30m',
		red: '\x1b[31m',
		green: '\x1b[32m',
		yellow: '\x1b[33m',
		blue: '\x1b[34m',
		magenta: '\x1b[35m',
		cyan: '\x1b[36m',
		white: '\x1b[37m'
	}

	static bg = {
		black: '\x1b[40m',
		red: '\x1b[41m',
		green: '\x1b[42m',
		yellow: '\x1b[43m',
		blue: '\x1b[44m',
		magenta: '\x1b[45m',
		cyan: '\x1b[46m',
		white: '\x1b[47m'
	}

	static log(data: any, ...effects: string[]) {
		console.log(`${effects.join('')}${data}${this.ctrl.reset}`);
	}

	static write(data: any, ...effects: string[]) {
		process.stdout.write(`${effects.join('')}${data}${this.ctrl.reset}`);
	}

	static warn(message: string) {
		this.write('WARN ', this.fg.yellow);
		console.log(message);
	}

	static error(message: string) {
		this.write('ERROR ', this.ctrl.bright, this.fg.red);
		console.log(message);
	}
}