export type dtoRegister = {
	name: string;
	email: string;
	password: string;
};

export type dtoLogin = {
	email: string;
	password: string;
};

export type dtoVerifyEmail = {
	token: string;
};

export type dtoPasswordRequestReset = {
	email: string;
};

export type dtoPasswordReset = {
	token: string;
	password: string;
};

export type User = {
	id: number;
	email: string;
	emailVerified: boolean;
	name: string;
	profilePicture: string | null;
};
