import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { LedgerAccount } from './ledger-account.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class LedgerAccountService {

    private resourceUrl = 'api/ledger-accounts';

    constructor(private http: Http) { }

    create(ledgerAccount: LedgerAccount): Observable<LedgerAccount> {
        const copy = this.convert(ledgerAccount);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(ledgerAccount: LedgerAccount): Observable<LedgerAccount> {
        const copy = this.convert(ledgerAccount);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<LedgerAccount> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convert(ledgerAccount: LedgerAccount): LedgerAccount {
        const copy: LedgerAccount = Object.assign({}, ledgerAccount);
        return copy;
    }
}
