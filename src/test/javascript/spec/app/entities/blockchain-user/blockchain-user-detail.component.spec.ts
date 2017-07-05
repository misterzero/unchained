import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { UnchainedTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { BlockchainUserDetailComponent } from '../../../../../../main/webapp/app/entities/blockchain-user/blockchain-user-detail.component';
import { BlockchainUserService } from '../../../../../../main/webapp/app/entities/blockchain-user/blockchain-user.service';
import { BlockchainUser } from '../../../../../../main/webapp/app/entities/blockchain-user/blockchain-user.model';

describe('Component Tests', () => {

    describe('BlockchainUser Management Detail Component', () => {
        let comp: BlockchainUserDetailComponent;
        let fixture: ComponentFixture<BlockchainUserDetailComponent>;
        let service: BlockchainUserService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [UnchainedTestModule],
                declarations: [BlockchainUserDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    BlockchainUserService,
                    EventManager
                ]
            }).overrideTemplate(BlockchainUserDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(BlockchainUserDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(BlockchainUserService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new BlockchainUser(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.blockchainUser).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
