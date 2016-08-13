(function() {
    'use strict';

    angular
        .module('kudoflashApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('kudo', {
            parent: 'entity',
            url: '/kudo',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Kudos'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/kudo/kudos.html',
                    controller: 'KudoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('kudo-detail', {
            parent: 'entity',
            url: '/kudo/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Kudo'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/kudo/kudo-detail.html',
                    controller: 'KudoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Kudo', function($stateParams, Kudo) {
                    return Kudo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'kudo',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('kudo-detail.edit', {
            parent: 'kudo-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kudo/kudo-dialog.html',
                    controller: 'KudoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Kudo', function(Kudo) {
                            return Kudo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('kudo.new', {
            parent: 'kudo',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kudo/kudo-dialog.html',
                    controller: 'KudoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                text: null,
                                date: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('kudo', null, { reload: true });
                }, function() {
                    $state.go('kudo');
                });
            }]
        })
        .state('kudo.edit', {
            parent: 'kudo',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kudo/kudo-dialog.html',
                    controller: 'KudoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Kudo', function(Kudo) {
                            return Kudo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('kudo', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('kudo.delete', {
            parent: 'kudo',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/kudo/kudo-delete-dialog.html',
                    controller: 'KudoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Kudo', function(Kudo) {
                            return Kudo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('kudo', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
