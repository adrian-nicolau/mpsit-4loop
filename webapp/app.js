var express = require('express');
var morgan = require('morgan');
var bodyParser = require ('body-parser');
var routes = require('./routes');

var app = express();

app.use(express.static('public'));
app.use(morgan('dev'));
app.use(bodyParser());

app.get('/api/post', routes.search);
app.post('/api/post', routes.postMessage);


app.listen(3000, function() {
    console.log('App listening on port 3000')
});
