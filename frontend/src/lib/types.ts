export type RegisterForm = {
	name: string;
	email: string;
	password: string;
};

export type LoginForm = {
	email: string;
	password: string;
};

export type User = {
	id: number;
	email: string;
	emailVerified: boolean;
	name: string;
	profilePicture: string | null;
};

export type dtoPasswordRequestReset = {
	email: string;
};

export type dtoPasswordReset = {
	token: string;
	password: string;
};
