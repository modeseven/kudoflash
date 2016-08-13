(function() {
    'use strict';

    angular
        .module('kudoflashApp')
        .controller('KudoDialogController', KudoDialogController);

    KudoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Kudo', 'User'];

    function KudoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Kudo, User) {
        var vm = this;

        vm.kudo = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.kudo.id !== null) {
                Kudo.update(vm.kudo, onSaveSuccess, onSaveError);
            } else {
                Kudo.save(vm.kudo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('kudoflashApp:kudoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
