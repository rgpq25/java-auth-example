import GoogleIcon from "@/assets/google-icon.svg";
import { useAuth } from "@/components/auth-provider";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { useMutation } from "@tanstack/react-query";
import { AlertCircle, Loader2 } from "lucide-react";
import { useState } from "react";
import { Link, Navigate } from "react-router-dom";

type LoginForm = {
	email: string;
	password: string;
};

export function Login() {
	const { isAuthenticated, isLoading, login } = useAuth();

	const [loginForm, setLoginForm] = useState<LoginForm>({
		email: "",
		password: "",
	});

	const loginMutation = useMutation({
		mutationFn: login,
	});

	async function onSignUpWithGoogle() {
		return;
	}

	if (isLoading) return null;

	if (isAuthenticated === true) return <Navigate to="/profile" replace />;

	return (
		<div className="flex h-dvh">
			<div className="w-1/2 h-full bg-secondary"></div>
			<div className="w-1/2 h-full flex">
				<div className="m-auto flex flex-col gap-4 w-xs">
					<header>
						<h1 className="text-2xl font-semibold text-center">
							Welcome back!
						</h1>
						<p className="text-muted-foreground text-center">
							Sign in to your account
						</p>
					</header>
					<section className="space-y-2">
						<InputField
							field="email"
							placeholder="youremail@hotmail.com"
							value={loginForm}
							onChange={setLoginForm}
						/>
						<InputField
							field="password"
							placeholder="verySecurePassword"
							value={loginForm}
							onChange={setLoginForm}
						/>
					</section>
					{loginMutation.error !== null && (
						<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
							<AlertCircle className="size-5 stroke-red-500 stroke-2" />
							<p className="text-sm text-red-500">
								{loginMutation.error.message}
							</p>
						</div>
					)}
					<Button
						className="flex w-full items-center justify-center"
						onClick={() => loginMutation.mutate(loginForm)}
						disabled={loginMutation.isPending}
					>
						{loginMutation.isPending ? (
							<Loader2 className="size-4 animate-spin" />
						) : null}
						Login
					</Button>
					<section className="flex items-center gap-2">
						<Separator
							orientation="horizontal"
							className="flex-1"
						/>
						<span className="shrink-0 text-muted-foreground leading-3">
							or
						</span>
						<Separator
							orientation="horizontal"
							className="flex-1"
						/>
					</section>
					<Button
						variant="outline"
						className="items-center flex"
						onClick={onSignUpWithGoogle}
					>
						<img
							src={GoogleIcon}
							alt="Google Icon"
							className="size-4"
						/>
						Sign in with Google
					</Button>
					<section>
						<p className="text-sm text-center text-muted-foreground">
							Don't have an account?{" "}
							<Link
								className="hover:underline font-medium text-primary cursor-pointer"
								to={"/register"}
							>
								Sign up
							</Link>
						</p>
					</section>
				</div>
			</div>
		</div>
	);
}

function InputField({
	field,
	placeholder,
	value,
	onChange,
}: {
	field: keyof LoginForm;
	placeholder: string;
	value: LoginForm;
	onChange: (val: LoginForm) => void;
}) {
	return (
		<div className="flex flex-col gap-1">
			<p className="text-sm font-semibold capitalize">{field}</p>
			<Input
				placeholder={placeholder}
				value={value[field]}
				onChange={(e) =>
					onChange({
						...value,
						[field]: e.target.value,
					})
				}
			/>
		</div>
	);
}
