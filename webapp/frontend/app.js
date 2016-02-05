(function() {
    var angular = require('angular');
    require('angular-animate');
    require('angular-aria');
    require('angular-material');
    require('angular-moment');
    var app = angular.module('app', ['ngMaterial', 'angularMoment']);
    app.controller('appcontroller', AppController);

    var setError = function(response) {
        console.log("Encountered error");
        this.error = response.data.errror;
    }

    function AppController($scope, $http) {
        var self = this;
        $scope.ctrl = this;

        this.$http = $http;

        this.message = "hello world";
        this.distances = null;
        this.position = null;
        this.messages = null;
        this.newMessage = null;

        navigator.geolocation.getCurrentPosition(function(position) {
            self.position = position;
            self.loadMessages();
        });

        $http.get('/api/distances').then(function(response) {
            console.log(response);
            self.distances = response.data.distances;
        }, setError.bind(this));
    }

    AppController.prototype.isLoaded = function() {
        return this.position != null && this.distances != null
               && this.messages != null;
    }

    AppController.prototype.submitMessage = function() {
        var payload = {
            message: this.newMessage,
            location: {
                longitude: this.position.coords.longitude,
                latitude: this.position.coords.latitude,
            }
        };
        this.$http.post('/api/post', payload,
            {headers: {'content-type':'application/json'}}).catch(setError.bind(this));
        console.log("submit");
        console.log(payload);
        this.newMessage = null;
    };

    AppController.prototype.loadMessages = function () {
        var self = this;
        var params = '?longitude=' + this.position.coords.longitude +
                     '&latitude=' + this.position.coords.latitude;

        this.$http.get('/api/post' + params).then(function(response) {
            console.log(response.data);
            self.messages = response.data;
        }, setError.bind(this));
    };
})();
