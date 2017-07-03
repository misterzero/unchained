import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';

import { BlockchainUser } from './blockchain-user.model';
import { BlockchainUserPopupService } from './blockchain-user-popup.service';
import { BlockchainUserService } from './blockchain-user.service';

@Component({
    selector: 'jhi-blockchain-user-delete-dialog',
    templateUrl: './blockchain-user-delete-dialog.component.html'
})
export class BlockchainUserDeleteDialogComponent {

    blockchainUser: BlockchainUser;

    constructor(
        private blockchainUserService: BlockchainUserService,
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private eventManager: EventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.blockchainUserService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'blockchainUserListModification',
                content: 'Deleted an blockchainUser'
            });
            this.activeModal.dismiss(true);
        });
        this.alertService.success('unchainedApp.blockchainUser.deleted', { param : id }, null);
    }
}

@Component({
    selector: 'jhi-blockchain-user-delete-popup',
    template: ''
})
export class BlockchainUserDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private blockchainUserPopupService: BlockchainUserPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.blockchainUserPopupService
                .open(BlockchainUserDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
