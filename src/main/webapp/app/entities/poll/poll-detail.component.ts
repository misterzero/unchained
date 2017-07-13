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
    options: Option[];

    constructor(
        private eventManager: EventManager,
        private pollService: PollService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.options = [];
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInPolls();
    }

    showOptions() {
        console.log('showOptions');
        console.log(this.poll.options);
        const json: Options = JSON.parse(this.poll.options);
        console.log('Length: ' + Object.keys(json).length);
        for (let i = 0; i < Object.keys(json).length; i++) {
            console.log(json[i].name);
            this.options.push(json[i].name);
        }
    }

    vote(option) {
        const ballot: string[] = ['"user"', '"' + this.poll.name + '"', '"' + option + '""'];
        console.log('Vote for poll: ' + this.poll.name);
        // ballot.push('user');
        // ballot.push(this.poll.name);
        // ballot.push(option);
        console.log(ballot.join());
        this.subscribeToVoteResponse(this.pollService.vote(ballot.join()), true);
        // this.pollService.vote(ballot.join());
        // this.subscription = this.route.params.subscribe((params) => {
        //     this.pollService.vote(ballot);
        // });
        // this.registerChangeInPollVote(ballot);
        // this.pollService.vote(ballot);
    }

    // private subscribeToVoteResponse(cast: Observable<string>) {
    //   cast.subscribe((res: string) => {
    //     this.pollService.vote(res);
    //   });
    // }

    private subscribeToVoteResponse(result: Observable<string>, isCreated: boolean) {
        result.subscribe((res: Poll) =>
            this.onSaveSuccess(res, isCreated), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Poll, isCreated: boolean) {
        console.log('Save success');

        this.eventManager.broadcast({ name: 'pollListModification', content: 'OK'});
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.onError(error);
    }

    private onError(error) {
      console.log('Error on save');
    }

    load(id) {
        this.pollService.find(id).subscribe((poll) => {
            this.poll = poll;
            let array = JSON.parse(poll.options);
            console.log('POLL LOG');
            this.options=array;
            console.log(poll)
        });
    }
    previousState() {
        window.history.back();
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

    registerChangeInPollVote(ballot) {
        this.eventSubscriber = this.eventManager.subscribe(
            'pollVoteModification',
            (response) => this.vote(ballot)
        );
    }
}
