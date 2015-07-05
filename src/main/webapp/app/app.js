'use strict';
// Here we set up an angular module. We'll attach controllers and
// other components to this module.
angular.module('app', ['ngRoute', 'ngResource', 'ui.bootstrap', 'MainController'])
    // Angular supports chaining, so here we chain the config function onto
    // the module we're configuring.
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/views/messages',
                controller: 'MainController'
            })
            .otherwise({
                redirectTo: '/'
            });

    });
