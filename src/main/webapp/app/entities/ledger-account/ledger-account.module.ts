import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { UnchainedSharedModule } from '../../shared';
import {
    LedgerAccountService,
    LedgerAccountPopupService,
    LedgerAccountComponent,
    LedgerAccountDetailComponent,
    LedgerAccountDialogComponent,
    LedgerAccountPopupComponent,
    LedgerAccountDeletePopupComponent,
    LedgerAccountDeleteDialogComponent,
    ledgerAccountRoute,
    ledgerAccountPopupRoute,
} from './';

const ENTITY_STATES = [
    ...ledgerAccountRoute,
    ...ledgerAccountPopupRoute,
];

@NgModule({
    imports: [
        UnchainedSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        LedgerAccountComponent,
        LedgerAccountDetailComponent,
        LedgerAccountDialogComponent,
        LedgerAccountDeleteDialogComponent,
        LedgerAccountPopupComponent,
        LedgerAccountDeletePopupComponent,
    ],
    entryComponents: [
        LedgerAccountComponent,
        LedgerAccountDialogComponent,
        LedgerAccountPopupComponent,
        LedgerAccountDeleteDialogComponent,
        LedgerAccountDeletePopupComponent,
    ],
    providers: [
        LedgerAccountService,
        LedgerAccountPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class UnchainedLedgerAccountModule {}
