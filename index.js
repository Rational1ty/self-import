#!/usr/bin/env node
"use strict";

const cp = require('child_process');

const args = process.argv.slice(2);

cp.fork(`./${args[0]}.js`, [args.slice(1)], {
    cwd: process.cwd()
});