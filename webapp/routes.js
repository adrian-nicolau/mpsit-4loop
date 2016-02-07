var client = require('./elastic').client;
var geocoderProvider = 'google';
var httpsAdapter = 'https';
var extra = {
    apiKey: require('./settings').googleApi,
    formatter: null
};

var geocoder = require('node-geocoder')(geocoderProvider, httpsAdapter, extra);

var maxLen = 200;
var minLen = 10;
var distances = ['50m', '100m', '500m', '1km', '2km', '5km'];

/** Send the allowed distances for a distance search. */
exports.distances = function(req, res) {
    res.send({distances: distances});
};


exports.location_name = function(req, res) {
    var longitude = req.query.longitude || '0.0';
    var latitude = req.query.latitude || '0.0';
}

/**
 * Send the messages that match the request params:
 *
 *  - Longitude: pin longitude;
 *  - Latitude: pin latitude;
 *  - Distance: Maximum distance around pin (haversine);
 *    See {@link distances} for the allowed ones.
 *  - Query: Full Text Search query.
 */
exports.search = function(req, res) {
    var longitude = req.query.longitude || '0.0';
    var latitude = req.query.latitude || '0.0';
    var query = req.query.q;
    var distance = req.query.distance || '500m';

    // Latitude and longitude will be parsed by ElasticSearch and dealt with
    // accordingly.
    if (distances.indexOf(distance) == -1) {
        res.status(400).send({error: 'bad distance'});
        return;
    }

    var queryDict = {
        'sort': [{date: {order: 'desc'}}],
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
        query = query.substring(0, maxLen);
        queryDict.query.filtered.query = {
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

/**
 * Stores a message sent as a payload with the given format:
 * {message: messageText,
 * location: {longitude: lon,
 *            latitude: lat}}
 * NOTE Must send application/json content type header.
 */
exports.postMessage = function(req, res) {
    var msg = req.body.message;
    var loc = req.body.location;
    console.log(req.body);
    if (!msg) {
        res.status(400).send({error: 'Can not add an empty message!'});
        return;
    }
    if (msg.length < minLen) {
        res.status(400).send({error: 'Message too short!'});
        return;
    }
    if (!loc) {
        res.status(400).send({error: 'We need a location!'});
        return;
    }
    var lat = loc.latitude;
    if (!lat || Math.abs(parseFloat(lat)) > 90) {
        res.status(400).send({error: 'Bad latitude'});
        return;
    }
    var lon = loc.longitude;
    if (!lon || Math.abs(parseFloat(lon)) > 180) {
        res.status(400).send({error: 'Bad longitude'});
        return;
    }

    var indexDict = {
        index: 'posts',
        type: 'post',
        body: {
            message: msg.substring(0, maxLen),
            location : {
                    lat: parseFloat(lat),
                    lon: parseFloat(lon),
                },
            date: new Date()
        }
    };

    geocoder.reverse({lat: lat, lon: lon})
    .then(function(loc) {
        indexDict.body.locationName = loc[0].formattedAddress;
        console.log(loc[0].formattedAddress);
        console.log(loc);
    })
    .then(function() {
        client.index(indexDict,
                     function(err, response) { res.send(response); });
    });
};
