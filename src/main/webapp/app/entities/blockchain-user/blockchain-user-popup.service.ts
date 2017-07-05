import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { BlockchainUser } from './blockchain-user.model';
import { BlockchainUserService } from './blockchain-user.service';

@Injectable()
export class BlockchainUserPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private blockchainUserService: BlockchainUserService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.blockchainUserService.find(id).subscribe((blockchainUser) => {
                this.blockchainUserModalRef(component, blockchainUser);
            });
        } else {
            return this.blockchainUserModalRef(component, new BlockchainUser());
        }
    }

    blockchainUserModalRef(component: Component, blockchainUser: BlockchainUser): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.blockchainUser = blockchainUser;
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
