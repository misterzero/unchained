import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager, ParseLinks, PaginationUtil, JhiLanguageService, AlertService } from 'ng-jhipster';

import { BlockchainUser } from './blockchain-user.model';
import { BlockchainUserService } from './blockchain-user.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-blockchain-user',
    templateUrl: './blockchain-user.component.html'
})
export class BlockchainUserComponent implements OnInit, OnDestroy {
blockchainUsers: BlockchainUser[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private blockchainUserService: BlockchainUserService,
        private alertService: AlertService,
        private eventManager: EventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.blockchainUserService.query().subscribe(
            (res: ResponseWrapper) => {
                this.blockchainUsers = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInBlockchainUsers();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: BlockchainUser) {
        return item.id;
    }
    registerChangeInBlockchainUsers() {
        this.eventSubscriber = this.eventManager.subscribe('blockchainUserListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
