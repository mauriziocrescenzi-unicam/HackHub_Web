export interface StaffResponse {
    idStaff: number;
    organizerId: number;
    organizerEmail: string;
    judgeId: number;    
    judgeEmail: string;
    mentors: {
        idAccount: number;
        email: string;
    }[];
}