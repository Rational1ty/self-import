export const ctrl = {
	reset: '\x1b[0m',
	bright: '\x1b[1m',
	dim: '\x1b[2m',
	underscore: '\x1b[4m',
	blink: '\x1b[5m',
	reverse: '\x1b[7m',
	hidden: '\x1b[8m'
}

export const fg = {
	black: '\x1b[30m',
	red: '\x1b[31m',
	green: '\x1b[32m',
	yellow: '\x1b[33m',
	blue: '\x1b[34m',
	magenta: '\x1b[35m',
	cyan: '\x1b[36m',
	white: '\x1b[37m'
}

export const bg = {
	black: '\x1b[40m',
	red: '\x1b[41m',
	green: '\x1b[42m',
	yellow: '\x1b[43m',
	blue: '\x1b[44m',
	magenta: '\x1b[45m',
	cyan: '\x1b[46m',
	white: '\x1b[47m'
}

export function log(data: any, ...effects: string[]) {
	console.log(`${effects.join('')}${data}${ctrl.reset}`);
}

export function write(data: any, ...effects: string[]) {
	process.stdout.write(`${effects.join('')}${data}${ctrl.reset}`);
}

export function done(message: string) {
	write('DONE! ', fg.green, ctrl.bright);
	console.log(message);
}

export function warn(message: string) {
	write('WARN ', fg.yellow);
	console.log(message);
}

export function error(message: string) {
	write('ERROR ', fg.red, ctrl.bright);
	console.log(message);
}