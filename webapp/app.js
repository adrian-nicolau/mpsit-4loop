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

app.get('/api/post', routes.search);
app.get('/api/distances', routes.distances);

function broadcastNewPost(req, res, next) {
    io.emit('newmsg', { for: 'everyone' });
    next();
}
app.post('/api/post', broadcastNewPost, routes.postMessage);


server.listen(3000, function() {
    console.log('App listening on port 3000');
});
