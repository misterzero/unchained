import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService } from 'ng-jhipster';

import { Poll } from './poll.model';
import { PollPopupService } from './poll-popup.service';
import { PollService } from './poll.service';
import { ITEMS_PER_PAGE, Principal, User, UserService, ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-poll-dialog',
    templateUrl: './poll-dialog.component.html'
})

export class PollDialogComponent implements OnInit {

    poll: Poll;
    authorities: any[];
    isSaving: boolean;
    expirationDp: any;
    options: any[];
    voters: any[];
    users: Voter[];

    constructor(
        private userService: UserService,
        public activeModal: NgbActiveModal,
        private alertService: AlertService,
        private pollService: PollService,
        private eventManager: EventManager
    ) {

    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.options = [{'id': 'option1', 'text': ''}];
        this.voters = [{'id': 'voter1', 'text': ''}];
        this.loadAll();
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
    	if(this)
        this.isSaving = true;
        this.setPollOptions();
        this.setPollVoters();

        if (this.poll.id !== undefined) {
            this.subscribeToSaveResponse(
                this.pollService.update(this.poll), false);
        } else {
            this.subscribeToSaveResponse(
                this.pollService.create(this.poll), true);
        }
    }

    addNewOption() {
        this.options.push({'id': 'option' + (this.options.length + 1), 'text': ''});
    }

    setPollOptions() {
        // Removes empty objects from array
        const options = this.options.filter(function(option){
            return option.text;
        });
        // Writes just the 'names' from the options object array into a csv string
        let arr = (Array.prototype.map.call(options, (s) => s.text).toString()).split(",");
        var set = new Set(arr);
        arr = Array.from(set);
        this.poll.options = arr.toString();
    }

    removeLastOption() {
        this.options.pop();
    }

    addNewVoter() {
        this.voters.push({'id': 'voter' + (this.voters.length + 1), 'text': ''});
    }

    setPollVoters() {
        const voters = this.voters.filter(function(vote){
            return vote.text;
        });
        // Writes just the 'names' from the options object array into a csv string
        this.poll.voters = Array.prototype.map.call(voters, (s) => s.text).toString();
    }

    removeLastVoter() {
        this.voters.pop();
    }

    private subscribeToSaveResponse(result: Observable<Poll>, isCreated: boolean) {
        result.subscribe((res: Poll) =>
            this.onSaveSuccess(res, isCreated), (res: Response) => this.onSaveError(res));
    }

    loadAll() {
        this.userService.queryByNameAndId().subscribe(
            (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    private onSuccess(data, headers) {
        this.users = data;
    }

    private onSaveSuccess(result: Poll, isCreated: boolean) {
        this.alertService.success(
            isCreated ? 'unchainedApp.poll.created'
            : 'unchainedApp.poll.updated',
            { param : result.id }, null);

        this.eventManager.broadcast({ name: 'pollListModification', content: 'OK'});
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
    selector: 'jhi-poll-popup',
    template: ''
})
export class PollPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private pollPopupService: PollPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.pollPopupService
                    .open(PollDialogComponent, params['id']);
            } else {
                this.modalRef = this.pollPopupService
                    .open(PollDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

export class Voter {
    public id?: any;
    public firstName?: string;
    public lastName?: string;

    constructor(
            id?: any,
            firstName?: string,
            lastName?: string,
    ) {
        this.id = id ? id : null;
        this.firstName = firstName ? firstName : null;
        this.lastName = lastName ? lastName : null;
    }
}
