export default class IllegalArgumentError extends Error {
	constructor(msg?: string) {
		super(msg);
		this.name = 'IllegalArgumentError';
	}
}