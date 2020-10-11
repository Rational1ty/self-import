import commandsJSON from './commands.json';

export const obj: CommandList = commandsJSON;
export const list = Object.keys(obj).slice(1);

export interface CommandList {
	[key: string]: Command
}

export interface Command {
	"aliases"?: string[],
	"usage": string,
	"description": string
}

export function isValidCommand(command: string): boolean {
	return command.match(matcher()) ? true : false;
}

export function matcher(): RegExp {
	const commandOrAlias = list.map(key => [key].concat(obj[key].aliases ?? []))
							   .flat()
							   .join('|');
	return new RegExp(`\\b(${commandOrAlias})\\b`, 'i');
}

export function getCommandFromAlias(alias: string): string {
	if (alias in obj) return alias;

	for (const cmd of list) {
		for (const a of obj[cmd].aliases ?? []) {
			if (alias === a) return cmd;
		}
	}

	return alias;
}