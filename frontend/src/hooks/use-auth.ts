import { createContext, useContext } from "react";
import type {
	User,
	dtoLogin,
	dtoRegister,
	dtoVerifyEmail,
	dtoPasswordRequestReset,
	dtoPasswordReset,
} from "@/lib/types";

export type AuthContextValue = {
	user: User | null;
	accessToken: string | null;
	isAuthenticated: boolean;
	isLoading: boolean;
	register: (data: dtoRegister) => Promise<string>;
	login: (
		provider: "credentials" | "google",
		data?: dtoLogin
	) => Promise<string>;
	resendVerificationEmail: () => Promise<string>;
	verifyEmail: (data: dtoVerifyEmail) => Promise<string>;
	passwordRequestReset: (data: dtoPasswordRequestReset) => Promise<string>;
	passwordReset: (data: dtoPasswordReset) => Promise<string>;
	logout: () => Promise<string>;
};

export const AuthContext = createContext<AuthContextValue | undefined>(
	undefined
);

export function useAuth(): AuthContextValue {
	const context = useContext(AuthContext);

	if (context === undefined) {
		throw new Error("useAuth must be used within an AuthProvider");
	}

	return context;
}
