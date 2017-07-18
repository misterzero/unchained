export class Poll {
    constructor(
        public id?: number,
        public name?: string,
        public options?: string,
        public expiration?: any,
        public voters?: string,
        public owner?: string,
        public chainCodeName?: string,
    ) {
    }
}
