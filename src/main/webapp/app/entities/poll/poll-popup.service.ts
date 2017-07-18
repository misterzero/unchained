import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Poll } from './poll.model';
import { PollService } from './poll.service';

@Injectable()
export class PollPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private pollService: PollService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.pollService.find(id).subscribe((blockchainDTO) => {
                if (blockchainDTO.poll.expiration) {
                    blockchainDTO.poll.expiration = {
                        year: blockchainDTO.poll.expiration.getFullYear(),
                        month: blockchainDTO.poll.expiration.getMonth() + 1,
                        day: blockchainDTO.poll.expiration.getDate()
                    };
                }
                this.pollModalRef(component, blockchainDTO.poll);
            });
        } else {
            return this.pollModalRef(component, new Poll());
        }
    }

    pollModalRef(component: Component, poll: Poll): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.poll = poll;
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
