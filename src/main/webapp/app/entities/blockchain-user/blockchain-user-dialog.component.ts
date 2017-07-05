import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService } from 'ng-jhipster';

import { BlockchainUser } from './blockchain-user.model';
import { BlockchainUserPopupService } from './blockchain-user-popup.service';
import { BlockchainUserService } from './blockchain-user.service';

@Component({
    selector: 'jhi-blockchain-user-dialog',
    templateUrl: './blockchain-user-dialog.component.html'
})
export class BlockchainUserDialogComponent implements OnInit {

    blockchainUser: BlockchainUser;
    authorities: any[];
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private blockchainUserService: BlockchainUserService,
        private eventManager: EventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.blockchainUser.id !== undefined) {
            this.subscribeToSaveResponse(
                this.blockchainUserService.update(this.blockchainUser), false);
        } else {
            this.subscribeToSaveResponse(
                this.blockchainUserService.create(this.blockchainUser), true);
        }
    }

    private subscribeToSaveResponse(result: Observable<BlockchainUser>, isCreated: boolean) {
        result.subscribe((res: BlockchainUser) =>
            this.onSaveSuccess(res, isCreated), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: BlockchainUser, isCreated: boolean) {
        this.alertService.success(
            isCreated ? 'unchainedApp.blockchainUser.created'
            : 'unchainedApp.blockchainUser.updated',
            { param : result.id }, null);

        this.eventManager.broadcast({ name: 'blockchainUserListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-blockchain-user-popup',
    template: ''
})
export class BlockchainUserPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private blockchainUserPopupService: BlockchainUserPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.blockchainUserPopupService
                    .open(BlockchainUserDialogComponent, params['id']);
            } else {
                this.modalRef = this.blockchainUserPopupService
                    .open(BlockchainUserDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
