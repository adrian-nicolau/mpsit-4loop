var client = require('./elastic').client;

exports.search = function(req, res) {
    var longitude = req.query.longitude || 0;
    var latitude = req.query.latitude || 0;
    var query = req.query.q;
    var distance = req.query.distance || '500m';
    //TODO validate this input!
    var queryDict = {
        'query': {
           'filtered' : {
                'filter' : {
                    'geo_distance' : {
                        'distance' : distance,
                        'location' : {
                            'lat' : latitude,
                            'lon' : longitude
                        }
                    }
                }
            }
        }
    };
    if (query) {
        queryDict.query.filtered['query'] = {
            'match': {
                'message': query
            }
        };
    }
    client.search({
        index: 'posts',
        type: 'post',
        body: queryDict
    }, function(error, response) {
        if (error) {
            res.send(error);
        } else {
            res.send(response);
        }
    });
};

exports.postMessage = function(req, res) {
};

