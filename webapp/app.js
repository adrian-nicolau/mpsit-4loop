var express = require('express');
var morgan = require('morgan');
var bodyParser = require ('body-parser');

var routes = require('./routes');
var elastic = require('./elastic');

var app = express();
var server = require('http').Server(app);
var io = require('socket.io')(server);

app.use(express.static('public'));
app.use(morgan('dev'));
app.use(bodyParser());

// Check index no matter what.
app.use(elastic.requireIndex);

/**
 * @api {get} /api/post?latitude=:lat&longitude=:lon&q=:q&distance:dist
 * Search Messages
 * @apiName Search
 * @apiGroup 4loop
 *
 * @apiDecription Search Messages around a Pin.
 *
 * @apiParam {Number} lat Latitude of pin
 * @apiParam {Number} lon Longitude of pin
 * @apiParam {Text} q Full Text Search Query
 * @apiParam {Text} dist Distance around pin. See /api/distances for the
 *                  allowed values
 *
 * @apiSuccess {json} all Forwareded JSON response in ElasticSearch format
 */
app.get('/api/post', routes.search);
/**
 * @api {get} /api/distances/ Get allowed distances
 * @apiName Distances
 * @apiGroup 4loop
 *
 * @apiDecription Get the allowed distances for the search endpoint
 *
 * @apiSuccess {json} all A dict with the correct distances
 * @apiSuccessExample Success-Response
 *      HTTP/1.1 200 OK
 *      {
 *          'distances': ['1km', '2km']
 *      }
 */
app.get('/api/distances', routes.distances);

/** Sends push notification stating that a new message was posted. */
function broadcastNewPost(req, res, next) {
    setTimeout(function() { io.emit('newmsg', { for: 'everyone' });}, 1000);
    next();
}
/**
 * @api {post} /api/post/
 * Post a new message
 * @apiName PostMessage
 * @apiGroup 4loop
 *
 * @apiDescription Post a new message.
 *
 * @apiHeaderExample {json} Request-Example:
 *  {
 *      "message": "A cool Message",
 *      "location": {
 *          "longitude": 42.000,
 *          "latitude": 36.600
 *      }
 *  }
 *
 * @apiSuccess {json} all forwareded JSON response in ElasticSearch format
 * @apiError POSTError Returned if the POST dict is incorrectly sent.
 *
 * @apiErrorExample Error-Response
 *      HTTP/1.1 400 ERROR
 *      {
 *          'error': 'Message Too Long'
 *      }
 *
 */
app.post('/api/post', broadcastNewPost, routes.postMessage);

server.listen(3000, function() {
    console.log('App listening on port 3000');
});
