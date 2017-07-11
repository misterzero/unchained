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

    showOptions() {
        console.log('showOptions');
        console.log(this.poll.options);
        let json : Options = JSON.parse(this.poll.options);
        console.log('Length: ' + Object.keys(json).length);
        console.log('Json name:' + json[0].name);
        console.log('Json name:' + json[1].name);
        for(let i=0;i<Object.keys(json).length;i++) {
            console.log(json[i].name);
            this.options.push(json[i].name);
        }
        console.log('Options:' + this.options);
    }
    load(id) {
        this.pollService.find(id).subscribe((poll) => {
            this.poll = poll;
            this.showOptions();
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
}

interface Options {
    string: Option;
}
interface Option {
    name: string;
    count: number;
}
