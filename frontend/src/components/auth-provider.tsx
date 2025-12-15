// TODO: all the mutations used here are a global state.
// TODO: if you request a password change, go back to login, and request it again, the mutation is still successfull.
// TODO: the auth actions need to be moved elsewhere.

import { api } from "@/api/api-client";
import type {
	dtoPasswordRequestReset,
	dtoPasswordReset,
	LoginForm,
	RegisterForm,
	User,
} from "@/lib/types";
import {
	useMutation,
	useQuery,
	type UseMutationResult,
} from "@tanstack/react-query";
import { AxiosError } from "axios";
import { jwtDecode } from "jwt-decode";
import {
	createContext,
	useCallback,
	useContext,
	useLayoutEffect,
	useMemo,
	useState,
	type PropsWithChildren,
} from "react";
import { useNavigate } from "react-router-dom";

type JwtPayload = {
	jti: string;
	sub: string;
	id: string;
	name: string;
	emailVerified: boolean;
	profilePicture?: string;
	iat: number;
	exp: number;
};

type AuthResponse = {
	accessToken: string;
};

type AuthContextValue = {
	user: User | null;
	accessToken: string | null;
	isAuthenticated: boolean;
	isLoading: boolean;
	register: UseMutationResult<string, Error, RegisterForm>;
	login: UseMutationResult<string, Error, LoginForm>;
	resendVerificationEmail: UseMutationResult<string, Error, void>;
	verifyEmail: (data: string) => Promise<string>;
	passwordRequestReset: UseMutationResult<
		string,
		Error,
		dtoPasswordRequestReset
	>;
	passwordReset: UseMutationResult<string, Error, dtoPasswordReset>;
	logout: UseMutationResult<string, Error, void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

type AuthProviderProps = PropsWithChildren;

export default function AuthProvider({ children }: AuthProviderProps) {
	const navigate = useNavigate();

	const [user, setUser] = useState<User | null>(null);
	const [accessToken, setAccessToken] = useState<string | null>(null);

	const saveAccessToken = useCallback((token: string | null) => {
		if (!token) {
			setAccessToken(null);
			setUser(null);
			return;
		}

		const claims = jwtDecode<JwtPayload>(token);

		const decodedUser: User = {
			id: parseInt(claims.id),
			email: claims.sub,
			name: claims.name,
			emailVerified: claims.emailVerified,
			profilePicture: claims.profilePicture ?? null,
		};

		setAccessToken(token);
		setUser(decodedUser);
	}, []);

	const { isPending: isLoading } = useQuery({
		queryKey: ["refresh"],
		queryFn: async () => {
			try {
				const response = await api.post<AuthResponse>("/auth/refresh");
				saveAccessToken(response.data.accessToken);
			} catch {
				saveAccessToken(null);
			}
			return null;
		},
		refetchOnWindowFocus: false,
		gcTime: 0,
	});

	useLayoutEffect(() => {
		const authInterceptor = api.interceptors.request.use((config) => {
			if (!config._retry && accessToken) {
				config.headers.Authorization = `Bearer ${accessToken}`;
			}
			return config;
		});

		return () => api.interceptors.request.eject(authInterceptor);
	}, [accessToken]);

	useLayoutEffect(() => {
		const refreshInterceptor = api.interceptors.response.use(
			(response) => response,
			async (error) => {
				const originalRequest = error.config;

				const isAuthCall =
					originalRequest?.url?.includes("/auth/register") ||
					originalRequest?.url?.includes("/auth/login-credentials") ||
					originalRequest?.url?.includes("/auth/refresh") ||
					originalRequest?.url?.includes("/auth/password");

				if (
					error.response.status !== 401 ||
					originalRequest._retry ||
					isAuthCall
				) {
					return Promise.reject(error);
				}

				originalRequest._retry = true;

				try {
					const response = await api.post("/auth/refresh");
					saveAccessToken(response.data.accessToken);
					originalRequest.headers.Authorization = `Bearer ${response.data.accessToken}`;
					return api(originalRequest);
				} catch {
					saveAccessToken(null);
					return Promise.reject(error);
				}
			}
		);

		return () => api.interceptors.response.eject(refreshInterceptor);
	}, [saveAccessToken]);

	const register = useMutation({
		retry: false,
		mutationFn: async (data: RegisterForm) => {
			try {
				const response = await api.post<AuthResponse>(
					"/auth/register",
					data
				);
				saveAccessToken(response.data.accessToken);

				return "Successfully registered!";
			} catch (error: unknown) {
				saveAccessToken(null);

				if (error instanceof AxiosError) {
					if (error.status === 409) {
						throw new Error("Email already in use");
					}
				}

				throw new Error("Something went wrong!");
			}
		},
	});

	const login = useMutation({
		retry: false,
		mutationFn: async (credentials: LoginForm) => {
			try {
				const response = await api.post<AuthResponse>(
					"/auth/login-credentials",
					credentials
				);
				saveAccessToken(response.data.accessToken);

				return "Successfully logged in!";
			} catch (error: unknown) {
				saveAccessToken(null);

				if (error instanceof AxiosError) {
					if (error.status === 401) {
						throw new Error("Invalid email or password");
					}
				}

				throw new Error("Something went wrong!");
			}
		},
	});

	const resendVerificationEmail = useMutation({
		retry: false,
		mutationFn: async () => {
			try {
				await api.post("/auth/email/resend");

				return "Successfully sent verification email!";
			} catch {
				throw new Error("Something went wrong!");
			}
		},
	});

	const verifyEmail = useCallback(async (token: string) => {
		try {
			await new Promise((res) => setTimeout(res, 2000));

			await api.post("/auth/email/verify", { token });

			setUser((prev) => (prev ? { ...prev, emailVerified: true } : prev));

			return "Successfully verified your account. We're sending you to your profile.";
		} catch (error: unknown) {
			if (error instanceof AxiosError) {
				if (error.status === 400) {
					throw new Error(
						"The verification link is invalid. Please make sure your heading to the link we sent you."
					);
				}
			}

			throw new Error("Something went wrong. Please try again later.");
		}
	}, []);

	const passwordRequestReset = useMutation({
		retry: false,
		mutationFn: async (request: dtoPasswordRequestReset) => {
			try {
				await api.post("/auth/password/request-reset", request);

				return "Successfully sent password reset email!";
			} catch {
				throw new Error("Something went wrong!");
			}
		},
	});

	const passwordReset = useMutation({
		retry: false,
		mutationFn: async (request: dtoPasswordReset) => {
			try {
				await api.post("/auth/password/reset", request);

				return "Successfully changed your password! Head back and login with your credentials.";
			} catch (error: unknown) {
				if (error instanceof AxiosError) {
					const errStatus = error.status;
					if (errStatus === 400) {
						throw new Error("Invalid password reset link.");
					}
				}

				throw new Error("Something went wrong!");
			}
		},
	});

	const logout = useMutation({
		retry: false,
		mutationFn: async () => {
			try {
				await api.post("/auth/logout");

				return "Successfully logged out!";
			} catch {
				throw new Error("Something went wrong!");
			} finally {
				saveAccessToken(null);
				navigate("/login", { replace: true });
			}
		},
	});

	const isAuthenticated = !!user;

	const value = useMemo<AuthContextValue>(
		() => ({
			user,
			accessToken,
			isAuthenticated,
			isLoading,
			register,
			login,
			resendVerificationEmail,
			verifyEmail,
			passwordRequestReset,
			passwordReset,
			logout,
		}),
		[
			user,
			accessToken,
			isAuthenticated,
			isLoading,
			register,
			login,
			resendVerificationEmail,
			verifyEmail,
			passwordRequestReset,
			passwordReset,
			logout,
		]
	);

	return (
		<AuthContext.Provider value={value}>{children}</AuthContext.Provider>
	);
}

export function useAuth(): AuthContextValue {
	const context = useContext(AuthContext);

	if (context === undefined) {
		throw new Error("useAuth must be used within an AuthProvider");
	}

	return context;
}
