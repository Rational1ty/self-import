import fs from 'fs';

const args = process.argv.slice(2);

fs.mkdir(`${args[0] ? args[0] : '.'}/lib`, { recursive: true }, err => {
    if (err) {
        console.error('Initialization failed:');
        console.error(err);
    }
});