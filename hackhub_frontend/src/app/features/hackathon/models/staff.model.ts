import { Account } from "../../account/models/account.model";

export interface Staff {
    idStaff: number;
    organizer: Account;
    judge: Account;
    mentors: Account[];
}