(function() {
    var angular = require('angular');
    var io = require('socket.io-client')();

    require('angular-animate');
    require('angular-aria');
    require('angular-material');
    require('angular-moment');
    var app = angular.module('app', ['ngMaterial', 'angularMoment']);
    app.controller('appcontroller', AppController);

    var setError = function(response) {
        console.log("Encountered error");
        this.showToast(response.data.error);
    };

    /**
     * AngularJS controller used for the site.
     * @constructor
     */
    function AppController($scope, $http, $document, $mdToast) {
        var self = this;
        $scope.ctrl = this;

        this.$http = $http;
        this.$mdToast = $mdToast;
        this.$document = $document[0];

        this.message = "hello world";
        this.distances = null;
        this.position = null;
        this.messages = null;
        this.newMessage = null;
        this.distanceIdx = null;

        $scope.$watch('ctrl.distanceIdx', function() {
            self.loadMessages();
        });

        if (Notification.permission !== 'denied') {
            Notification.requestPermission();
        }

        io.on('newmsg', function() {
            self.loadMessages();
            if ((self.$document).visibilityState != "visible") {
                var n = new Notification('4loop',{
                    tag: '4loop',
                    body: 'New post on 4loop.',
                    renotify: false,
                });
                setTimeout(n.close.bind(n), 1000);
            }
        });

        navigator.geolocation.getCurrentPosition(function(position) {
            self.position = position;
            self.loadMessages();
        });

        $http.get('/api/distances').then(function(response) {
            self.distances = response.data.distances;
            self.distanceIdx = self.distances.length / 2 | 0;
        }, setError.bind(this));
    }

    AppController.prototype.isLoaded = function() {
        return this.position !== null && this.distances !== null &&
               this.messages !== null;
    };

    AppController.prototype.submitMessage = function() {
        var self = this;
        var payload = {
            message: this.newMessage,
            location: {
                longitude: this.position.coords.longitude,
                latitude: this.position.coords.latitude,
            }
        };
        this.$http.post('/api/post', payload,
            {headers: {'content-type':'application/json'}}).then(
            function(response) { self.showToast('Posted!'); },
            setError.bind(this));
        console.log("submit");
        console.log(payload);
        this.newMessage = null;
    };

    AppController.prototype.loadMessages = function() {
        if (!this.position) {
            return;
        }
        var self = this;
        var dist = this.distances[this.distanceIdx - 1];
        var params = '?longitude=' + this.position.coords.longitude +
                     '&latitude=' + this.position.coords.latitude +
                     '&distance=' + dist;
        console.log(params);

        this.$http.get('/api/post' + params).then(function(response) {
            self.messages = response.data;
        }, setError.bind(this));
    };

    AppController.prototype.showToast = function(text) {
        this.$mdToast.show(this.$mdToast.simple().textContent(text)
                           .hideDelay(3000).position('top'));
    };
})();
