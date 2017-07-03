import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { UnchainedSharedModule } from '../../shared';
import {
    BlockchainUserService,
    BlockchainUserPopupService,
    BlockchainUserComponent,
    BlockchainUserDetailComponent,
    BlockchainUserDialogComponent,
    BlockchainUserPopupComponent,
    BlockchainUserDeletePopupComponent,
    BlockchainUserDeleteDialogComponent,
    blockchainUserRoute,
    blockchainUserPopupRoute,
} from './';

const ENTITY_STATES = [
    ...blockchainUserRoute,
    ...blockchainUserPopupRoute,
];

@NgModule({
    imports: [
        UnchainedSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        BlockchainUserComponent,
        BlockchainUserDetailComponent,
        BlockchainUserDialogComponent,
        BlockchainUserDeleteDialogComponent,
        BlockchainUserPopupComponent,
        BlockchainUserDeletePopupComponent,
    ],
    entryComponents: [
        BlockchainUserComponent,
        BlockchainUserDialogComponent,
        BlockchainUserPopupComponent,
        BlockchainUserDeleteDialogComponent,
        BlockchainUserDeletePopupComponent,
    ],
    providers: [
        BlockchainUserService,
        BlockchainUserPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class UnchainedBlockchainUserModule {}
