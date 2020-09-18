"use strict";

const fs = require('fs');

fs.mkdir('./lib', { recursive: true }, err => {
    if (err) {
        console.error('Initialization failed:');
        console.error(err);
    }
});