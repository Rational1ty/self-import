import fs from 'fs';

const args = process.argv.slice(2);

const s = args[0].split('/');

type Language = 'java' | 'javascript' | 'typescript' | 'python';

console.log(`Installing ${process.argv.slice(2)[0]} ...`);