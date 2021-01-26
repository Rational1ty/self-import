import fs from 'fs';

const args = process.argv.slice(2);

const s = args[0].split('/');

console.log(`Installing ${args[0]} ...`);