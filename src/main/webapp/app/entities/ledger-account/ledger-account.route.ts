import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { LedgerAccountComponent } from './ledger-account.component';
import { LedgerAccountDetailComponent } from './ledger-account-detail.component';
import { LedgerAccountPopupComponent } from './ledger-account-dialog.component';
import { LedgerAccountDeletePopupComponent } from './ledger-account-delete-dialog.component';

import { Principal } from '../../shared';

export const ledgerAccountRoute: Routes = [
    {
        path: 'ledger-account',
        component: LedgerAccountComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.ledgerAccount.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'ledger-account/:id',
        component: LedgerAccountDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.ledgerAccount.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const ledgerAccountPopupRoute: Routes = [
    {
        path: 'ledger-account-new',
        component: LedgerAccountPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.ledgerAccount.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'ledger-account/:id/edit',
        component: LedgerAccountPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.ledgerAccount.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'ledger-account/:id/delete',
        component: LedgerAccountDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'unchainedApp.ledgerAccount.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
