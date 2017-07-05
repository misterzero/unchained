import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { BlockchainUserComponent } from './blockchain-user.component';
import { BlockchainUserDetailComponent } from './blockchain-user-detail.component';
import { BlockchainUserPopupComponent } from './blockchain-user-dialog.component';
import { BlockchainUserDeletePopupComponent } from './blockchain-user-delete-dialog.component';

import { Principal } from '../../shared';

export const blockchainUserRoute: Routes = [
    {
        path: 'blockchain-user',
        component: BlockchainUserComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.blockchainUser.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'blockchain-user/:id',
        component: BlockchainUserDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.blockchainUser.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const blockchainUserPopupRoute: Routes = [
    {
        path: 'blockchain-user-new',
        component: BlockchainUserPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.blockchainUser.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'blockchain-user/:id/edit',
        component: BlockchainUserPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.blockchainUser.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'blockchain-user/:id/delete',
        component: BlockchainUserDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.blockchainUser.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
