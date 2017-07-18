import {Poll} from "./poll.model";
import {BlockchainUser} from "../blockchain-user/blockchain-user.model";
export class BlockchainDTO {
    constructor(
        public poll?: Poll,
        public user?: BlockchainUser,
    ) {
    }
}
