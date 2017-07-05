import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { UnchainedLedgerAccountModule } from './ledger-account/ledger-account.module';
import { UnchainedPollModule } from './poll/poll.module';
import { UnchainedBlockchainUserModule } from './blockchain-user/blockchain-user.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        UnchainedLedgerAccountModule,
        UnchainedPollModule,
        UnchainedBlockchainUserModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class UnchainedEntityModule {}
