import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager  } from 'ng-jhipster';

import { BlockchainUser } from './blockchain-user.model';
import { BlockchainUserService } from './blockchain-user.service';

@Component({
    selector: 'jhi-blockchain-user-detail',
    templateUrl: './blockchain-user-detail.component.html'
})
export class BlockchainUserDetailComponent implements OnInit, OnDestroy {

    blockchainUser: BlockchainUser;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: EventManager,
        private blockchainUserService: BlockchainUserService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInBlockchainUsers();
    }

    load(id) {
        this.blockchainUserService.find(id).subscribe((blockchainUser) => {
            this.blockchainUser = blockchainUser;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInBlockchainUsers() {
        this.eventSubscriber = this.eventManager.subscribe(
            'blockchainUserListModification',
            (response) => this.load(this.blockchainUser.id)
        );
    }
}
