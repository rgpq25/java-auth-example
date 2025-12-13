import GoogleIcon from "@/assets/google-icon.svg";
import AuthLoading from "@/components/auth-loading";
import { useAuth } from "@/components/auth-provider";
import AuthWrapper from "@/components/auth-wrapper";
import { Button } from "@/components/ui/button";
import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import {
	Field,
	FieldDescription,
	FieldGroup,
	FieldLabel,
	FieldSeparator,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import type { LoginForm } from "@/lib/types";
import { AlertCircle, GalleryVerticalEnd, Loader2 } from "lucide-react";
import { useState } from "react";
import { Link, Navigate } from "react-router-dom";

export function Login() {
	const { isAuthenticated, isLoading, login } = useAuth();

	const [form, setForm] = useState<LoginForm>({
		email: "",
		password: "",
	});

	const handleChange =
		(field: keyof LoginForm) =>
		(event: React.ChangeEvent<HTMLInputElement>) => {
			setForm((prev) => ({
				...prev,
				[field]: event.target.value,
			}));
		};

	const handleLogin = () => {
		login.mutate(form);
	};

	if (isLoading) return <AuthLoading />;

	if (isAuthenticated === true) return <Navigate to="/profile" replace />;

	return (
		<AuthWrapper>
			<div className="flex w-full max-w-sm flex-col gap-6">
				<a
					href="#"
					className="flex items-center gap-2 self-center font-medium"
				>
					<div className="bg-primary text-primary-foreground flex size-6 items-center justify-center rounded-md">
						<GalleryVerticalEnd className="size-4" />
					</div>
					Java Auth Example
				</a>
				<div className={"flex flex-col gap-6"}>
					<Card className="gap-4">
						<CardHeader className="text-center">
							<CardTitle className="text-xl">
								Welcome back
							</CardTitle>
							<CardDescription>
								Login with your Google account
							</CardDescription>
						</CardHeader>
						<CardContent>
							<FieldGroup className="gap-6">
								<Field>
									<Button
										variant="outline"
										type="button"
										className="items-center flex"
									>
										<img
											src={GoogleIcon}
											alt="Google Icon"
											className="size-4"
										/>
										Login with Google
									</Button>
								</Field>
								<FieldSeparator className="*:data-[slot=field-separator-content]:bg-card">
									Or continue with
								</FieldSeparator>
								<Field>
									<FieldLabel htmlFor="email">
										Email
									</FieldLabel>
									<Input
										id="email"
										type="email"
										placeholder="m@example.com"
										required
										value={form.email}
										onChange={handleChange("email")}
									/>
								</Field>
								<Field>
									<div className="flex items-center">
										<FieldLabel htmlFor="password">
											Password
										</FieldLabel>
										<Link
											to={"/password/request-reset"}
											className="ml-auto text-sm underline-offset-4 hover:underline"
										>
											Forgot your password?
										</Link>
									</div>
									<Input
										id="password"
										type="password"
										placeholder="safePassword"
										required
										value={form.password}
										onChange={handleChange("password")}
									/>
								</Field>
								{login.error && (
									<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
										<AlertCircle className="size-5 stroke-red-500 stroke-2" />
										<p className="text-sm text-red-500">
											{login.error.message}
										</p>
									</div>
								)}
								<Field className="flex flex-col gap-3">
									<Button
										type="button"
										onClick={handleLogin}
										disabled={login.isPending}
									>
										{login.isPending ? (
											<Loader2 className="size-4 animate-spin" />
										) : null}
										Login
									</Button>
									<FieldDescription className="text-center">
										Don&apos;t have an account?{" "}
										<Link to={"/register"}>Sign up</Link>
									</FieldDescription>
								</Field>
							</FieldGroup>
						</CardContent>
					</Card>
				</div>
			</div>
		</AuthWrapper>
	);
}
