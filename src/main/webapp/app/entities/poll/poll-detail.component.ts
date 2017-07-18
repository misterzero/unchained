import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';

import { Subscription } from 'rxjs/Rx';
import { EventManager  } from 'ng-jhipster';

import { Poll } from './poll.model';
import { PollService } from './poll.service';

@Component({
    selector: 'jhi-poll-detail',
    templateUrl: './poll-detail.component.html'
})
export class PollDetailComponent implements OnInit, OnDestroy {

    poll: Poll;
    private subscription: Subscription;
    private eventSubscriber: Subscription;
    isVoting: Boolean;
    options: any[];

    constructor(
        private eventManager: EventManager,
        private pollService: PollService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.options = [];
        this.isVoting = false;
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInPolls();
    }

    vote(option) {
        this.isVoting = true;
        const ballot: string[] = [this.poll.id + '_' + this.poll.name, option];
        console.log('Vote for poll: ' + this.poll.name);
        console.log(ballot.join());
        this.subscribeToVoteResponse(this.pollService.vote(ballot.join()), true);
    }

    private subscribeToVoteResponse(result: Observable<string>, isCreated: boolean) {
        result.subscribe((res: Poll) =>
            this.onSaveSuccess(res, isCreated), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Poll, isCreated: boolean) {
        console.log('Save success');
        this.isVoting = false;
        this.eventManager.broadcast({ name: 'pollListModification', content: 'OK'});
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
          console.log('onSaveError');
            // error.message = error.;
        }
        this.onError(error);
        this.isVoting = false;
    }

    private onError(error) {
      console.log('onError');
    }

    load(id) {
        this.pollService.find(id).subscribe((poll) => {
            this.poll = poll;
            const array = JSON.parse(poll.options);
            this.options = array;
        });

    }
    previousState() {
        window.history.back();
    }

    close() {
        this.pollService.delete(this.poll.id + '_' + this.poll.name).subscribe();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInPolls() {
        this.eventSubscriber = this.eventManager.subscribe(
            'pollListModification',
            (response) => this.load(this.poll.id)
        );
    }
}
