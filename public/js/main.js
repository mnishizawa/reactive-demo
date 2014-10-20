/*global require, requirejs */

'use strict';

requirejs.config({
  paths: {
    'angular': ['../lib/angularjs/angular'],
    'angular-route': ['../lib/angularjs/angular-route'],
    'ui-bootstrap': ['../lib/angular-ui-bootstrap/ui-bootstrap'],
    'ui-bootstrap-tpls': ['../lib/angular-ui-bootstrap/ui-bootstrap-tpls']
  },
  shim: {
    'angular': {
      exports : 'angular'
    },
    'angular-route': ['angular'],
    'ui-bootstrap': ['angular'],
    'ui-bootstrap-tpls': ['angular']
  }
});

require([
    'angular',
    'ui-bootstrap',
    'ui-bootstrap-tpls',
    './controllers',
    './directives',
    './filters',
    './services',
    'angular-route'
], function( angular ) {

    // Declare app level module which depends on filters, and services

    angular.module( 'myApp', [
        'myApp.controllers',
        'myApp.filters',
        'myApp.services',
        'myApp.directives',
        'ngRoute'
    ])

    .config( ['$routeProvider', function($routeProvider) {
        $routeProvider.when('/time-entry', {
            templateUrl: 'partials/time-entry.html',
            controller: 'TimeEntryController'
        });
        $routeProvider.when('/projects', {
            templateUrl: 'partials/project-list.html',
            controller: 'ProjectListController'
        });
        $routeProvider.when('/projects/:projectName', {
            templateUrl: 'partials/project.html',
            controller: 'ProjectController'
        });
        $routeProvider.otherwise({redirectTo: '/projects'});
    }]);

    angular.bootstrap(document, ['myApp']);

});
