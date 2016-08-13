(function() {
    'use strict';

    angular
        .module('kudoflashApp')
        .controller('KudoDeleteController',KudoDeleteController);

    KudoDeleteController.$inject = ['$uibModalInstance', 'entity', 'Kudo'];

    function KudoDeleteController($uibModalInstance, entity, Kudo) {
        var vm = this;

        vm.kudo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Kudo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
