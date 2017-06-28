type option = {
    name: string;
    votes: number;
}
export class Poll {
    constructor(
        public id?: number,
        public name?: string,
        public options?: option[],
        public expiration?: any,
    ) {
    }
}
