/*global define */

'use strict';

define(['angular'], function(angular) {

    /* Controllers */
    var mod = angular.module('myApp.controllers', []);

    mod.controller('SiteNavController', [
        '$scope', '$location',
        function SiteNavController ($scope, $location) {
            $scope.siteNav = {
                isActive: function (route) {
                    return $location.path().indexOf(route) == 0;
                }
            };
        }]);

    mod.controller('ProjectController', [
        '$scope', '$routeParams', 'ProjectService',
        function ProjectController ($scope, $routeParams, ProjectService) {
            $scope.project = {};
            $scope.project.name = $routeParams.projectName;
            $scope.project.timeEntries = [];

            ProjectService.listenForNewProjectTimeEntries($scope.project.name, function (timeEntryList) {
                console.log("SERIOUSLY!!!! This is workign!!!!!!");
                console.log(timeEntryList);
                $scope.project.timeEntries = timeEntryList;
            });
        }]);

    mod.controller('ProjectListController', [
        '$scope', 'ProjectService',
        function ProjectListController ($scope, ProjectService) {
            $scope.projects = {
                list: []
            };

            ProjectService.getProjectList().success(function ctrlGetProjectlistSuccess (list) {
                console.log('Doing stuff with project list...');
                console.log(list);
                $scope.projects.list = list;
            });
        }]);

    mod.controller('TimeEntryController', [
        '$scope', '$filter', 'TimeEntryService', 
        function TimeEntryController ($scope, $filter, TimeEntryService) {
            $scope.timeEntry = {
                entry: TimeEntryService.create(),
                save: function ctrlSaveTimeEntry () {
                    console.log("CTRL: Attempting to save time entry");
                    TimeEntryService.save($scope.timeEntry.entry);
                    $scope.timeEntry.entry = TimeEntryService.create();
                }
            };

            $scope.$watch('timeEntry.entry._entryDate', function (newDate) {
                console.log('updating date stuff');
                $scope.timeEntry.entry.entryDate = $filter('date')(newDate, 'yyyy-MM-dd');
            });
        }]);

});
