/*global define */

'use strict';

define(['angular'], function(angular) {

    /* Services */
    var mod = angular.module('myApp.services', []);

    mod.value('version', '0.1');

    mod.factory('ProjectService', [
        '$http', '$rootScope',
        function ProjectService ($http, $rootScope) {
            var currentProjectTimeEntries = [];
            var _callback;

            // WebSocket connection stuff
            var ws = new WebSocket("ws://localhost:9000/observe");
            ws.onopen = function(event){
                console.log("Socket has been opened!");
                console.log(event);
            };
            ws.onclose = function(event){
                console.log("Socket has been closed!");
                console.log(event);
            };
            ws.onerror = function(event){
                console.log("An error has occurred with the Socket");
                console.log(event);
            };
            ws.onmessage = function(event) {
                console.log("Received event from Socket!");
                console.log(event);
                handleObservedTimeEntries(JSON.parse(event.data));
            };

            var handleObservedTimeEntries = function handleObservedTimeEntries (entryList) {
                console.log("handling entry list");
                console.log(entryList);
                $rootScope.$apply(function () {
                    var timeEntries = entryList.map(function fixDatesForTimeEntries (entry) {
                        entry.entryDate = new Date(entry.entryDate);
                        return entry;
                    });
                    if (typeof _callback == 'function') {
                        _callback(timeEntries);
                    }
                });
            };

            var Service = {
                getProjectList: function getProjectList () {
                    return $http.get('/project')
                    .success(function getProjectListSuccess (data, status) {
                        console.log("Successfully got project list");
                        console.log({data:data, status:status});
                    })
                    .error(function getProjectListError (data, status) {
                        console.log("Error while trying to get project list");
                        console.log({data:data, status:status});
                    });
                },
                listenForNewProjectTimeEntries: function listenAndStuff (projectName, callback) {
                    _callback = callback;
                    ws.send(projectName);
                }
            };

            return Service;

        }]);

    mod.factory('TimeEntryService', [
        '$http', '$q', '$rootScope', 'ProjectService', 
        function TimeEntryService ($http, $q, $rootScope, ProjectService) {
            var _timeEntries = [];

            var Service = {
                timeEntries: _timeEntries,
                create: function createTimeEntry () {
                    return {
                        name: '',
                        project_name: '',
                        _entryDate: new Date(),
                        hours: 8
                    };
                },
                save: function saveTimeEntry (entry) {
                    $http.put('/time', entry)
                    .success(function saveTimeEntrySuccess (data, status, headers, config) {
                        console.log("SUCCESS! Saved time entry to server");
                        console.log({data:data, status:status, headers:headers, config:config});
                        _timeEntries.push(entry);
                    })
                    .error(function saveTimeEntryError (data, status, headers, config) {
                        console.log("Yo, dude... Problems...");
                        console.log({data:data, status:status, headers:headers, config:config});
                    });
                },
                getProjectList: function getProjectList () {
                    var projectList = {};
                    for (var i = 0; i < this.timeEntryList.length; i++) {
                        projectList[this.timeEntryList[i].project] = 1;
                    }
                    return projectList.keys();
                }
            };

            return Service;

        }]);

});
