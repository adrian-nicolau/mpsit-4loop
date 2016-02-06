var elasticsearch = require('elasticsearch');
var client = new elasticsearch.Client({
    host: 'localhost:9200',
    log: 'trace'
});

var INDEX = 'posts';
var MAPPING = {
    index: INDEX,
    body: {
        mappings: {
            post: {
                properties: {
                    message: {
                        type: 'string'
                    },
                    location: {
                        type: 'geo_point'
                    },
                    date: {
                        type: 'date'
                    }
                }
            }
        }
    }
};


/** Route that doesn't let requests pass unless the correct ES index exists. */
exports.requireIndex = function(req, res, next) {
    client.indices.exists({
        index: INDEX,
    }, function(error, exists) {
        if (exists === false) {
            client.indices.create(MAPPING).then(function() {
                next();
            },
            function() {
                res.send('ERROR OCCURED!');
            });
        } else {
            next();
        }
    });
};

exports.client = client;
