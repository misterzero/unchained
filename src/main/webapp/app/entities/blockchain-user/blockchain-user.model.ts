export class BlockchainUser {
    constructor(
        public id?: number,
        public name?: string,
        public activePolls?: string,
        public inactivePolls?: string,
    ) {
    }
}
