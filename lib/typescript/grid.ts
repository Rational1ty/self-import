export default class Grid<T> {
    protected grid: T[][];

    constructor(protected size: number, initial?: T[][]) {
        if (initial) {
            this.grid = initial;
        } else {
            this.grid = new Array(size).fill(new Array(size));
        }
    }
}