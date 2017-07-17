import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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
    options: any[];

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
        this.pollService.delete(this.poll.id).subscribe();
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
