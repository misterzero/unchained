import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AlertService, EventManager } from 'ng-jhipster';

import { LedgerAccount } from './ledger-account.model';
import { LedgerAccountPopupService } from './ledger-account-popup.service';
import { LedgerAccountService } from './ledger-account.service';

@Component({
    selector: 'jhi-ledger-account-delete-dialog',
    templateUrl: './ledger-account-delete-dialog.component.html'
})
export class LedgerAccountDeleteDialogComponent {

    ledgerAccount: LedgerAccount;

    constructor(
        private ledgerAccountService: LedgerAccountService,
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private eventManager: EventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.ledgerAccountService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'ledgerAccountListModification',
                content: 'Deleted an ledgerAccount'
            });
            this.activeModal.dismiss(true);
        });
        this.alertService.success('unchainedApp.ledgerAccount.deleted', { param : id }, null);
    }
}

@Component({
    selector: 'jhi-ledger-account-delete-popup',
    template: ''
})
export class LedgerAccountDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private ledgerAccountPopupService: LedgerAccountPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.ledgerAccountPopupService
                .open(LedgerAccountDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
