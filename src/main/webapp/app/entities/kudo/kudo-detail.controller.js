(function() {
    'use strict';

    angular
        .module('kudoflashApp')
        .controller('KudoDetailController', KudoDetailController);

    KudoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Kudo', 'User'];

    function KudoDetailController($scope, $rootScope, $stateParams, previousState, entity, Kudo, User) {
        var vm = this;

        vm.kudo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('kudoflashApp:kudoUpdate', function(event, result) {
            vm.kudo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
