export interface Staff {
    id: number;
    name: string;
    email: string;
    role?: 'ORGANIZER' | 'JUDGE' | 'MENTOR'; 
}
