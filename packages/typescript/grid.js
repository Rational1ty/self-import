"use strict";
exports.__esModule = true;
var Grid = /** @class */ (function () {
    function Grid(size) {
        this.size = size;
        this.grid = new Array(size).fill(new Array(size));
    }
    return Grid;
}());
exports["default"] = Grid;
