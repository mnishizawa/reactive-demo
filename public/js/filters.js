/*global define */

'use strict';

define(['angular'], function(angular) {

    /* Filters */
    var mod = angular.module('myApp.filters', []);

    mod.filter('interpolate', ['version', function(version) {
        return function(text) {
            return String(text).replace(/\%VERSION\%/mg, version);
        }
    }]);

    mod.filter('sumProjectHours', [function sumProjectHoursFilter () {
        return function (timeEntries) {
            return timeEntries.reduce(function (accum, entry) {
                return accum + entry.hours;
            }, 0);
        };
    }]);

});
