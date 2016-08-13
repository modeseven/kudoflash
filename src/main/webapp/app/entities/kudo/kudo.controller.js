(function() {
    'use strict';

    angular
        .module('kudoflashApp')
        .controller('KudoController', KudoController);

    KudoController.$inject = ['$scope', '$state', 'Kudo'];

    function KudoController ($scope, $state, Kudo) {
        var vm = this;
        
        vm.kudos = [];

        loadAll();

        function loadAll() {
            Kudo.query(function(result) {
                vm.kudos = result;
            });
        }
    }
})();
