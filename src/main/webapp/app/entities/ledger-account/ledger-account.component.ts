import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager, ParseLinks, PaginationUtil, JhiLanguageService, AlertService } from 'ng-jhipster';

import { LedgerAccount } from './ledger-account.model';
import { LedgerAccountService } from './ledger-account.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-ledger-account',
    templateUrl: './ledger-account.component.html'
})
export class LedgerAccountComponent implements OnInit, OnDestroy {
ledgerAccounts: LedgerAccount[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private ledgerAccountService: LedgerAccountService,
        private alertService: AlertService,
        private eventManager: EventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.ledgerAccountService.query().subscribe(
            (res: ResponseWrapper) => {
                this.ledgerAccounts = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInLedgerAccounts();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: LedgerAccount) {
        return item.id;
    }
    registerChangeInLedgerAccounts() {
        this.eventSubscriber = this.eventManager.subscribe('ledgerAccountListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
