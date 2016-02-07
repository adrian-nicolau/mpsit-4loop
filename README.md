# mpsit-4loop
Project @ Managementul Proiectelor și Serviciilor Software, Fall 2015

## Team
* Cătălin BADEA, IA2, Web frontend
* Mihai BIVOL, IA2, Node.js backend
* Adrian NICOLAU, SRIC2, Android client

## Project Description
Our goal is to write an anonymous Twitter-like app with location-awareness.
That is, a simple app which allows users to post "thoughts" that are only
visible to other users within, let's say, 50 meters. Users are anonymous, at
login they will receive an unique ID and this ID will be used throughout the app
for thoughts posting or discovery. The web frontend and Android app will be
comprised of two views, one for the user profile and one for the map discovery.
Both will exercise location APIs.

Catchphrase: *keeping you in the **loop**!*

## Howto

* Install vagrant
* Install Virtualbox
* Run ```vagrant up``` and wait a while
* ```vagrant ssh```
* ```service elasticsearch start``` If not already started
* ```cd /webapp/```
* ```npm install``` If not already installed stuff
* ```grunt```
* ```cp settings.example.json settings.json```
* ```add the correct API key
* ```nodejs app.js```
* Ports are forwarded so go to http://localhost:8080 in your machine

## Dev Cycle

* ```grunt watch:builder``` # Builds all requirements from frontend app to public/bundle.js
* ```nodejs app.js``` In another tab
* TODO autoreload with grunt command

Note:
Code directory is shared so edit *locally* and restart app in vagrant.
