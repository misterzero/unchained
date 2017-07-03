import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { BlockchainUser } from './blockchain-user.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class BlockchainUserService {

    private resourceUrl = 'api/blockchain-users';

    constructor(private http: Http) { }

    create(blockchainUser: BlockchainUser): Observable<BlockchainUser> {
        const copy = this.convert(blockchainUser);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(blockchainUser: BlockchainUser): Observable<BlockchainUser> {
        const copy = this.convert(blockchainUser);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<BlockchainUser> {
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

    private convert(blockchainUser: BlockchainUser): BlockchainUser {
        const copy: BlockchainUser = Object.assign({}, blockchainUser);
        return copy;
    }
}
