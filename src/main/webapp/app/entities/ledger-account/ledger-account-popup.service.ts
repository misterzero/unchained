import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { LedgerAccount } from './ledger-account.model';
import { LedgerAccountService } from './ledger-account.service';

@Injectable()
export class LedgerAccountPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private ledgerAccountService: LedgerAccountService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.ledgerAccountService.find(id).subscribe((ledgerAccount) => {
                this.ledgerAccountModalRef(component, ledgerAccount);
            });
        } else {
            return this.ledgerAccountModalRef(component, new LedgerAccount());
        }
    }

    ledgerAccountModalRef(component: Component, ledgerAccount: LedgerAccount): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.ledgerAccount = ledgerAccount;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
