(function() {
    'use strict';
    angular
        .module('kudoflashApp')
        .factory('Kudo', Kudo);

    Kudo.$inject = ['$resource', 'DateUtils'];

    function Kudo ($resource, DateUtils) {
        var resourceUrl =  'api/kudos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.date = DateUtils.convertDateTimeFromServer(data.date);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
