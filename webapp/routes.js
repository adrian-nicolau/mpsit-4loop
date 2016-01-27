var client = require('./elastic').client;

exports.search = function(req, res) {
    res.send('ok');
};

exports.postMessage = function(req, res) {
    res.send('{}');
};

