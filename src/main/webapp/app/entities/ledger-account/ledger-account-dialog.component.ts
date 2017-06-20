import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService } from 'ng-jhipster';

import { LedgerAccount } from './ledger-account.model';
import { LedgerAccountPopupService } from './ledger-account-popup.service';
import { LedgerAccountService } from './ledger-account.service';

@Component({
    selector: 'jhi-ledger-account-dialog',
    templateUrl: './ledger-account-dialog.component.html'
})
export class LedgerAccountDialogComponent implements OnInit {

    ledgerAccount: LedgerAccount;
    authorities: any[];
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private ledgerAccountService: LedgerAccountService,
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
        if (this.ledgerAccount.id !== undefined) {
            this.subscribeToSaveResponse(
                this.ledgerAccountService.update(this.ledgerAccount), false);
        } else {
            this.subscribeToSaveResponse(
                this.ledgerAccountService.create(this.ledgerAccount), true);
        }
    }

    private subscribeToSaveResponse(result: Observable<LedgerAccount>, isCreated: boolean) {
        result.subscribe((res: LedgerAccount) =>
            this.onSaveSuccess(res, isCreated), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: LedgerAccount, isCreated: boolean) {
        this.alertService.success(
            isCreated ? 'unchainedApp.ledgerAccount.created'
            : 'unchainedApp.ledgerAccount.updated',
            { param : result.id }, null);

        this.eventManager.broadcast({ name: 'ledgerAccountListModification', content: 'OK'});
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
    selector: 'jhi-ledger-account-popup',
    template: ''
})
export class LedgerAccountPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private ledgerAccountPopupService: LedgerAccountPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.ledgerAccountPopupService
                    .open(LedgerAccountDialogComponent, params['id']);
            } else {
                this.modalRef = this.ledgerAccountPopupService
                    .open(LedgerAccountDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
