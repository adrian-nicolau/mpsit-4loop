var express = require('express');
var morgan = require('morgan');
var bodyParser = require ('body-parser');

var routes = require('./routes');
var elastic = require('./elastic');

var app = express();

app.use(express.static('public'));
app.use(morgan('dev'));
app.use(bodyParser());

// Check index no matter what.
app.use(elastic.requireIndex);

app.get('/api/post', routes.search);
app.get('/api/distances', routes.distances);
app.post('/api/post', routes.postMessage);


app.listen(3000, function() {
    console.log('App listening on port 3000');
});
