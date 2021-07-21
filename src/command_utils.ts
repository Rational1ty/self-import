import commandsJSON from './commands.json';

export const obj: CommandList = commandsJSON;
export const list = Object.keys(obj).slice(1);

export interface CommandList {
	[key: string]: Command;
}

export interface Command {
	usage: string;
	description: string;
	aliases?: string[];
}

export function isValidCommand(command: string): boolean {
	return matcher().test(command);
}

export function matcher(): RegExp {
	const commandOrAlias = list.map(key => [key].concat(obj[key].aliases ?? []))
							   .flat()
							   .join('|');
	return new RegExp(`^(${commandOrAlias})$`, 'i');
}

export function getCommandFromAlias(alias: string): string {
	if (alias in obj) return alias;

	for (const cmd of list) {
		if (obj[cmd].aliases?.includes(alias))
			return cmd;
	}

	return alias;
}