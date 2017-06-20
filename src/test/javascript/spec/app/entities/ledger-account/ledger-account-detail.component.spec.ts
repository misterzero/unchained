import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { UnchainedTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { LedgerAccountDetailComponent } from '../../../../../../main/webapp/app/entities/ledger-account/ledger-account-detail.component';
import { LedgerAccountService } from '../../../../../../main/webapp/app/entities/ledger-account/ledger-account.service';
import { LedgerAccount } from '../../../../../../main/webapp/app/entities/ledger-account/ledger-account.model';

describe('Component Tests', () => {

    describe('LedgerAccount Management Detail Component', () => {
        let comp: LedgerAccountDetailComponent;
        let fixture: ComponentFixture<LedgerAccountDetailComponent>;
        let service: LedgerAccountService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [UnchainedTestModule],
                declarations: [LedgerAccountDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    LedgerAccountService,
                    EventManager
                ]
            }).overrideTemplate(LedgerAccountDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(LedgerAccountDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LedgerAccountService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new LedgerAccount(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.ledgerAccount).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
